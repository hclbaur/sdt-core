package be.baur.sdt.transform;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sdt.SDT;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>ForEachStatement</code> evaluates an XPath expression, iterates the
 * resulting node set and executes a compound statement on each iteration. If
 * present, one or more {@code SortStatement}s are applied to order the selected
 * node-set prior to iteration. An optional GROUP attribute specified a grouping
 * key expression, that is used to group nodes with the same key together.
 * 
 * @see SortStatement
 */
public class ForEachStatement extends XPathStatement {

	private String groupExpression; // expression for the grouping key
	
	
	/**
	 * Creates a ForEachStatement.
	 * 
	 * @param xpath the XPath to be evaluated, not null
	 */
	public ForEachStatement(XPath xpath) {
		super(xpath);
	}


	/**
	 * Sets the XPath expression that determines the keys by which nodes are
	 * grouped. If no expression was set, no grouping should occur.
	 * 
	 * @param xpath an XPath object, not null
	 */
	public void setGroupExpression(XPath xpath) {
		groupExpression = Objects.requireNonNull(xpath, "xpath must not be null").toString();
	}


	/**
	 * Returns the XPath expression text that determines the keys by which nodes are
	 * grouped, or null if no expression has been set.
	 * 
	 * @return an expression string, may be null
	 */
	public String getGroupExpression() {
		return groupExpression;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context and evaluate it to obtain a node-set. Optionally sort the set, then
		 * execute the compound statement for every node in that set. If a grouping key
		 * expression has been set, nodes with the same keys are grouped together in
		 * node-sets, and the compound statement is executed for each node-set.
		 */
		List<Node> statements = nodes();
		if (statements.isEmpty()) return; // nothing to do

		try {

			// select the node-set to be iterated
			XPath xpath = new SDAXPath(getExpression());
			xpath.setVariableContext(staco);
			List nodeset = xpath.selectNodes(staco.getXPathContext());
			final int setsize = nodeset.size();
			if (setsize == 0) return; // do nothing
			
			/*
			 * Optionally sort the node-set prior to iteration. If we have at least 2 nodes
			 * we create a comparator from all sort statements (if any) and sort if needed.
			 */
			if (setsize > 1) {

				Comparator<Object> comparator = null;
				for (Node statement : statements) {
					if (statement instanceof SortStatement) {
						SortStatement sortstat = (SortStatement) statement;
						if (comparator == null) // new comparator
							comparator = sortstat.getComparator(staco);
						else // add "if-equals" comparator for subsequent sort statement(s)
							comparator = comparator.thenComparing(sortstat.getComparator(staco));
					}
				}

				if (comparator != null)
					nodeset.sort(comparator);
			}

			/*
			 * Optionally group nodes in the set. Nodes that share the same grouping key are
			 * collected in a new node-set, associated with a particular key.
			 */
			Map<String, List> groups = null;
			if (groupExpression != null) {

				SDAXPath groupxp = new SDAXPath(groupExpression);
				groupxp.setVariableContext(staco);
				groups = new LinkedHashMap<String,List>();
				
				for (Object node : nodeset) {
					String key = groupxp.stringValueOf(node);
					groups.computeIfAbsent(key, k -> new ArrayList()).add(node);
				}
			}
			
			StatementContext coco = staco.newChild(); // compound statement context
			
			/*
			 * If no groups have been created, a regular iteration is performed. On every
			 * iteration the context node and the automatic variables $last, $current and
			 * $position are (re)set, prior to execution of the compound statement.
			 */
			int position = 0; 		
			if (groups == null) {
				coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "last", new Double(setsize));
				for (Object node : nodeset) {
					++position;	coco.setContextNode(node);
					coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "current", node);
					coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "position", new Double(position));

					for (Node statement : statements)
						((Statement) statement).execute(traco, coco);
				}
			}
			/*
			 * Otherwise, the groups are iterated, and the automatic variables $last,
			 * $current-group, $current-grouping-key and $position are (re)set prior to
			 * execution of the compound statement.
			 */
			else {
				coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "last", new Double(groups.size()));
				for (String key : groups.keySet()) {
					List group = groups.get(key);
					++position;	coco.setContextNode(group);
					coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "current-group", group);
					coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "current-grouping-key", key);
					coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "position", new Double(position));

					for (Node statement : statements)
						((Statement) statement).execute(traco, coco);
				}
			}
		
		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}
	
	
	/**
	 * @return a data node representing:<br><br>
	 *         <code>foreach "<i>expression</i>" { <i>statement+</i> }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Keyword.FOREACH.tag, getExpression());
		node.add(null); // render compound statement, even if empty
		if (groupExpression != null) // add group attribute
			node.add(new DataNode(Keyword.GROUP.tag, groupExpression));
		for (Node statement : nodes()) // add any child statements
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

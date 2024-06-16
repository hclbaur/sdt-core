package be.baur.sdt.transform;

import java.util.Comparator;
import java.util.List;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sdt.SDT;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>ForEachStatement</code> evaluates an XPath expression, iterates the
 * resulting node set and executes a compound statement on each iteration. If
 * present, one or more {@code SortStatement}s are applied to order the selected
 * node-set prior to iteration.
 * 
 * @see SortStatement
 */
public class ForEachStatement extends XPathStatement {

	/**
	 * Creates a ForEachStatement.
	 * 
	 * @param xpath the XPath to be evaluated, not null
	 */
	public ForEachStatement(SDAXPath xpath) {
		super(xpath);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context and evaluate it to obtain a node-set. Optionally sort the set, then
		 * execute the compound statement for every node in that set. On every iteration
		 * the context node and the automatic variables $last, $current and $position
		 * are (re)set.
		 */
		List<Node> statements = nodes();
		if (statements.isEmpty()) return; // nothing to do

		try {
			
			SDAXPath xpath = new SDAXPath(getExpression());
			xpath.setVariableContext(staco);
			
			List nodeset = xpath.selectNodes(staco.getContextNode());
			int setsize = nodeset.size();
			if (setsize == 0) return; // do nothing
			
			// Breaks if expression does not return a List of nodes, must fix this later!
			// For example test this with the results of a tokenize (if we have one)
			
			/*
			 * Optionally sort the node-set prior to iteration. If we have at least 2 nodes
			 * we create a comparator from all sort statements (if any) and sort if needed.
			 */
			if (setsize > 1) {

				Comparator<DataNode> comparator = null;
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


			StatementContext coco = staco.newChild(); // compound statement context
			coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "last", new Double(setsize));
			
			int position = 0;
			for (Object current : nodeset) {
			
				++position; coco.setContextNode(current);
				coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "current", current);
				coco.setVariableValue(SDT.FUNCTIONS_NS_URI, "position", new Double(position));
				
				for (Node statement : statements) {
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
		DataNode node = new DataNode(Statements.FOREACH.tag, getExpression());
		node.add(null); // render compound statement, even if empty
		for (Node statement : nodes()) // add any child statements
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

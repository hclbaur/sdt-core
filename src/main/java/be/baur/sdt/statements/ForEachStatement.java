package be.baur.sdt.statements;

import java.util.Comparator;
import java.util.List;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sdt.SDT;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.statements.SortStatement.NodeComparator;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>ForEachStatement</code> evaluates an XPath expression, iterates the
 * resulting node set and executes a compound statement on each iteration.
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
		 * context and evaluate it to obtain a node-set. Execute the compound statement
		 * for every node in that set. On every iteration the context node and the
		 * automatic variables $last, $current and $position are (re)set.
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
			
			// Optionally sort the node set prior to iteration
			if (setsize > 1 && statements.get(0) instanceof SortStatement) {

				SortStatement sortstat = (SortStatement) statements.get(0);
				SDAXPath sortxpath = new SDAXPath(sortstat.getExpression());
				sortxpath.setVariableContext(staco);
				
				boolean reverse = false;
				String revexp = sortstat.getReverseExpression();
				if (revexp != null) {
					SDAXPath revxpath = new SDAXPath(revexp);
					revxpath.setVariableContext(staco);
					reverse = revxpath.booleanValueOf(staco.getContextNode());
				}

				Comparator comp = new NodeComparator(sortxpath);
				nodeset.sort(reverse ? comp.reversed() : comp);
				
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
		for (Node statement : nodes()) // add child statements
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

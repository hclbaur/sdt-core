package be.baur.sdt.transform;

import java.util.List;

import be.baur.sda.Node;
import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>IfStatement</code> evaluates an XPath expression and executes a
 * compound statement if the boolean result is true.
 */
public class IfStatement extends XPathStatement {

	/**
	 * Creates an IfStatement.
	 * 
	 * @param xpath the XPath to be evaluated, not null
	 */
	public IfStatement(SDAXPath xpath) {
		super(xpath);
		add(null); // must have child statements so initialize it with an empty node set
	}


	@Override void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context and perform a Boolean evaluation. If the result is true, execute the
		 * compound statement, otherwise do nothing.
		 */
		List<Node> statements = nodes();
		if (statements.isEmpty()) return; // nothing to do

		try {
			SDAXPath xpath = new SDAXPath(getExpression());
			xpath.setVariableContext(staco);
			Boolean test = xpath.booleanValueOf(staco.getContextNode());

			if (! test) return; // do nothing
			
			StatementContext coco = staco.newChild();
			for (Node statement : statements) {
				((Statement) statement).execute(traco, coco);
			}
		
		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}

	
	/**
	 * @return a data node representing:<br><br>
	 *         <code>if "<i>expression</i>" { <i>statement+</i> }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Keyword.IF.tag, getExpression()); 
		node.add(null); // render compound statement, even if empty
		for (Node statement : nodes()) // add any child statements
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

package be.baur.sdt.transform;

import be.baur.sda.Node;
import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>WhenStatement</code> is a mandatory subordinate statement of the
 * {@code ChooseStatement} that evaluates an XPath expression and executes a
 * compound statement if the boolean result is true.
 */
public class WhenStatement extends XPathStatement {

	/**
	 * Creates an WhenStatement.
	 * 
	 * @param xpath the XPath to be evaluated, not null
	 */
	public WhenStatement(SDAXPath xpath) {
		super(xpath);
	}

	
	@Override void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * This method does nothing. Execution takes place in the context of the ChooseStatement.
		 */
	}

	
	/**
	 * @return a data node representing:<br><br>
	 *         <code>when "<i>expression</i>" { <i>statement+</i> }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Statements.WHEN.tag, getExpression()); 
		node.add(null); // render compound statement, even if empty
		for (Node statement : nodes()) // add any child statements
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

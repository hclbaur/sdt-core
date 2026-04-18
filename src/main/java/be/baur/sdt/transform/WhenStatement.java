package be.baur.sdt.transform;

import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;

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
	public WhenStatement(XPath xpath) {
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
		var node = new DataNode(Keyword.WHEN.tag, getExpression()); 
		node.expand(); // render compound statement, even if empty
		for (var statement : nodes()) // add any child statements
			node.add( statement.toSDA() );
		return node;
	}

}

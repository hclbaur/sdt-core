package be.baur.sdt.statements;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;
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
		super(Statements.IF.tag, xpath);
		add(null); // must have child statements so initialize it with an empty node set
	}


	@Override
	public void execute(TransformContext tracon, StatementContext stacon) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context and perform a Boolean evaluation. If the result is true, execute the
		 * compound statement, otherwise do nothing.
		 */
		NodeSet statements = getNodes();
		if (statements == null) return; // nothing to do

		try {
			SDAXPath xpath = new SDAXPath(getExpression());
			xpath.setVariableContext(stacon);
			Boolean test = xpath.booleanValueOf(stacon.getContextNode());

			if (! test) return; // do nothing
			
			StatementContext comcon = stacon.newChild();
			for (Node statement : statements) {
				((Statement) statement).execute(tracon, comcon);
			}
		
		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}

	
	/**
	 * @return an SDA node representing<br>
	 *         <code>if "<i>expression</i>" { <i>statement+</i> }</code>
	 */
	public Node toNode() {
		Node node = new Node(Statements.IF.tag, getExpression()); 
		for (Node statement : this.getNodes()) // // add child statements
			node.add(((Statement) statement).toNode());
		return node;
	}

}

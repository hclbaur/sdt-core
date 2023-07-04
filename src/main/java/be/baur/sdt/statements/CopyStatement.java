package be.baur.sdt.statements;

import java.util.List;

import be.baur.sda.Node;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Attribute;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>CopyStatement</code> evaluates an XPath expression and creates a
 * deep copy of the resulting node(s).
 */
public class CopyStatement extends XPathStatement {

	/**
	 * Creates a CopyStatement.
	 * 
	 * @param xpath the XPath to be evaluated, not null
	 */
	public CopyStatement(SDAXPath xpath) {
		super(Statements.COPY.tag, xpath);
	}


	/*
	 * Private helper method to create a deep copy of an SDA node. This functionality should
	 * be provided by the SDA core library in a future release.
	 */
	private static Node copy(Node node) {
		Node copy = new Node(node.getName(), node.getValue());
		if (node.isComplex()) {
			copy.add(null);
			for (Node child : node.getNodes()) 
				copy.add(copy(child));
		}
		return copy;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(TransformContext tracon, StatementContext stacon) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context and evaluate. If the result is a node(set), copy and add the node(s)
		 * to the current output node. Otherwise, do nothing.
		 */
		try {
			SDAXPath xpath = new SDAXPath(getExpression());
			xpath.setVariableContext(stacon);
			Object value = xpath.evaluate(stacon.getContextNode());

			if (!(value instanceof List)) return;
			
			for (Object object : (List) value) {
				if (object instanceof Node)
					stacon.getOutputNode().add(copy((Node) object));
			}

		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}


	/**
	 * @return an SDA node representing<br>
	 *         <code>copy { select "<i>expression</i>" }</code>
	 */
	public Node toNode() {
		Node node = new Node(Statements.COPY.tag, getValue());
		node.add( new Node(Attribute.SELECT.tag, getExpression()) ); 
		return node;
	}

}

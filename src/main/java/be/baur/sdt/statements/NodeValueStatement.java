package be.baur.sdt.statements;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SDA;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Attribute;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The {@code NodeValueStatement} creates a new node with the specified name and
 * evaluates an XPath expression to set the string value of the node. Any child
 * nodes can be created in the compound statement.
 * 
 * @see NodeStatement
 */
public class NodeValueStatement extends XPathStatement {

	/**
	 * Creates a {@code NodeValueStatement}.
	 * 
	 * @param name  the name of the new node, not null
	 * @param xpath the XPath to be evaluated, not null
	 * @throws IllegalArgumentException if the node name is invalid
	 */
	public NodeValueStatement(String name, SDAXPath xpath) {
		super(Statements.NODE.tag, xpath);
		if (! SDA.isName(name)) 
			throw new IllegalArgumentException("invalid node name '" + name + "'");
		setValue(name); // name is stored in the node value, bit icky
	}


	@Override
	public void execute(TransformContext tracon, StatementContext stacon) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context and perform a String evaluation. Use the result to create a new SDA
		 * node, and add it as a child to the current output node. Then, execute the
		 * compound statement with the new node set as the current output node to
		 * collect any child nodes created "downstream".
		 */
		try {
			SDAXPath xpath = new SDAXPath(getExpression()); 
			xpath.setVariableContext(stacon);
			String value = xpath.stringValueOf(stacon.getContextNode());
			
			Node newNode = new Node(getValue(), value); // icky :(
			stacon.getOutputNode().add(newNode);

			NodeSet statements = getNodes();
			if (statements == null) return; // nothing to do
			
			StatementContext comcon = stacon.newChild();
			comcon.setOutputNode(newNode);
			for (Node statement : statements) {
				((Statement) statement).execute(tracon, comcon);
			}

		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}


	/**
	 * @return a node representing<br>
	 *         <code>node "<i>name</i>" { value "<i>expression</i>" <i>statement*</i> }</code>
	 */
	public Node toNode() {
		Node node = new Node(Statements.NODE.tag, getValue());
		node.add(new Node(Attribute.VALUE.tag, getExpression()));
		final NodeSet nodes = this.getNodes();
		if (nodes != null)
			for (Node statement : nodes) // add child statements, if any
				node.add(((Statement) statement).toNode());
		return node;
	}

}

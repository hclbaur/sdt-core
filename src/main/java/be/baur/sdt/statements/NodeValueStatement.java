package be.baur.sdt.statements;

import java.util.List;
import java.util.Objects;

import be.baur.sda.Node;
import be.baur.sda.DataNode;
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
public class NodeValueStatement extends NodeStatement {

	private String expression;


	/**
	 * Creates a {@code NodeValueStatement}.
	 * 
	 * @param name  the name of the new node, not null
	 * @param xpath the XPath to be evaluated, not null
	 * @throws IllegalArgumentException if the node name is invalid
	 */
	public NodeValueStatement(String name, SDAXPath xpath) {
		super(name);
		expression = Objects.requireNonNull(xpath, "xpath must not be null").toString();
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
			SDAXPath xpath = new SDAXPath(expression); 
			xpath.setVariableContext(stacon);
			String value = xpath.stringValueOf(stacon.getContextNode());
			
			DataNode newNode = new DataNode(getNodeName(), value);
			stacon.getOutputNode().add(newNode);

			List<Node> statements = nodes();
			if (statements.isEmpty()) return; // nothing to do
			
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
	public DataNode toSDA() {
		DataNode node = new DataNode(Statements.NODE.tag, getNodeName());
		node.add(new DataNode(Attribute.VALUE.tag, expression));
		for (Node statement : nodes()) // add child statements, if any
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

package be.baur.sdt.statements;

import java.util.List;
import java.util.Objects;

import org.jaxen.JaxenException;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Attribute;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The {@code NodeStatement} creates a new node with the specified name and an
 * optional value from an evaluated XPath expression. Any child nodes can be
 * created by the compound statement.
 */
public class NodeStatement extends XPathStatement {
	
	/** The expression that evaluates to an empty string. */
	private static SDAXPath EMPTY = null;
	static {
		try { EMPTY = new SDAXPath("''");
		} catch (JaxenException e) { /* never happens */ }
	}
	
	private String nodeName; // name of the node created by this statement
	private final boolean withValue; // whether this node gets a value upon creation

	/**
	 * Creates a node statement without a value.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if name is invalid
	 */
	public NodeStatement(String name) {
		super(EMPTY); setNodeName(name); withValue = false;
	}

	
	/**
	 * Creates a node statement with a value from an XPath expression.
	 * 
	 * @param name  the name of the new node, not null
	 * @param xpath the XPath to be evaluated, not null
	 * @throws IllegalArgumentException if the node name is invalid
	 */
	public NodeStatement(String name, SDAXPath xpath) {
		super(xpath); setNodeName(name); withValue = true;
	}

	/**
	 * Returns the name of the node created by this statement.
	 * 
	 * @returns a node name, not null or empty
	 */
	public String getNodeName() {
		return nodeName;
	}


	/**
	 * Sets the name of the node created by this statement.
	 * 
	 * @param name a valid node name, see {@link SDA#isName}
	 * @throws IllegalArgumentException if name is invalid
	 */
	void setNodeName(String name) {
		Objects.requireNonNull(name, "name must not be null");
		if (!SDA.isName(name))
			throw new IllegalArgumentException("name '" + name + "' is invalid");
		nodeName = name;
	}


	@Override
	public void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: create a new SDA node, and add it to the current output node.
		 * Then, execute the compound statement with the new node set as the current
		 * output node to collect any child nodes created "downstream".
		 */
		try {
			
			String value = null;
			
			if (withValue) {
				SDAXPath xpath = new SDAXPath(getExpression());
				xpath.setVariableContext(staco);
				value = xpath.stringValueOf(staco.getContextNode());
			}
			
			DataNode newNode = new DataNode(nodeName, value);
			staco.getOutputNode().add(newNode);

			List<Statement> statements = nodes();
			if (statements.isEmpty()) return; // nothing to do
			
			StatementContext coco = staco.newChild();
			coco.setOutputNode(newNode);
			for (Statement statement : statements) {
				statement.execute(traco, coco);
			}

		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}


	/**
	 * @return a data node representing<br>
	 *         <code>node "<i>name</i>" { <i>statement*</i> }</code> or<br>
	 *         <code>node "<i>name</i>" { value "<i>expression</i>" <i>statement*</i> }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Statements.NODE.tag, nodeName);
		if (withValue)
			node.add(new DataNode(Attribute.VALUE.tag, getExpression()));
		for (Node statement : nodes()) // add child statements, if any
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

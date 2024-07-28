package be.baur.sdt.transform;

import java.util.List;
import java.util.Objects;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The {@code NodeStatement} creates a new node with the specified name and an
 * optional value from an evaluated XPath expression. Any child nodes can be
 * created by the compound statement.
 */
public class NodeStatement extends Statement {
	
	private String nodeName; // name of the node created by this statement
	private String valueExpression; // expression that sets the node value

	/**
	 * Creates a {@code NodeStatement} with the name of the node to create.
	 * 
	 * @param name a valid node name
	 * @throws IllegalArgumentException if name is invalid
	 */
	public NodeStatement(String name) {
		setNodeName(name);
	}


	/**
	 * Returns the name of the node created by this statement.
	 * 
	 * @return a node name, not null or empty
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


	/**
	 * Sets the XPath expression used for the value of the node created by this
	 * statement. If no expression is set, the node will have an empty value.
	 * 
	 * @param xpath an XPath object, not null
	 */
	public void setValueExpression(SDAXPath xpath) {
		valueExpression = Objects.requireNonNull(xpath, "xpath must not be null").toString();
	}


	/**
	 * Returns the XPath expression text used for the value of the node created by
	 * this statement. If no expression is set (is null), the node will have an
	 * empty value.
	 * 
	 * @return an expression string, may be null
	 */
	public String getValueExpression() {
		return valueExpression;
	}
	
	
	@Override 
	void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: create a new SDA node, and add it to the current output node.
		 * Then, execute the compound statement with the new node as the current output
		 * node to collect any child nodes created "downstream".
		 */
		try {
			
			String value = null;
			
			if (valueExpression != null) {
				SDAXPath xpath = new SDAXPath(valueExpression);
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
	 * @return a data node representing:<br><br>
	 *         <code>node "<i>name</i>" { <i>statement*</i> }</code> or<br>
	 *         <code>node "<i>name</i>" { value "<i>expression</i>" <i>statement*</i> }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Keyword.NODE.tag, nodeName);
		if (valueExpression != null)
			node.add(new DataNode(Keyword.VALUE.tag, valueExpression));
		else
			node.add(null);
		for (Node statement : nodes()) // add child statements, if any
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

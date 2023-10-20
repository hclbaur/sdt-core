package be.baur.sdt.statements;

import java.util.List;
import java.util.Objects;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sda.DataNode;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;

/**
 * The {@code NodeStatement} creates a new node with the specified name and an
 * empty value (and therefore no XPath expression needs to be evaluated). Any
 * child nodes can be created by the compound statement.
 * 
 * @see NodeValueStatement
 */
public class NodeStatement extends Statement {
	
	private String nodeName; // name of the node created by this statement


	/**
	 * Creates a {@code NodeStatement}.
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
	public void setNodeName(String name) {
		Objects.requireNonNull(name, "name must not be null");
		if (!SDA.isName(name))
			throw new IllegalArgumentException("name '" + name + "' is invalid");
		nodeName = name;
	}


	@Override
	public void execute(TransformContext tracon, StatementContext stacon) throws TransformException {
		/*
		 * Execution: create a new SDA node, and add it to the current output node.
		 * Then, execute the compound statement with the new node set as the current
		 * output node to collect any child nodes created "downstream".
		 */
		try {
			DataNode newNode = new DataNode(nodeName);
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
	 *         <code>node "<i>name</i>" { <i>statement?</i> }</code>
	 */
	public DataNode toSDA() {
		DataNode node = new DataNode(Statements.NODE.tag, nodeName);
		for (Node statement : nodes()) // add child statements, if any
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

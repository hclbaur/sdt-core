package be.baur.sdt.statements;

import be.baur.sda.Node;
import be.baur.sda.NodeSet;
import be.baur.sda.SDA;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;

/**
 * The {@code NodeStatement} creates a new node with the specified name and an
 * empty value (and therefore no XPath expression needs to be evaluated). Any
 * child nodes can be created in the compound statement.
 * 
 * @see NodeValueStatement
 */
public class NodeStatement extends Statement {

	/**
	 * Creates a {@code NodeStatement}.
	 * 
	 * @param name the name of the new node, not null
	 * @throws IllegalArgumentException if the node name is invalid
	 */
	public NodeStatement(String name) {
		super(Statements.NODE.tag);
		if (!SDA.isName(name))
			throw new IllegalArgumentException("invalid node name '" + name + "'");
		setValue(name); // name is stored in the node value, bit icky
	}


	@Override
	public void execute(TransformContext tracon, StatementContext stacon) throws TransformException {
		/*
		 * Execution: create a new SDA node, and add it to the current output node.
		 * Then, execute the compound statement with the new node set as the current
		 * output node to collect any child nodes created "downstream".
		 */
		try {
			Node newNode = new Node(getValue()); // icky :(
			stacon.getOutputNode().add(newNode);

			NodeSet statements = getNodes();
			if (statements == null) return; // nothing further to do
			
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
	public Node toNode() {
		Node node = new Node(Statements.NODE.tag, getValue());
		final NodeSet nodes = this.getNodes();
		if (nodes != null)
			for (Node statement : nodes) // add child statements, if any
				node.add(((Statement) statement).toNode());
		return node;
	}

}

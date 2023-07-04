package be.baur.sdt.statements;

import be.baur.sda.Node;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;

/**
 * The <code>OtherwiseStatement</code> is an optional subordinate statement of
 * the {@link ChooseStatement} that executes a compound statement if no other
 * conditions apply.
 */
public class OtherwiseStatement extends Statement {

	/**
	 * Creates an OtherwiseStatement.
	 */
	public OtherwiseStatement() {
		super(Statements.OTHERWISE.tag);
		add(null); // must have child statements so initialize it with an empty node set
	}


	@Override
	public void execute(TransformContext tracon, StatementContext stacon) throws TransformException {
		/*
		 * This method does nothing. Execution takes place in the context of the ChooseStatement.
		 */
	}

	
	/**
	 * @return an SDA node representing<br>
	 *         <code>when "<i>expression</i>" { <i>statement+</i> }</code>
	 */
	public Node toNode() {
		Node node = new Node(Statements.OTHERWISE.tag); 
		for (Node statement : this.getNodes()) // add child statements
			node.add(((Statement) statement).toNode());
		return node;
	}

}

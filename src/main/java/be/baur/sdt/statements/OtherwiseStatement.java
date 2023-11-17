package be.baur.sdt.statements;

import be.baur.sda.Node;
import be.baur.sda.DataNode;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;

/**
 * The <code>OtherwiseStatement</code> is an optional subordinate statement of
 * the {@link ChooseStatement} that executes a compound statement if no other
 * conditions apply.
 */
public class OtherwiseStatement extends Statement {

	@Override
	public void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * This method does nothing. Execution takes place in the context of the ChooseStatement.
		 */
	}

	
	/**
	 * @return an SDA node representing<br>
	 *         <code>otherwise { <i>statement+</i> }</code>
	 */
	public DataNode toSDA() {
		DataNode node = new DataNode(Statements.OTHERWISE.tag); 
		for (Node statement : nodes()) // add child statements
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

package be.baur.sdt.statements;

import be.baur.sda.Node;
import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
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
	void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * This method does nothing. Execution takes place in the context of the ChooseStatement.
		 */
	}

	
	/**
	 * @return a data node representing:<br><br>
	 *         <code>otherwise { <i>statement+</i> }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Statements.OTHERWISE.tag); 
		node.add(null); // render compound statement, even if empty
		for (Node statement : nodes()) // add any child statements
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

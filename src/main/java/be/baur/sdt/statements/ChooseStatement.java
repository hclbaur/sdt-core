package be.baur.sdt.statements;

import java.util.Objects;

import be.baur.sda.Node;
import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>ChooseStatement</code> conditionally executes a compound statement,
 * supporting multiple conditions. It contains at least one {@link WhenStatement}
 * and an optional {@link OtherwiseStatement}.
 */
public class ChooseStatement extends Statement {

	/**
	 * Creates a ChooseStatement with a subordinate WhenStatement.
	 * 
	 * @param when a WhenStatement, not null
	 */
	public ChooseStatement(WhenStatement when) {
		Objects.requireNonNull(when, "when statement must not be null");
		add(when);
	}


	@Override void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: for each sub-ordinate "when" statement, create an XPath from the
		 * statement expression, set the variable context and perform a Boolean
		 * evaluation. If the result is true, execute its compound statement and return.
		 * If false, evaluate the next "when" statement. If no "when" statements apply
		 * and there is an "otherwise", execute its compound statement and return.
		 */

		try {
			for (Node statement : nodes()) { // will have at least a when statement
				
				Boolean test = false;
				if (statement instanceof WhenStatement) {
					
					SDAXPath xpath = new SDAXPath( ((WhenStatement) statement).getExpression() );
					xpath.setVariableContext(staco);
					test = xpath.booleanValueOf(staco.getContextNode());
					if (! test) continue; // test next when clause
				}
				
				// we have a when that applies, or an otherwise, or neither.
				if (test || statement instanceof OtherwiseStatement) {
					
					// execute compound of when or otherwise in new context
					StatementContext coco = staco.newChild();
					for (Node compound : statement.nodes()) {
						((Statement) compound).execute(traco, coco);
					}
					return;
				}
				
				// we have a when that does not apply or (in theory) something that does not belong here at all
				if (! (statement instanceof WhenStatement))
					throw new TransformException(this, "statement '" + statement.getName() + "' is not allowed here");
				
				// continue statement loop
			}
			// no when statements applied and there was no otherwise, so do nothing
		
		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}

	
	/**
	 * @return an SDA node representing<br>
	 *         <code>choose { <i>when_statement+</i> <i>otherwise_statement?</i> }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Statements.CHOOSE.tag);
		for (Node statement : nodes()) // add when/otherwise statements
			node.add(((Statement) statement).toSDA());
		return node;
	}

}

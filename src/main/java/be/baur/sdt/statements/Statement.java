package be.baur.sdt.statements;

import be.baur.sda.Node;
import be.baur.sda.serialization.SDAFormatter;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;

/**
 * The abstract superclass of all transform statements.
 */
public abstract class Statement extends Node {

	/**
	 * Creates a {@code Statement}.
	 * 
	 * @param name a valid statement name, see {@link Statements}
	 */
	public Statement(String name) {
		super(name);
	}
	
	
	/**
	 * Executes this statement. The caller of this method must supply a
	 * {@code TransformContext} and a {@code StatementContext}.
	 * 
	 * @param tracon the transformation context
	 * @param stacon the statement context
	 * @throws TransformException if an exception occurs during execution
	 */
	public abstract void execute(TransformContext tracon, StatementContext stacon) throws TransformException;

	
	/**
	 * Returns an SDA node representing this statement. In other words, what an SDA
	 * parser would return upon processing an input stream describing the statement
	 * in SDT notation.
	 * 
	 * @return a node representing this statement
	 */
	public abstract Node toNode();

	
	/**
	 * Returns a string representing this statement in SDT notation. The result is
	 * formatted as a single line of text. For a more readable output, use the
	 * {@link #toNode} method and render it node using an {@link SDAFormatter}.
	 * 
	 * @return a string representation of this statement
	 */
	@Override
	public String toString() {
		return toNode().toString();
	}

}

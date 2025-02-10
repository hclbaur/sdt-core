package be.baur.sdt.transform;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;
import be.baur.sda.io.SDAFormatter;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;

/**
 * The abstract superclass of all transform statements.
 */
public abstract class Statement extends AbstractNode {


	/**
	 * Executes this statement. The caller of this method must supply a
	 * {@code TransformContext} and a {@code StatementContext}.
	 * 
	 * @param traco the transformation context
	 * @param staco the statement context
	 * @throws TransformException if an exception occurs during execution
	 */
	abstract void execute(TransformContext traco, StatementContext staco) throws TransformException;

	
	/**
	 * Returns an SDA node representing this statement. In other words, what an SDA
	 * parser would return upon processing an input stream describing the statement
	 * in SDT notation.
	 * 
	 * @return a node representing this statement
	 */
	public abstract DataNode toSDA();

	
	/**
	 * Returns a string representing this statement in SDT notation. The result is
	 * formatted as a single line of text. For a more readable output, use the
	 * {@link #toSDA} method and render it node using an {@link SDAFormatter}.
	 * 
	 * @return a string representation of this statement
	 */
	@Override
	public String toString() {
		return toSDA().toString();
	}

}

package be.baur.sdt;

import java.util.List;
import java.util.Objects;

import be.baur.sda.Node;
import be.baur.sda.serialization.SDAFormatter;
import be.baur.sdt.serialization.SDTParser;
import be.baur.sdt.statements.Statement;
import be.baur.sdt.statements.StatementContext;

/**
 * A {@code Transform} represents a recipe with statements for mapping and
 * transformation of SDA content. It is usually not created "manually" but
 * parsed from input in SDT notation.
 * 
 * @see Statement
 * @see SDTParser
 */
public final class Transform extends Node {

	public static final String TAG = "transform";	
	

	/** 
	 * Creates a {@code Transform}. 
	 */
	public Transform() {
		super(TAG); // extends Node, so it must have a tag, even if we do not really use it
	}


	/**
	 * Executes this transform suing the supplied {@code TransformContext}. This
	 * method returns an output document {@code Node} which may be empty if no nodes
	 * were created during transformation.
	 * 
	 * @param context the transformation context, not null
	 * @return the output Node, may be null
	 * @throws TransformException if an exception occurs during execution
	 * @see TransformContext
	 */
	public Node execute(TransformContext context) throws TransformException {

		Objects.requireNonNull(context, "context must not be null");

		List<Node> statements = nodes();
		if (statements.isEmpty()) return null; // nothing to do, should we return null though?

		StatementContext stacon = new StatementContext();
		for (Node statement : statements) {
			((Statement) statement).execute(context, stacon);
		}

		// At the end of the transform return the output node
		// this is probably not the best design, must improve!
		return stacon.getOutputNode();
	}
	
	
	/**
	 * Returns an SDA node representing this transform. In other words, what an SDA
	 * parser would return upon processing an input stream describing the Transform
	 * in SDT notation.
	 * 
	 * @return a node representing<br>
	 *         <code>transform { <i>statement?</i> }</code>
	 */
	public Node toNode() {
		Node node = new Node(TAG); node.add(null); // in case there are no statements
		for (Node statement : nodes()) // add child statements
			node.add(((Statement) statement).toNode());
		return node;
	}
	
	
	/**
	 * Returns the string representing this transform in SDT notation. For example:
	 * 
	 * <pre>
	 * transform { println { value "'hello world!'" } }
	 * </pre>
	 * 
	 * The result is formatted as a single line of text. For a more readable output,
	 * use the {@link #toNode} method and render it using a {@link SDAFormatter}.
	 * 
	 * @return a string in SDT format
	 */
	@Override
	public String toString() {
		return toNode().toString();
	}
}

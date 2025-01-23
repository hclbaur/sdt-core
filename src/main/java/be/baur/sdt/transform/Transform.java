package be.baur.sdt.transform;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Objects;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.serialization.SDAFormatter;
import be.baur.sda.serialization.SDAParseException;
import be.baur.sdt.SDT;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;
import be.baur.sdt.parser.SDTParseException;
import be.baur.sdt.parser.SDTParser;

/**
 * A {@code Transform} represents a recipe with statements for mapping and
 * transformation of node content. It is usually not created "manually" but
 * parsed from input in SDT notation.
 * 
 * @see Statement
 * @see SDTParser
 */
public final class Transform extends AbstractNode {
	
	/**
	 * Executes this transform with the supplied {@code TransformContext}. This
	 * method returns an output {@code DataNode} or null if no nodes were created
	 * during transformation.
	 * 
	 * @param context the transformation context, not null
	 * @return an output node, may be null
	 * @throws TransformException if an exception occurs during execution
	 * @see TransformContext
	 */
	public DataNode execute(TransformContext context) throws TransformException {

		Objects.requireNonNull(context, "context must not be null");

		StatementContext staco = new StatementContext();
	    DataNode output = new DataNode("output"); // collects nodes created during transform
	    staco.setOutputNode(output);
	    
	    List<Statement> statements = nodes();
		for (Statement statement : statements) {
			statement.execute(context, staco);
		}

		/*
		 * At the end of the transform the output node will contain zero or more child
		 * nodes that have been created during transformation. However, a transform must
		 * return at most one data node. If additional nodes were created, an exception
		 * is thrown.
		 */
 
		List<DataNode> nodes = output.nodes();
		
		switch (nodes.size()) {
		case 0:
			return null;
		case 1:
			DataNode first = nodes.get(0);
			output.remove(first);
			return first;
		}
		
		// if we get here at least 2 top-level nodes were created, which is illegal
		throw new TransformException(null,
			"an additional root node '" + nodes.get(1).getName() + "' was created");
	}
	
	
	/**
	 * Returns an SDA node representing this transform. In other words, what an SDA
	 * parser would return upon processing an input stream describing the Transform
	 * in SDT notation.
	 * 
	 * @return a node representing<br>
	 *         <code>transform { <i>statement*</i> }</code>
	 */
	public DataNode toSDA() {
		DataNode node = new DataNode(Keyword.TRANSFORM.tag); 
		node.add(null); // in case there are no statements
		for (Node statement : nodes()) // add child statements
			node.add(((Statement) statement).toSDA());
		return node;
	}
	
	
	/**
	 * Returns the string representing this transform in SDT notation. For example:
	 * 
	 * <pre>
	 * transform { println "'hello world!'" }
	 * </pre>
	 * 
	 * The result is formatted as a single line of text. For a more readable output,
	 * use the {@link #toSDA} method and render it using a {@link SDAFormatter}.
	 * 
	 * @return a string in SDT format
	 */
	@Override
	public String toString() {
		return toSDA().toString();
	}
	
	
	/**
	 * Verifies this transform. This method can be used to validate a transform that
	 * was not created by the {@code SDTParser}.
	 * 
	 * @throws IOException       if an I/O operation failed
	 * @throws SDAParseException if an SDA parse exception occurs
	 * @throws SDTParseException if an SDT parse exception occurs
	 */
	public void verify() throws IOException, SDAParseException, SDTParseException {
		// Serialize the Transform to SDT, then parsed it back to reveal any issues
		SDT.parse(new StringReader(this.toString()));
	}
}

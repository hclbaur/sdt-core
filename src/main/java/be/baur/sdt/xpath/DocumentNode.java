package be.baur.sdt.xpath;

import java.util.Objects;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;
import be.baur.sda.Node;

/**
 * A document node is a node that encapsulates the entire document. In SDA there
 * is no such thing, but this class mimics one, so that XPath expressions may
 * select "/" and the root node by name.
 */
final class DocumentNode extends AbstractNode {

	/**
	 * Creates a document node containing the specified root node. This is not a
	 * public class; only the navigator should create document nodes.
	 *
	 * @param root a root node (e.g. not a parent), and not null
	 */
	DocumentNode(DataNode root) {
		Objects.requireNonNull(root, "root node must not be null");
		super.add(root); // one and only child; we override the add/remove methods.
	}


	/**
	 * This method throws an {@code UnsupportedOperationException}. DocumentNodes
	 * must not be tampered with after creation.
	 */
	@Override
	public boolean add(Node node) {
		throw new UnsupportedOperationException("cannot add to a document node");
	}


	/**
	 * This method throws an {@code UnsupportedOperationException}. DocumentNodes
	 * must not be tampered with after creation.
	 */
	@Override
	public boolean remove(Node node) {
		throw new UnsupportedOperationException("cannot remove from a document node");
	}


	/**
	 * Returns a string representing the root node.
	 */	
	@Override
	public String toString() {
		return nodes().get(0).toString();
	}
}

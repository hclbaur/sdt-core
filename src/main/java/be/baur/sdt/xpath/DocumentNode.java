package be.baur.sdt.xpath;

import java.util.Objects;

import be.baur.sda.AbstractNode;
import be.baur.sda.DataNode;

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
		add(root); // will throw an exception if root has a parent!
	}

	/*
	 * DocumentNodes should not be tampered with after creation. We should override
	 * the add/remove methods to prevent modifying them, but this will require a
	 * change in sda-core, as currently they cannot be overridden.
	 */

	/**
	 * Returns a string representing the root node.
	 */	
	@Override
	public String toString() {
		return nodes().get(0).toString();
	}
}

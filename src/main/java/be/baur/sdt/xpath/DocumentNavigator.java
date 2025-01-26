package be.baur.sdt.xpath;

import java.io.File;
import java.io.Reader;
import java.util.Collections;
import java.util.Iterator;

import org.jaxen.DefaultNavigator;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenConstants;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.SDA;

/**
 * Interface for navigating around the SDA object model.
 * 
 * This class is not intended for direct usage, but is used by the Jaxen engine
 * during evaluation.
 * 
 * @see DefaultNavigator
 * @see XPath
 *
 */
@SuppressWarnings("rawtypes")
public class DocumentNavigator extends DefaultNavigator {

	private static final long serialVersionUID = 3623355213625129369L;
	private static final DocumentNavigator SINGLETON = new DocumentNavigator();

	/**
	 * Non-public default constructor, the DocumentNavigator is a singleton.
	 */
	private DocumentNavigator() {
	}

    
	/**
	 * Returns a singleton instance of this {@code Navigator}.
	 *
	 * @return a Navigator
	 */
	public static Navigator getInstance() {
		return SINGLETON;
	}


	/**
	 * Retrieve an <code>Iterator</code> matching the <code>child</code> XPath axis.
	 *
	 * @param contextNode the original context node
	 *
	 * @return an Iterator capable of traversing the axis, not null
	 */
	@Override
    public Iterator getChildAxisIterator(Object contextNode)
    {
    	Node node = ((Node) contextNode);
    	if (node.isParent()) 
    		return node.nodes().iterator();
    	return JaxenConstants.EMPTY_ITERATOR;
    }


	/**
	 * Retrieve an <code>Iterator</code> matching the <code>parent</code> XPath
	 * axis.
	 *
	 * @param contextNode the original context node
	 *
	 * @return an Iterator capable of traversing the axis, not null
	 */
	@Override
    public Iterator getParentAxisIterator(Object contextNode)
    {
    	Node parent = ((Node) contextNode).getParent();
        if (parent != null) {
        	return Collections.singletonList(parent).iterator();
        }
    	return JaxenConstants.EMPTY_ITERATOR;
    }


	/**
	 * Throws an {@code UnsupportedAxisException}.
	 */
	@Override
    public Iterator getAttributeAxisIterator(Object contextNode) throws UnsupportedAxisException
    {
        throw new UnsupportedAxisException("SDA does not support attributes");
    }


	/**
	 * Throws an {@code UnsupportedAxisException}.
	 */
	@Override
    public Iterator getNamespaceAxisIterator(Object contextNode) throws UnsupportedAxisException
    {
        throw new UnsupportedAxisException("SDA does not support namespaces");
    }


	/**
	 * Returns the document node that contains the given context node.
	 *
	 * @see #isDocument(Object)
	 *
	 * @param contextNode the context node
	 *
	 * @return the document of the context node
	 */
	@Override
    public Object getDocumentNode(Object contextNode)
    {
        Node root = ((Node) contextNode).root();
        return root instanceof DocumentNode ? root : null;
    }


	/**
	 * Returns a document node containing the specified data node. This method is
	 * supplied to create a document node like in XML DOM, which will allow XPath
	 * expressions to select "/" and the root node by name.
	 * <p>
	 * <i>Note: this method will modify the data node (add it to a parent) and may
	 * have side effects (consider supplying a copy of the real root node). This
	 * method is experimental and may be obsoleted or removed in the future.</i>
	 * 
	 * @see DocumentNode
	 * 
	 * @param root a root node (e.g. not a parent), and not null
	 * 
	 * @return a new document node
	 */
    public static DocumentNode newDocumentNode(DataNode root)
    {
    	return new DocumentNode(root);
    }


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
    public String translateNamespacePrefixToUri(String prefix, Object contextNode)
    {
    	throw new UnsupportedOperationException("SDA does not support namespaces");
    }


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
    public String getProcessingInstructionTarget(Object contextNode)
    {
    	throw new UnsupportedOperationException("SDA does not support processing instructions");
    }


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
    public String getProcessingInstructionData(Object contextNode)
    {
    	throw new UnsupportedOperationException("SDA does not support processing instructions");
    }


	/**
	 * Returns the parent of the given context node.
	 *
	 * <p>
	 * The parent of any node must either be a document node or an element node.
	 * </p>
	 *
	 * @see #isDocument
	 * @see #isElement
	 *
	 * @param contextNode the context node
	 * 
	 * @return the parent of the context node, or null if this is a document node.
	 */
	@Override
    public Object getParentNode(Object contextNode)
    {
    	return ((Node) contextNode).getParent();
    }


	/**
	 * Loads an SDA document from the given URI.
	 *
	 * @param uri the URI of the document to load
	 *
	 * @return the document
	 *
	 * @throws FunctionCallException if the document could not be loaded
	 */
	@Override
    public Object getDocument(String uri) throws FunctionCallException
    {
    	try {
			return SDA.parse(new File(uri));
		} catch (Exception e) {
			throw new FunctionCallException(e.getMessage(), e);
		}
    }


	/**
	 * Loads an SDA document from the given character stream.
	 *
	 * @param input the stream with SDA content 
	 *
	 * @return the document
	 *
	 * @throws FunctionCallException if the document could not be loaded
	 */
    public static Node getDocument(Reader input) throws FunctionCallException
    {
    	try {
			return SDA.parse(input);
		} catch (Exception e) {
			throw new FunctionCallException(e.getMessage(), e);
		}
    }


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
    public Object getElementById(Object contextNode, String elementId)
    {
    	throw new UnsupportedOperationException("SDA does not support element Ids");
    }


	/**
	 * Always returns null; SDA does not support element namespaces.
	 */
	@Override
	public String getElementNamespaceUri(Object element) {
		//throw new UnsupportedOperationException("SDA does not support namespaces");
		return null; // not sure if I should return null or empty string
	}


	@Override
	public String getElementName(Object element) {
		return ((Node) element).getName();
	}


	/**
	 * Always returns the local name; SDA does not support element namespaces.
	 */
	@Override
	public String getElementQName(Object element) {
		// No namespaces in SDA so local and qualified names are the same
		return ((Node) element).getName();
	}


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
	public String getAttributeNamespaceUri(Object attr) {
		throw new UnsupportedOperationException("SDA does not support attributes");
	}


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
	public String getAttributeName(Object attr) {
		throw new UnsupportedOperationException("SDA does not support attributes");
	}


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
	public String getAttributeQName(Object attr) {
		throw new UnsupportedOperationException("SDA does not support attributes");
	}


	@Override
	public boolean isDocument(Object object) {
		return (object instanceof DocumentNode);
	}


	@Override
	public boolean isElement(Object object) {
		// All SDA nodes except the document node are considered elements
		return (object instanceof DocumentNode) ? false : (object instanceof Node);
	}


	/**
	 * Always returns false; SDA does not support attributes.
	 */
	@Override
	public boolean isAttribute(Object object) {
		// SDA does not support attributes
		return false;
	}


	/**
	 * Always returns false; SDA does not support namespaces.
	 */
	@Override
	public boolean isNamespace(Object object) {
		// SDA does not support namespaces
		return false;
	}


	/**
	 * Always returns false; SDA does not support comments.
	 */
	@Override
	public boolean isComment(Object object) {
		// SDA does not support comments
		return false;
	}


	/**
	 * Always returns false; SDA does not support text nodes.
	 */
	@Override
	public boolean isText(Object object) {
		// SDA does not support text nodes
		return false;
	}


	/**
	 * Always returns false; SDA does not support processing instructions.
	 */
	@Override
	public boolean isProcessingInstruction(Object object) {
		// SDA does not support processing instructions
		return false;
	}


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
	public String getCommentStringValue(Object comment) {
		throw new UnsupportedOperationException("SDA does not support comments");
	}


	@Override
	public String getElementStringValue(Object element) {
		if (element instanceof DataNode)
			return ((DataNode) element).getValue();
		else
			return "";
	}


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
	public String getAttributeStringValue(Object attr) {
		throw new UnsupportedOperationException("SDA does not support attributes");
	}


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
	public String getNamespaceStringValue(Object ns) {
		throw new UnsupportedOperationException("SDA does not support namespaces");
	}


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
	public String getTextStringValue(Object text) {
		throw new UnsupportedOperationException("SDA does not support text nodes");
	}


	/**
	 * Throws an {@code UnsupportedOperationException}.
	 */
	@Override
	public String getNamespacePrefix(Object ns) {
		throw new UnsupportedOperationException("SDA does not support namespaces");
	}


	@Override
	public XPath parseXPath(String xpath) throws JaxenException   {
		return new SDAXPath(xpath);
	}

}
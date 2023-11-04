package be.baur.sdt.xpath;

import java.io.File;
import java.io.FileReader;
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

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sda.DataNode;

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
	 * @return a Navigator.
	 */
	public static Navigator getInstance() {
		return SINGLETON;
	}
    
	@Override
    public Iterator getChildAxisIterator(Object contextNode)
    {
    	Node node = ((Node) contextNode);
    	if (node.isParent()) 
    		return node.nodes().iterator();
    	return JaxenConstants.EMPTY_ITERATOR;
    }
    
	@Override
    public Iterator getParentAxisIterator(Object contextNode)
    {
    	Node parent = ((Node) contextNode).getParent();
        if (parent != null) {
        	return Collections.singletonList(parent).iterator();
        	//ArrayList<Node> l = new ArrayList<Node>();
        	//l.add(parent); return l.iterator();
        }
    	return JaxenConstants.EMPTY_ITERATOR;
    }
    
	@Override
    public Iterator getAttributeAxisIterator(Object contextNode) throws UnsupportedAxisException
    {
        throw new UnsupportedAxisException("SDA does not support attributes");
    }
    
	@Override
    public Iterator getNamespaceAxisIterator(Object contextNode) throws UnsupportedAxisException
    {
        throw new UnsupportedAxisException("SDA does not support namespaces");
    }
    
	@Override
    public Object getDocumentNode(Object contextNode)
    {
		//if (contextNode == null) return new Node("empty");  // good idea? 
        return ((Node) contextNode).root();
    }
    
	@Override
    public String translateNamespacePrefixToUri(String prefix, Object contextNode)
    {
    	throw new UnsupportedOperationException("SDA does not support namespaces");
    }
    
	@Override
    public String getProcessingInstructionTarget(Object contextNode)
    {
    	throw new UnsupportedOperationException("SDA does not support processing instructions");
    }

	@Override
    public String getProcessingInstructionData(Object contextNode)
    {
    	throw new UnsupportedOperationException("SDA does not support processing instructions");
    }
    
	@Override
    public Object getParentNode(Object contextNode)
    {
    	return ((Node) contextNode).getParent();
    }
    
	@Override
    public Object getDocument(String uri) throws FunctionCallException
    {
    	try {
			return getDocument(new FileReader(new File(uri)));
		} catch (Exception e) {
			throw new FunctionCallException(e.getMessage(), e);
		}
    }
	
    public static DataNode getDocument(Reader input) throws FunctionCallException
    {
    	DataNode root, document;
    	try {
			root = SDA.parse(input);
		} catch (Exception e) {
			throw new FunctionCallException(e.getMessage(), e);
		}
    	// add the root to a "document node" for XPath to work as expected
    	document = new DataNode("document"); document.add(root); 
    	return document;
    }
    
	@Override
    public Object getElementById(Object contextNode, String elementId)
    {
    	throw new UnsupportedOperationException("SDA does not support element Ids");
    }
        
	@Override
	public String getElementNamespaceUri(Object element) {
		//throw new UnsupportedOperationException("SDA does not support namespaces");
		return null; // not sure if I should return null or empty string
	}

	@Override
	public String getElementName(Object element) {
		return ((Node) element).getName();
	}

	@Override
	public String getElementQName(Object element) {
		// No namespaces in SDA so local and qualified names are the same
		return ((Node) element).getName();
	}

	@Override
	public String getAttributeNamespaceUri(Object attr) {
		throw new UnsupportedOperationException("SDA does not support namespaces");
	}

	@Override
	public String getAttributeName(Object attr) {
		throw new UnsupportedOperationException("SDA does not support attributes");
	}

	@Override
	public String getAttributeQName(Object attr) {
		throw new UnsupportedOperationException("SDA does not support attributes");
	}

	@Override
	public boolean isDocument(Object object) {
		// The root node is considered the document node
		return (object instanceof Node && ((Node) object).getParent() == null);
	}

	@Override
	public boolean isElement(Object object) {
		// All SDA nodes except the root node are considered elements
		return (object instanceof Node && ((Node) object).getParent() != null);
	}

	@Override
	public boolean isAttribute(Object object) {
		// SDA does not support attributes
		return false;
	}

	@Override
	public boolean isNamespace(Object object) {
		// SDA does not support namespaces
		return false;
	}

	@Override
	public boolean isComment(Object object) {
		// SDA does not support comments
		return false;
	}

	@Override
	public boolean isText(Object object) {
		// SDA does not support text nodes
		return false;
	}

	@Override
	public boolean isProcessingInstruction(Object object) {
		// SDA does not support processing instructions
		return false;
	}

	@Override
	public String getCommentStringValue(Object comment) {
		throw new UnsupportedOperationException("SDA does not support comments");
	}

	@Override
	public String getElementStringValue(Object element) {
		return ((DataNode) element).getValue();
	}

	@Override
	public String getAttributeStringValue(Object attr) {
		throw new UnsupportedOperationException("SDA does not support attributes");
	}

	@Override
	public String getNamespaceStringValue(Object ns) {
		throw new UnsupportedOperationException("SDA does not support namespaces");
	}

	@Override
	public String getTextStringValue(Object text) {
		throw new UnsupportedOperationException("SDA does not support text nodes");
	}

	@Override
	public String getNamespacePrefix(Object ns) {
		throw new UnsupportedOperationException("SDA does not support namespaces");
	}

	@Override
	public XPath parseXPath(String xpath) throws JaxenException   {
		return new SDAXPath(xpath);
	}

}
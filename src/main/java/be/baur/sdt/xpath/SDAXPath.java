package be.baur.sdt.xpath;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

import be.baur.sdt.SDT;


/**
 * An XPath implementation for the SDA object model.
 * 
 * This is the main entry point for matching an XPath against an SDA node tree.
 * You create a compiled XPath object, then match it against one or more context
 * nodes using the {@link #selectNodes(Object)} method, as in the following
 * example:
 *
 * <pre>
 * XPath path = new SDAXPath("<i>expression</i>");
 * List results = path.selectNodes(node);
 * </pre>
 *
 * @see BaseXPath
 */
public class SDAXPath extends BaseXPath {

	private static final long serialVersionUID = 368489177460992020L;
	
	/**
	 * Create a new <code>SDAXPath</code> from an XPath expression.
	 *
	 * @param expression the XPath expression
	 * 
	 * @throws JaxenException if there is a syntax error in the expression
	 */
	public SDAXPath(String expression) throws JaxenException {
		
		super(expression, DocumentNavigator.getInstance());
		this.addNamespace(SDT.FUNCTIONS_NS_PFX, SDT.FUNCTIONS_NS_URI);
		this.addNamespace(SDT.W3CFUNCTIONS_NS_PFX, SDT.W3CFUNCTIONS_NS_URI);
	}


	@Override
	protected SDTFunctionContext createFunctionContext() {
		
		// the default SDT function context
		return SDTFunctionContext.getInstance();
	}
	
}

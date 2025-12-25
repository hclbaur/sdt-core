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
	 * Create a new <code>SDAXPath</code> from an XPath expression. Note that
	 * support for SDT extensions is <i>not</i> included by default.
	 *
	 * @param expression an XPath expression
	 * @throws JaxenException if there is a syntax error in the expression
	 * @see #withSDTSupport
	 */
	public SDAXPath(String expression) throws JaxenException {
		
		super(expression, DocumentNavigator.getInstance());
	}
	
	
	/**
	 * Creates an XPath expression object that includes support for all SDT
	 * extensions.
	 * 
	 * @param expression an XPath expression
	 * @return a new XPath expression object, not null
	 * @throws JaxenException if the XPath expression is invalid
	 */
	public static SDAXPath withSDTSupport(String expression) throws JaxenException {

		SDAXPath xpath = new SDAXPath(expression);
		xpath.setFunctionContext(SDTFunctionContext.getInstance());
		xpath.addNamespace(SDT.FUNCTIONS_NS_PFX, SDT.FUNCTIONS_NS_URI);
		xpath.addNamespace(SDT.W3CFUNCTIONS_NS_PFX, SDT.W3CFUNCTIONS_NS_URI);
		return xpath;
	}

}

package be.baur.sdt.xpath;

import org.jaxen.FunctionContext;
import org.jaxen.SimpleNamespaceContext;

import be.baur.sdt.SDT;

/**
 * A <code>NamespaceContext</code> pre-populated with SDT bindings.
 * 
 * @see FunctionContext
 */
public class SDTNamespaceContext extends SimpleNamespaceContext
{
	private static final long serialVersionUID = 6971443730989114519L;
	private static final SDTNamespaceContext instance = new SDTNamespaceContext();

	/**
	 * Returns the default SDT namespace context. Do not extend this with your own
	 * bindings, as it will affect all XPath instances that use this global
	 * default. Instead, extend <code>SDTNamespaceContext</code> or create a new
	 * instance to include your own functions.
	 *
	 * @return a namespace context
	 */
	public static SDTNamespaceContext getInstance() {
		return instance;
	}

 
	/**
	 * Create a new XPath namespace context including the SDT extension bindings.
	 */
	public SDTNamespaceContext() {

		super();
		addNamespace(SDT.FUNCTIONS_NS_PFX, SDT.FUNCTIONS_NS_URI);
		addNamespace(SDT.W3CFUNCTIONS_NS_PFX, SDT.W3CFUNCTIONS_NS_URI);
	}

}

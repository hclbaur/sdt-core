package be.baur.sdt.xpath;

import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;

import be.baur.sdt.SDT;

/**
 * A <code>NamespaceContext</code> pre-populated with SDT specific bindings.
 * 
 * @see NamespaceContext
 */
public class SDTNamespaceContext extends SimpleNamespaceContext
{
	private static final long serialVersionUID = 6971443730989114519L;
	private static final SDTNamespaceContext instance = new SDTNamespaceContext();

	/**
	 * Returns the default SDT namespace context. Be aware that adding bindings will
	 * affect all XPath instances that use this default. Consider extending
	 * <code>SDTNamespaceContext</code> or create a new instance to add your own
	 * bindings to.
	 *
	 * @return a namespace context
	 */
	public static SDTNamespaceContext getInstance() {
		return instance;
	}

 
	/**
	 * Create a new namespace context that includes the SDT extension bindings.
	 */
	public SDTNamespaceContext() {

		super();
		addNamespace(SDT.FUNCTIONS_NS_PFX, SDT.FUNCTIONS_NS_URI);
		addNamespace(SDT.W3CFUNCTIONS_NS_PFX, SDT.W3CFUNCTIONS_NS_URI);
	}

}

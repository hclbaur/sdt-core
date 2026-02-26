package be.baur.sdt.xpath;

import static be.baur.sdt.xpath.SDTFunctionContext.FUNCTIONS_NS_PFX;
import static be.baur.sdt.xpath.SDTFunctionContext.FUNCTIONS_NS_URI;
import static be.baur.sdt.xpath.SDTFunctionContext.W3C_FUNCTIONS_NS_PFX;
import static be.baur.sdt.xpath.SDTFunctionContext.W3C_FUNCTIONS_NS_URI;

import java.io.Serializable;
import java.util.Objects;

import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;

/**
 * A <code>NamespaceContext</code> providing the bindings used by the SDT
 * extension functions.
 * 
 * @see NamespaceContext
 */
public final class SDTNamespaceContext implements NamespaceContext, Serializable
{
	private static final long serialVersionUID = -5838697281768509583L;

	// this class is a wrapper backed by a simple namespace context
	private static final SimpleNamespaceContext NC = new SimpleNamespaceContext();
 
	// pre-register SDT specific bindings
	static {
		NC.addNamespace(FUNCTIONS_NS_PFX, FUNCTIONS_NS_URI);
		NC.addNamespace(W3C_FUNCTIONS_NS_PFX, W3C_FUNCTIONS_NS_URI);
	}
	
	/**
	 * Create a new namespace context that includes the SDT extension bindings.
	 */
	public SDTNamespaceContext() {}


	@Override
	public String translateNamespacePrefixToUri(String prefix) {
		return NC.translateNamespacePrefixToUri(prefix);
	}


	/**
	 * Binds a prefix to a namespace URI in this context. This method will not
	 * overwrite existing bindings (including those pre-registered for SDT).
	 * 
	 * @param prefix a namespace prefix, not null
	 * @param URI    a namespace URI, not null
	 * @returns true if the context was changes as a result of this call
	 */
	public boolean addNamespace(String prefix, String URI) {

		Objects.requireNonNull(prefix, "prefix must not be null");
		Objects.requireNonNull(URI, "URI must not be null");

		if (NC.translateNamespacePrefixToUri(prefix) == null) {
			NC.addNamespace(prefix, URI);
			return true;
		}
		return false;
	}

}

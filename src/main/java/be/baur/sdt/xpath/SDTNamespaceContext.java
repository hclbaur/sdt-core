package be.baur.sdt.xpath;

import static be.baur.sdt.xpath.SDTFunctionContext.FUNCTIONS_NS_PFX;
import static be.baur.sdt.xpath.SDTFunctionContext.FUNCTIONS_NS_URI;
import static be.baur.sdt.xpath.SDTFunctionContext.W3C_FUNCTIONS_NS_PFX;
import static be.baur.sdt.xpath.SDTFunctionContext.W3C_FUNCTIONS_NS_URI;

import java.util.Objects;

import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;

/**
 * A <code>NamespaceContext</code> providing the bindings used by the SDT
 * extension functions.
 * 
 * @see NamespaceContext
 */
public final class SDTNamespaceContext implements NamespaceContext {

	// this class is a wrapper backed by a pre-registered namespace context
	private static final SimpleNamespaceContext PREREG = new SimpleNamespaceContext();

	// register SDT specific bindings
	static {
		PREREG.addNamespace(FUNCTIONS_NS_PFX, FUNCTIONS_NS_URI);
		PREREG.addNamespace(W3C_FUNCTIONS_NS_PFX, W3C_FUNCTIONS_NS_URI);
	}


	// this instance namespace context for custom bindings
	private final SimpleNamespaceContext nscontext;
	
	/**
	 * Create a new SDT namespace context.
	 */
	public SDTNamespaceContext() {
		 nscontext = new SimpleNamespaceContext();
	}


	/**
	 * Binds a prefix to a namespace URI in this context. This method will not
	 * overwrite existing bindings (including those pre-registered for SDT).
	 * 
	 * @param prefix a namespace prefix, not null
	 * @param URI    a namespace URI, not null
	 * @return true if the context changed as a result of this call
	 */
	public boolean addNamespace(String prefix, String URI) {

		Objects.requireNonNull(prefix, "prefix must not be null");
		Objects.requireNonNull(URI, "URI must not be null");

		if (PREREG.translateNamespacePrefixToUri(prefix) == null) {

			if (nscontext.translateNamespacePrefixToUri(prefix) == null) {
				nscontext.addNamespace(prefix, URI);
				return true;
			}
		}
		return false;
	}


	@Override
	public String translateNamespacePrefixToUri(String prefix) {
		
		// first check pre-registered context, then this instance
		String URI = PREREG.translateNamespacePrefixToUri(prefix);
		return URI == null ? nscontext.translateNamespacePrefixToUri(prefix) : URI;
	}
}

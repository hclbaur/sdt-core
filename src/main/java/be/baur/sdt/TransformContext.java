package be.baur.sdt;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jaxen.FunctionContext;
import org.jaxen.NamespaceContext;
import org.jaxen.Navigator;
import org.jaxen.XPath;
import org.jaxen.saxpath.SAXPathException;

import be.baur.sdt.transform.Transform;
import be.baur.sdt.xpath.DocumentNavigator;
import be.baur.sdt.xpath.SDTFunctionContext;
import be.baur.sdt.xpath.SDTNamespaceContext;

/**
 * A {@code TransformContext} is created prior to, and used during execution of
 * a {@code Transform}.
 * <p>
 * It provides a navigator, a writer for the {@code PrintStatement} to write
 * output to, and (optionally prepared) parameters to overwrite the default
 * value of a {@code ParamStatement}.
 * <p>
 * The context cannot be instantiated, but must be built using a
 * {@link Builder}.
 * 
 * @see Transform
 */
public class TransformContext {

	private final Writer writer;
	private final Map<String, Object> parameters;
	private final Navigator navigator;
	private final FunctionContext fncontext = new SDTFunctionContext();
	private final NamespaceContext nscontext;
	
	private TransformContext(Builder builder) {

		this.writer = builder.writer;
		this.parameters = builder.parameters;
		this.navigator = builder.navigator;
		this.nscontext = builder.nscontext;
	}


	/**
	 * Returns the {@code Writer} provided by this context. If no writer was set
	 * explicitly, this will be a writer to standard output.
	 * 
	 * @return a writer, not null
	 */
	public Writer getWriter() {
		return writer;
	}

	
	/**
	 * Returns an unmodifiable view of the parameters provided by this transform
	 * context.
	 * 
	 * @return a map, not null, may be empty
	 */
	public  Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(parameters);
	}
	
	
//	/**
//	 * Returns the navigator for this transform context.
//	 * 
//	 * @return a navigator, not null
//	 */
//	public Navigator getNavigator() {
//		return navigator;
//	}


	/**
	 * Creates an XPath expression object for this transform context. By default,
	 * this also includes the SDT function and namespace context.
	 * 
	 * @param expression an XPath expression
	 * @return a new XPath expression object, not null
	 * @throws SAXPathException if the XPath expression is invalid
	 * 
	 * @see SDTFunctionContext
	 * @see SDTNamespaceContext
	 */
	public XPath getXPath(String expression) throws SAXPathException {
		
		XPath xpath = navigator.parseXPath(expression);
		xpath.setFunctionContext( fncontext );
		xpath.setNamespaceContext( nscontext );
		return xpath;
	}



	/**
	 * A builder class to build a {@code TransformContext}. The builder has methods
	 * to set a writer for textual output and/or add parameters to the context.
	 */
	 public static class Builder {
		
		private Writer writer = new PrintWriter(System.out);
		private final Map<String, Object> parameters = new HashMap<String, Object>();
		private Navigator navigator = DocumentNavigator.getInstance();
		private final SDTNamespaceContext nscontext = new SDTNamespaceContext();
		
		/**
		 * Creates an {@code Builder} that builds a {@code TransformContext} with a
		 * <i>standard output writer</i> and no parameters. To set a different writer
		 * and/or add parameters, use the provided setter methods.
		 */
		public Builder() {
		}
		
		/**
		 * Sets the {@code Writer} for the context to be built. The writer cannot be
		 * null, but a {@link SDT#nullWriter} can be used to suppress output.
		 * 
		 * @param writer a writer, not null
		 * @return the builder
		 */
		public Builder setWriter(Writer writer) {
			this.writer = Objects.requireNonNull(writer, "writer must not be null");
			return this;
		}
		
		/*
		 * Private helper method to set a parameter with non-null name and value. We do
		 * not supply a generic method because parameters cannot be just anything, they
		 * must be either String, Double or Boolean.
		 */
		private Builder setParameter(String name, Object value) {
			Objects.requireNonNull(name, "name must not be null");
			Objects.requireNonNull(value, "value must not be null");
			this.parameters.put(name, value);
			return this;
		}
		
		/**
		 * Sets or overwrites a String parameter for the context to be built.
		 * 
		 * @param name  the name of the parameter, not null
		 * @param value a String, not null
		 * @return the builder
		 */
		public Builder setStringParameter(String name, String value) {
			return setParameter(name, value);
		}
		
		/**
		 * Sets or overwrites a Double parameter for the context to be built.
		 * 
		 * @param name  the name of the parameter, not null
		 * @param value a Double, not null
		 * @return the builder
		 */
		public Builder setDoubleParameter(String name, Double value) {
			return setParameter(name, value);
		}
		
		/**
		 * Sets or overwrites a Boolean parameter for the context to be built.
		 * 
		 * @param name  the name of the parameter, not null
		 * @param value a Boolean, not null
		 * @return the builder
		 */
		public Builder setBooleanParameter(String name, Boolean value) {
			return setParameter(name, value);
		}
		
		/**
		 * Sets the {@code Navigator} for the context to be built.
		 * 
		 * @param navigator a navigator, not null
		 * @return the builder
		 */
		public Builder setNavigator(Navigator navigator) {
			this.navigator  = Objects.requireNonNull(navigator, "navigator must not be null");
			return this;
		}

		/**
		 * Binds a prefix to a namespace URI in this context.
		 * 
		 * @param prefix a namespace prefix, not null
		 * @param URI    a namespace URI, not null
		 * @return the builder
		 * 
		 * @see SDTNamespaceContext#addNamespace
		 */
		public Builder addNamespace(String prefix, String URI) {
			nscontext.addNamespace(prefix, URI);
			return this;
		}

		/**
		 * Builds and returns a new {@code TransformContext}.
		 * 
		 * @return a transform context
		 */
		public TransformContext build() {
			return new TransformContext(this);
		}
	}

}

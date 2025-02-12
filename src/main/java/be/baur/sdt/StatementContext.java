package be.baur.sdt;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;

import be.baur.sda.DataNode;

/**
 * A {@code StatementContext} resolves variable bindings in XPath expressions
 * during the execution of statements in a {@code Transform}.
 * <p>
 * Since statements can be nested and each statement can have its own context,
 * variables may exist in several nodes of the "context tree", even with the
 * same name. A statement context will first try to resolve a variable binding
 * in its own context before checking its ancestor contexts.
 * <p>
 * In addition, the statement context provides the context node(set) for the
 * evaluation of XPath expressions, and the current output context node that
 * will be the parent of newly created nodes.
 * 
 * @see VariableContext
 */
public class StatementContext implements VariableContext {

	private final StatementContext parent; // the parent of this context
    private final Map<String, Object> variables = new HashMap<String, Object>();	

    private Object xpathContext = Collections.EMPTY_LIST; // the (initial) XPath context
    private DataNode outputNode = null; // the (initial) output context node
    
	/**
	 * Creates a {@code StatementContext}.
	 */
	public StatementContext() {
		this.parent = null;
	}

	
	/*
	 * Private constructor to create a context from a parent context. The child
	 * context will inherit the current XPath context and output context node.
	 */
	private StatementContext(StatementContext parent) {
		this.parent = parent;
		this.xpathContext = parent.getXPathContext();
		this.outputNode = parent.getOutputNode();
	}

	
	/**
	 * Returns a new {@code StatementContext} with this context as its parent.
	 * 
	 * @return a new child context
	 */
	public StatementContext newChild() {
		return new StatementContext(this);
	}


	/**
	 * Returns the current XPath context.
	 * 
	 * @return a context object, not null
	 */
	public Object getXPathContext() {
		return xpathContext;
	}


	/**
	 * Sets the current XPath context. A null value is not allowed.
	 * 
	 * @param xpathContext a context node, not null
	 */
	public void setContextNode(Object xpathContext) {
		Objects.requireNonNull(xpathContext, "xpathContext must not be null");
		this.xpathContext = xpathContext;
	}

	
	/**
	 * Returns the current output context node.
	 * 
	 * @return a data node, initially null
	 */
	public DataNode getOutputNode() {
		return outputNode;
	}


	/**
	 * Sets the current output context node. A null value is not allowed.
	 * 
	 * @param outputNode a data node, not null
	 */
	public void setOutputNode(DataNode outputNode) {
		Objects.requireNonNull(outputNode, "outputNode must not be null");
		this.outputNode = outputNode;
	}


	/*
	 * Private helper method to return a lookup key in Clark notation
	 */
	private static final String key(String namespaceURI, String localName) {
		return (namespaceURI == null) ? localName : "{" + namespaceURI + "}" + localName;
	}


	/**
	 * Sets the value of a variable in this context. The variable will be created if
	 * it does not exist, and overwritten if it does.
	 * <p>
	 * The variable is optionally associated with a namespace URI.
	 * 
	 * @param namespaceURI namespace URI of the variable, may be null
	 * @param localName    local name of the variable, not null
	 * @param value        value to be set, may be null
	 */
	public void setVariableValue(String namespaceURI, String localName, Object value) {
		
		Objects.requireNonNull(localName, "localName must not be null");
		this.variables.put(key(namespaceURI, localName), value);
	}


	/**
	 * Returns the value of a variable specified by a local name and optional
	 * namespace URI. If the variable is found in the current context or in any
	 * ancestor context, its value will be returned. Otherwise an
	 * {@code UnresolvableException} is thrown.
	 * 
	 * @param namespaceURI namespace URI of the variable, may be null
	 * @param prefix       namespace prefix of the variable
	 * @param localName    local name of the variable, not null
	 */
	@Override
	public Object getVariableValue(String namespaceURI, String prefix, String localName) throws UnresolvableException {

		Objects.requireNonNull(localName, "localName must not be null");
		final String key = key(namespaceURI, localName);

		if (this.variables.containsKey(key))
			return this.variables.get(key);

		if (parent != null)
			return parent.getVariableValue(namespaceURI, prefix, localName);

		final String pfxname = (prefix == null || prefix.isEmpty()) ? localName : prefix + ":" + localName;
		throw new UnresolvableException("variable '" + pfxname + "' not found");
	}

	
	/**
	 * Returns the context of a variable specified by a local name and optional
	 * namespace URI. If found in the current context, this will be returned.
	 * Otherwise, the first ancestor context that contains it is returned, or null
	 * if the variable is not found at all.
	 * 
	 * @param namespaceURI namespace URI of the variable, may be null
	 * @param localName    local name of the variable, not null
	 * @return the context of the variable, may be null
	 */
	public StatementContext getVariableContext(String namespaceURI, String localName) {

		Objects.requireNonNull(localName, "localName must not be null");

		if (this.variables.containsKey(key(namespaceURI, localName)))
			return this;

		if (parent != null)
			return parent.getVariableContext(namespaceURI, localName);

		return null;
	}

}

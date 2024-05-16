package be.baur.sdt;

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
 * In addition, the statement context provides the current context node for the
 * evaluation of XPath expressions, and the current output node that will be the
 * parent of newly created nodes.
 * 
 * @see VariableContext
 */
public class StatementContext implements VariableContext {

	private final StatementContext parent; // parent of this context
    private final Map<String, Object> variables = new HashMap<String, Object>();	

    private Object contextNode = null; // the current context node, initially null!
    private DataNode outputNode = new DataNode("output"); // the output document node
    
	/**
	 * Creates a {@code StatementContext}.
	 */
	public StatementContext() {
		this.parent = null;
	}

	
	/*
	 * Private constructor to create a context from a parent context. The child
	 * context will inherit the current context node and current output node.
	 */
	private StatementContext(StatementContext parent) {
		this.parent = parent;
		this.contextNode = parent.getContextNode();
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
	 * Returns the current context node.
	 * 
	 * @return a context node, may be null
	 */
	public Object getContextNode() {
		return contextNode;
	}


	/**
	 * Sets the current context node. A null value is not allowed.
	 * 
	 * @param contextNode a context node, not null
	 */
	public void setContextNode(Object contextNode) {
		Objects.requireNonNull(contextNode, "contextNode must not be null");
		this.contextNode = contextNode;
	}

	
	/**
	 * Returns the output document node.
	 * 
	 * @return a data node, never null
	 */
	public DataNode getOutputNode() {
		return outputNode;
	}


	/**
	 * Sets the current output node. A null value is not allowed.
	 * 
	 * @param outputNode a Node, not null
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
	 * Sets the value of a variable in this context. The variable will be
	 * created if it does not exist, and overwritten if it does.
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


	/*
	 * Private recursive method that returns the value of a variable if this or any
	 * of the parent contexts contain the specified variable key. Otherwise, an
	 * UnresolvableException is thrown. The pfxname is the name of the variable
	 * including the namespace prefix.
	 */
	private Object getVariableValueByKey(String key, String pfxname) throws UnresolvableException {

		if (this.variables.containsKey(key))
			return this.variables.get(key);

		if (parent == null)
			throw new UnresolvableException("variable '" + pfxname + "' not found");

		return parent.getVariableValueByKey(key, pfxname);
	}


	/**
	 * Returns the value of an XPath variable based on the namespace URI and local
	 * name of the variable-reference expression.
	 * 
	 * @throws NullPointerException if localName is null
	 */
	@Override
	public Object getVariableValue(String namespaceURI, String prefix, String localName) throws UnresolvableException {

		Objects.requireNonNull(localName, "localName must not be null");
		return getVariableValueByKey(key(namespaceURI, localName), prefix == "" ? localName : prefix + ":" + localName);
	}


	/*
	 * Private recursive method that returns true if this or any of the parent
	 * contexts contain the specified variable key. Otherwise false is returned.
	 */
	private boolean containsVariableKey(String key) {
		
		if (this.variables.containsKey(key))
			return true;
		
		return (parent == null) ? false : parent.containsVariableKey(key);
	}


	/**
	 * Returns whether this context can resolve the specified XPath variable.
	 * 
	 * @param namespaceURI namespace URI of the variable, may be null
	 * @param localName    local name of the variable, not null
	 * @return true if the variable can be resolved
	 */
	public boolean hasVariable(String namespaceURI, String localName) {

		Objects.requireNonNull(localName, "localName must not be null");
		return containsVariableKey(key(namespaceURI, localName));
	}

}

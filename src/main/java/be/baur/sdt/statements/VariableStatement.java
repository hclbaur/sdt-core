package be.baur.sdt.statements;

import java.util.List;
import java.util.Objects;

import be.baur.sda.Node;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Attribute;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The {@code VariableStatement} evaluates an XPath expression and assigns
 * the result to a variable. Unlike parameters, variables cannot be overwritten
 * by the transformation context.
 * 
 * @see ParamStatement
 */
public class VariableStatement extends XPathStatement {

	/**
	 * Creates a {@code VariableStatement}.
	 * 
	 * @param name  the name of the variable, not null
	 * @param xpath the XPath to be evaluated, not null
	 * @throws IllegalArgumentException if name is invalid
	 */
	public VariableStatement(String name, SDAXPath xpath) {
		super(Statements.VARIABLE.tag, xpath);
		Objects.requireNonNull(name, "name must not be null");
		if (! isVarName(name))
			throw new IllegalArgumentException("name '" + name + "' is invalid");
		setValue(name); // variable is stored in the node value, somewhat icky
	}


	/**
	 * Returns true if {@code name} is a valid variable name, and false otherwise.
	 * 
	 * @param name a variable name
	 * @return true if name is valid
	 */
	public static boolean isVarName(String name) {
		// No attempt is made to check if name is a valid XPath variable name
		// (see https://www.w3.org/TR/REC-xml/#NT-Name). At least, we disallow
		// the declaration of variables with a namespace prefix (for now).
		return !name.contains(":");
	}


	@SuppressWarnings("rawtypes")
	@Override
	public void execute(TransformContext tracon, StatementContext stacon) throws TransformException {
		/*
		 * Execution: Execution: create an XPath from the statement expression, set the
		 * variable context, and evaluate. The resulting value is used to add a new
		 * variable to the statement context (or overwrite an existing variable with
		 * the same name).
		 */
		try {
			SDAXPath xpath = new SDAXPath(getExpression());
			xpath.setVariableContext(stacon);
			Object value = xpath.evaluate(stacon.getContextNode());
			
			if (value instanceof List && ((List) value).size() == 1) {
				value = ((List) value).get(0); // replace a list of one node with that node
			}
			
			stacon.setVariableValue(null, getValue(), value);
		
		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}
	
	
	/**
	 * @return a node representing<br>
	 *         <code>variable "<i>name</i>" { select "<i>expression</i>" }</code>
	 */
	public Node toNode() {
		Node node = new Node(Statements.VARIABLE.tag, getValue());
		node.add( new Node(Attribute.SELECT.tag, getExpression()) ); 
		return node;
	}

}

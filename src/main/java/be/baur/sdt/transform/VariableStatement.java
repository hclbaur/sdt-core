package be.baur.sdt.transform;

import java.util.List;
import java.util.Objects;

import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The {@code VariableStatement} evaluates an XPath expression and assigns the
 * result to a named variable. Unlike parameters, variables cannot be supplied
 * by the transformation context, and are mutable during transform execution.
 * 
 * @see ParamStatement
 */
public class VariableStatement extends XPathStatement {

	private String varName; // name of the variable assigned by this statement


	/**
	 * Creates a {@code VariableStatement}.
	 * 
	 * @param name  the name of the variable, not null
	 * @param xpath the XPath to be evaluated, not null
	 * @throws IllegalArgumentException if name is invalid
	 */
	public VariableStatement(String name, XPath xpath) {
		super(xpath); setVarName(name);
	}


	/**
	 * Returns the name of the variable assigned by this statement.
	 * 
	 * @return a variable name, not null or empty
	 */
	public String getVarName() {
		return varName;
	}


	/**
	 * Sets the name of the variable assigned by this statement.
	 * 
	 * @param name the name of the variable, not null or empty
	 * @throws IllegalArgumentException if name is invalid
	 */
	public void setVarName(String name) {
		Objects.requireNonNull(name, "name must not be null");
		if (! isVarName(name))
			throw new IllegalArgumentException("name '" + name + "' is invalid");
		varName = name;
	}


	/**
	 * Determines if {@code name} is a valid variable name.
	 * 
	 * @param name a variable name
	 * @return true or false
	 */
	public static boolean isVarName(String name) {
		// No attempt is made to check if name is a valid XSLT variable name
		// (see https://www.w3.org/TR/REC-xml-names/#NT-QName). At least, we disallow
		// the declaration of variables with a namespace prefix (for now).
		return !(name == null || name.isEmpty() || name.contains(":"));
	}


	@Override @SuppressWarnings("rawtypes")
	void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context, and evaluate. The resulting value is used to add a new variable to
		 * the statement context or overwrite an existing variable with the same name.
		 */
		try {
			XPath xpath = new SDAXPath(getExpression());
			xpath.setVariableContext(staco);
			Object value = xpath.evaluate(staco.getContextNode());

			if (value instanceof List && ((List) value).size() == 1) {
				value = ((List) value).get(0); // replace a list of one node with that node
			}

			StatementContext vcx = staco.getVariableContext(null, varName);
			if (vcx != null) // existing variable, will be updated
				vcx.setVariableValue(null, varName, value);
			else // new variable, add to current statement context
				staco.setVariableValue(null, varName, value);

		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}
	
	
	/**
	 * @return a data node representing:<br><br>
	 *         <code>variable "<i>name</i>" { select "<i>expression</i>" }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Keyword.VARIABLE.tag, varName);
		node.add( new DataNode(Keyword.SELECT.tag, getExpression()) ); 
		return node;
	}

}

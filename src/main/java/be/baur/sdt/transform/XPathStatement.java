package be.baur.sdt.transform;

import java.util.Objects;

import org.jaxen.XPath;

/**
 * The abstract superclass of all transform statements that evaluate at least
 * one XPath expression.
 * 
 * @see Statement
 */
public abstract class XPathStatement extends Statement {
	/*
	 * These statements are associated with an XPath expression that is evaluated at
	 * run-time when the transform is executed. Because an XPath object is neither
	 * re-usable nor thread-safe, and its evaluation depends on a context, there is
	 * no point to keep it as a class field. So instead, we just save the expression
	 * text of the object that is passed in the constructor. This places the burden
	 * of creating a valid XPath on the caller.
	 */
	private final String expression;


	/**
	 * Creates a statement from an XPath object.
	 * 
	 * @param xpath an XPath object, not null
	 */
	public XPathStatement(XPath xpath) {
		expression = Objects.requireNonNull(xpath, "xpath must not be null").toString();
	}


	/**
	 * Returns the XPath expression text associated with this statement.
	 * 
	 * @return an expression string, never null or empty
	 */
	public String getExpression() {
		return expression;
	}

}

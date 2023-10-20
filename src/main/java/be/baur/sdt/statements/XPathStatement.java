package be.baur.sdt.statements;

import java.util.Objects;

import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The abstract superclass of all transform statements that evaluate an XPath
 * expression.
 * 
 * @see Statement
 */
public abstract class XPathStatement extends Statement {
	/*
	 * These statements are associated with an XPath expression that is evaluated at
	 * run-time when the transform is executed. Because an XPath object is not
	 * re-usable or thread-safe, and its evaluation depends on a context, there is
	 * no point to encapsulate it in this class. So instead, we merely save the
	 * expression text of the object that is passed in the constructor. This places
	 * the burden of creating an XPath object on the caller, so we do not have to
	 * deal with exceptions and still be sure that the expression is actually valid.
	 */
	private final String expression;


	/**
	 * Creates an {@code XPathStatement} from an XPath object.
	 * 
	 * @param name a valid statement name, see {@link Statements}
	 * @param xpath the XPath object, not null
	 */
	public XPathStatement(SDAXPath xpath) {
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

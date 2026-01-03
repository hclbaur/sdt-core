package be.baur.sdt.xpath;

import java.time.ZonedDateTime;

import org.jaxen.Function;
import org.jaxen.FunctionContext;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.UnresolvableException;
import org.jaxen.XPathFunctionContext;

import be.baur.sdt.xpath.function.CompareNumberFunction;
import be.baur.sdt.xpath.function.CompareStringFunction;
import be.baur.sdt.xpath.function.CurrentDateTimeFunction;
import be.baur.sdt.xpath.function.DateTimeFunction;
import be.baur.sdt.xpath.function.DateTimeToLocalFunction;
import be.baur.sdt.xpath.function.DateTimeToMillisFunction;
import be.baur.sdt.xpath.function.DateTimeToTimeZoneFunction;
import be.baur.sdt.xpath.function.DocumentNodeFunction;
import be.baur.sdt.xpath.function.FormatDateTimeFunction;
import be.baur.sdt.xpath.function.LeftFunction;
import be.baur.sdt.xpath.function.MillisToDateTimeFunction;
import be.baur.sdt.xpath.function.ParseDateTimeFunction;
import be.baur.sdt.xpath.function.ParseSDAFunction;
import be.baur.sdt.xpath.function.RenderSDAFunction;
import be.baur.sdt.xpath.function.RightFunction;
import be.baur.sdt.xpath.function.StringJoinFunction;
import be.baur.sdt.xpath.function.SystemTimeZoneFunction;
import be.baur.sdt.xpath.function.TimestampFunction;
import be.baur.sdt.xpath.function.TokenizeFunction;

/**
 * A <code>FunctionContext</code> providing the core XPath functions plus
 * Jaxen extensions, all SDT extensions and evaluation metadata to support
 * deterministic and/or context-dependent functions.
 * 
 * @see FunctionContext
 */
public class SDTFunctionContext implements FunctionContext {
	
	// this class is a wrapper around the default XPath context
	private static final SimpleFunctionContext FC = new XPathFunctionContext();
	
	// Namespace prefix and URI of SDT functions
	public static final String FUNCTIONS_NS_PFX = "sdt";
	public static final String FUNCTIONS_NS_URI = "be.baur.sdt.xpath.function";

	// Namespace prefix and URI of W3C XPath functions
	public static final String W3C_FUNCTIONS_NS_PFX = "fn";
	public static final String W3C_FUNCTIONS_NS_URI = "http://www.w3.org/2005/xpath-functions";
	
	// add SDT extensions to the core Xpath functions and Jaxen extensions
	static {
		FC.registerFunction(FUNCTIONS_NS_URI, CompareNumberFunction.NAME, new CompareNumberFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, CompareStringFunction.NAME, new CompareStringFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, CurrentDateTimeFunction.NAME, new CurrentDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DateTimeFunction.NAME, new DateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DateTimeToLocalFunction.NAME, new DateTimeToLocalFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DateTimeToMillisFunction.NAME, new DateTimeToMillisFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DateTimeToTimeZoneFunction.NAME, new DateTimeToTimeZoneFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DocumentNodeFunction.NAME, new DocumentNodeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, FormatDateTimeFunction.NAME, new FormatDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, LeftFunction.NAME, new LeftFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, MillisToDateTimeFunction.NAME, new MillisToDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, ParseDateTimeFunction.NAME, new ParseDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, ParseSDAFunction.NAME, new ParseSDAFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, RenderSDAFunction.NAME, new RenderSDAFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, RightFunction.NAME, new RightFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, SystemTimeZoneFunction.NAME, new SystemTimeZoneFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, TimestampFunction.NAME, new TimestampFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, TokenizeFunction.NAME, new TokenizeFunction());
		
		FC.registerFunction(W3C_FUNCTIONS_NS_URI, StringJoinFunction.NAME, new StringJoinFunction());
	}


	private ZonedDateTime currentDateTime = null; // current context date and time


	/**
	 * Create a new SDT function context.
	 */
	public SDTFunctionContext() {}


	@Override
	public Function getFunction(String namespaceURI, String prefix, String localName) throws UnresolvableException {
		return FC.getFunction(namespaceURI, prefix, localName);
	}


	// Supporting methods for deterministic and/or context-dependent functions


	/**
	 * Returns the current system's clock date and time (with time zone) for this
	 * context. The result is deterministic; multiple invocations will return the
	 * same result.
	 * 
	 * @return a zoned date time
	 */
	public ZonedDateTime getCurrentDateTime() {

		if (currentDateTime == null)
			currentDateTime = ZonedDateTime.now();
		return currentDateTime;
	}
}

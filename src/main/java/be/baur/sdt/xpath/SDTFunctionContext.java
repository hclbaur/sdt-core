package be.baur.sdt.xpath;

import org.jaxen.FunctionContext;
import org.jaxen.XPathFunctionContext;

import be.baur.sdt.SDT;
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
 * A <code>FunctionContext</code> implementing the core XPath function library,
 * plus Jaxen extensions, and all SDT extensions.
 * 
 * @see FunctionContext
 */
public class SDTFunctionContext extends XPathFunctionContext
{
    private static final SDTFunctionContext instance = new SDTFunctionContext();

	/**
	 * Returns the default SDT function context. Do not extend this with your own
	 * functions, as it will affect all XPath instances that use this global
	 * default. Instead, extend <code>SDTFunctionContext</code> or create a new
	 * instance to include your own functions.
	 *
	 * @return a function context
	 */
	public static SDTFunctionContext getInstance() {
		return instance;
	}

 
	/**
	 * Create a new XPath function context including the core XPath function
	 * library, plus Jaxen extensions, and all SDT extensions.
	 */
	public SDTFunctionContext() {
		
		super(true); // adds the core Xpath functions and Jaxen extensions
		
		registerFunction(SDT.FUNCTIONS_NS_URI, CompareNumberFunction.NAME, new CompareNumberFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, CompareStringFunction.NAME, new CompareStringFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, CurrentDateTimeFunction.NAME, new CurrentDateTimeFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, DateTimeFunction.NAME, new DateTimeFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, DateTimeToLocalFunction.NAME, new DateTimeToLocalFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, DateTimeToMillisFunction.NAME, new DateTimeToMillisFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, DateTimeToTimeZoneFunction.NAME, new DateTimeToTimeZoneFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, DocumentNodeFunction.NAME, new DocumentNodeFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, FormatDateTimeFunction.NAME, new FormatDateTimeFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, LeftFunction.NAME, new LeftFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, MillisToDateTimeFunction.NAME, new MillisToDateTimeFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, ParseDateTimeFunction.NAME, new ParseDateTimeFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, ParseSDAFunction.NAME, new ParseSDAFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, RenderSDAFunction.NAME, new RenderSDAFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, RightFunction.NAME, new RightFunction());
		registerFunction(SDT.W3CFUNCTIONS_NS_URI, StringJoinFunction.NAME, new StringJoinFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, SystemTimeZoneFunction.NAME, new SystemTimeZoneFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, TimestampFunction.NAME, new TimestampFunction());
		registerFunction(SDT.FUNCTIONS_NS_URI, TokenizeFunction.NAME, new TokenizeFunction());
	}

}

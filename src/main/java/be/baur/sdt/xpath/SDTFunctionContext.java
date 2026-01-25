package be.baur.sdt.xpath;

import java.time.Instant;
import java.time.ZoneId;
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
import be.baur.sdt.xpath.function.ImplicitTimeZoneFunction;
import be.baur.sdt.xpath.function.LeftFunction;
import be.baur.sdt.xpath.function.MillisToDateTimeFunction;
import be.baur.sdt.xpath.function.ParseDateTimeFunction;
import be.baur.sdt.xpath.function.ParseSDAFunction;
import be.baur.sdt.xpath.function.RenderSDAFunction;
import be.baur.sdt.xpath.function.RightFunction;
import be.baur.sdt.xpath.function.StringJoinFunction;
import be.baur.sdt.xpath.function.SystemDateTimeFunction;
import be.baur.sdt.xpath.function.SystemTimeZoneFunction;
import be.baur.sdt.xpath.function.TimeZoneFromDateTime;
import be.baur.sdt.xpath.function.TokenizeFunction;

/**
 * A <code>FunctionContext</code> providing the core XPath functions plus
 * Jaxen extensions, all SDT extensions and evaluation metadata to support
 * deterministic and/or context-dependent functions.
 * 
 * @see FunctionContext
 */
public class SDTFunctionContext implements FunctionContext {
	
	// Namespace prefix and URI of SDT provided functions
	public static final String FUNCTIONS_NS_PFX = "sdt";
	public static final String FUNCTIONS_NS_URI = "be.baur.sdt.xpath.function";

	// Namespace prefix and URI of W3C like XPath functions
	public static final String W3C_FUNCTIONS_NS_PFX = "fn";
	public static final String W3C_FUNCTIONS_NS_URI = "http://www.w3.org/2005/xpath-functions";
	
	// the implicit context time zone can be specified in a property
	public static final String TZ_PROPERTY = "sdt.context.timezone";


	// this class is a wrapper around the default XPath context
	private static final SimpleFunctionContext FC = new XPathFunctionContext();
	
	static {

		// Add SDT extensions to the core Xpath functions and Jaxen extensions
		FC.registerFunction(FUNCTIONS_NS_URI, CompareNumberFunction.NAME, new CompareNumberFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, CompareStringFunction.NAME, new CompareStringFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, CurrentDateTimeFunction.NAME, new CurrentDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DateTimeFunction.NAME, new DateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DateTimeToLocalFunction.NAME, new DateTimeToLocalFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DateTimeToMillisFunction.NAME, new DateTimeToMillisFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DateTimeToTimeZoneFunction.NAME, new DateTimeToTimeZoneFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, DocumentNodeFunction.NAME, new DocumentNodeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, FormatDateTimeFunction.NAME, new FormatDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, ImplicitTimeZoneFunction.NAME, new ImplicitTimeZoneFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, LeftFunction.NAME, new LeftFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, MillisToDateTimeFunction.NAME, new MillisToDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, ParseDateTimeFunction.NAME, new ParseDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, ParseSDAFunction.NAME, new ParseSDAFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, RenderSDAFunction.NAME, new RenderSDAFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, RightFunction.NAME, new RightFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, SystemDateTimeFunction.NAME, new SystemDateTimeFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, SystemTimeZoneFunction.NAME, new SystemTimeZoneFunction());
		FC.registerFunction(FUNCTIONS_NS_URI, TimeZoneFromDateTime.NAME, new TimeZoneFromDateTime());
		FC.registerFunction(FUNCTIONS_NS_URI, TokenizeFunction.NAME, new TokenizeFunction());
		// W3C like functions
		FC.registerFunction(W3C_FUNCTIONS_NS_URI, StringJoinFunction.NAME, new StringJoinFunction());

	}


	private static ZoneId implicitTimeZone;	// static (!) context time zone id
	private ZonedDateTime currentDateTime; // this context date and time


	/**
	 * Create a new SDT function context.
	 */
	public SDTFunctionContext() {}


	@Override
	public Function getFunction(String namespaceURI, String prefix, String localName) throws UnresolvableException {
		return FC.getFunction(namespaceURI, prefix, localName);
	}


	// Supporting (final) methods for deterministic and/or context-dependent functions


	/**
	 * Returns the system's clock date and time (in the implicit time zone) for this
	 * context.
	 * <p>
	 * <i>Note:</i> multiple invocations for this context instance will return the
	 * same result.
	 * 
	 * @return a zoned date time
	 */
	public final ZonedDateTime getCurrentDateTime() {
		
		if (currentDateTime == null)
			currentDateTime = Instant.from(ZonedDateTime.now()).atZone(getImplicitTimeZone());
		return currentDateTime;
	}


	/**
	 * Returns the implicit time zone id for this context. The time-zone id is
	 * determined in the following way:
	 * <p>
	 * 1) from the value of the system property {@code sdt.context.timezone}<br>
	 * 2) if not set or empty, the value of {@link ZoneId#systemDefault()}<br>
	 * 3) or the value of ZoneId#of("UTC") as the ultimate fallback<br>
	 * <p>
	 * <i>Note:</i> the time-zone is a static property that cannot be changed after
	 * initialization; multiple invocations will return the same result.
	 * 
	 * @return a zone id, not null
	 */
	public final ZoneId getImplicitTimeZone() {
		
		if (implicitTimeZone == null) {
			ZoneId zoneid;
			try {
				String timezone = System.getProperty(TZ_PROPERTY);
				if (timezone == null || timezone.isEmpty())
					zoneid = ZoneId.systemDefault();
				else
					zoneid = ZoneId.of(timezone);
			} catch (Exception e) {
				zoneid = ZoneId.of("UTC"); // ultimate fallback
			}
			implicitTimeZone = zoneid;
		}
			
		return implicitTimeZone;
	}

//	public static void main(String[] args) {
//		SDTFunctionContext c1 = new SDTFunctionContext();
//		c1.getImplicitTimeZone(); c1.getCurrentDateTime(); c1.getCurrentDateTime();
//		SDTFunctionContext c2 = new SDTFunctionContext();
//		c2.getImplicitTimeZone(); c2.getCurrentDateTime(); c2.getCurrentDateTime();
//	}

}

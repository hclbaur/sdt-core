package be.baur.sdt.xpath;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
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
 * An XPath implementation for the SDA object model.
 * 
 * This is the main entry point for matching an XPath against an SDA node tree.
 * You create a compiled XPath object, then match it against one or more context
 * nodes using the {@link #selectNodes(Object)} method, as in the following
 * example:
 *
 * <pre>
 * XPath path = new SDAXPath("<i>expression</i>");
 * List results = path.selectNodes(node);
 * </pre>
 *
 * @see BaseXPath
 */
public class SDAXPath extends BaseXPath {

	private static final long serialVersionUID = 368489177460992020L;
	
	static {	
		// add all SDT specific functions to the default context
		XPathFunctionContext ctx = (XPathFunctionContext) XPathFunctionContext.getInstance();
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "compare-number", new CompareNumberFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "compare-string", new CompareStringFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "current-dateTime", new CurrentDateTimeFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "dateTime", new DateTimeFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "dateTime-to-local", new DateTimeToLocalFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "dateTime-to-millis", new DateTimeToMillisFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "dateTime-to-timezone", new DateTimeToTimeZoneFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "document-node", new DocumentNodeFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "format-dateTime", new FormatDateTimeFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "left", new LeftFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "millis-to-dateTime", new MillisToDateTimeFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "parse-dateTime", new ParseDateTimeFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "parse-sda", new ParseSDAFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "render-sda", new RenderSDAFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "right", new RightFunction());
		ctx.registerFunction(SDT.W3CFUNCTIONS_NS_URI, "string-join", new StringJoinFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "system-timezone", new SystemTimeZoneFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "timestamp", new TimestampFunction());
		ctx.registerFunction(SDT.FUNCTIONS_NS_URI, "tokenize", new TokenizeFunction());
	}
	
	/**
	 * Create a new <code>SDAXPath</code> from an XPath expression.
	 *
	 * @param expression the XPath expression
	 * 
	 * @throws JaxenException if there is a syntax error in the expression
	 */
	public SDAXPath(String expression) throws JaxenException {
		
		super(expression, DocumentNavigator.getInstance());
		this.addNamespace(SDT.FUNCTIONS_NS_PFX, SDT.FUNCTIONS_NS_URI);
		this.addNamespace(SDT.W3CFUNCTIONS_NS_PFX, SDT.W3CFUNCTIONS_NS_URI);
	}

}

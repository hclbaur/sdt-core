package be.baur.sdt.xpath.function;

import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.FunctionContext;

import be.baur.sdt.xpath.SDTFunctionContext;

/**
 * <code><i>date-time</i> sdt:current-dateTime()</code><br>
 * <p>
 * Returns the current date and time (in extended ISO-8601 format) from the SDT
 * context in the implicit time zone.
 * <p>
 * <i>Note:</i> this function is deterministic and context-dependent; multiple
 * invocations within the same execution context will return the same result.
 * 
 * @see SDTFunctionContext
 * @see <a href=
 *      "https://www.w3.org/TR/xpath-functions/#func-current-dateTime">Section
 *      15.3 of the XPath Specification</a>
 * @see SystemDateTimeFunction
 */
public class CurrentDateTimeFunction implements Function
{
	public static final String NAME = "current-dateTime";
	
    /**
     * Create a new <code>CurrentDateTimeFunction</code> object.
     */
    public CurrentDateTimeFunction() {}
    
	/**
	 * Returns the current date and time.
	 *
	 * @param context the expression context
	 * @param args    an empty list
	 * @return a zoned date-time string
	 * @throws FunctionCallException if <code>args</code> is not empty
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
	{
		if (args.size() == 0)
			return DateTimeFunction.format(evaluate(context));

		throw new FunctionCallException(NAME + "() requires no arguments.");
	}

  
	/**
	 * Returns the current date and time.
	 * 
	 * @param context the expression context
	 * @return a zoned date-time, not null
	 */
	public static TemporalAccessor evaluate(Context context) {
		
		FunctionContext fc = context.getContextSupport().getFunctionContext();
		
		if (fc instanceof SDTFunctionContext)
			return ((SDTFunctionContext) fc).getCurrentDateTime();

		throw new AssertionError(NAME + "() not called from an SDT context.");
	}

}

package be.baur.sdt.xpath.function;

import java.time.ZoneId;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.FunctionContext;

import be.baur.sdt.xpath.SDTFunctionContext;

/**
 * <code><i>time-zone</i> sdt:implicit-timezone()</code><br>
 * <p>
 * Returns the value of the implicit time zone ID from the SDT context. This is
 * the time zone to be used when a date-time value that does not have a time
 * zone component is used in a comparison or arithmetic operation.
 * <p>
 * <i>Note:</i> the result is deterministic and context-dependent; multiple
 * invocations within the same execution context will return the same result.
  * <p>
 * Example:
 * <p>
 * <code>sdt:implicit-timezone()</code>
 * returns <code>Europe/Amsterdam</code>.<br>
 * 
 * @see SDTFunctionContext
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/List_of_tz_database_time_zones">List of
 *      tz database time zones</a>
 */
public final class ImplicitTimeZoneFunction implements Function
{
	public static final String NAME = "implicit-timezone";
	
    /**
     * Create a new <code>ImplicitTimeZoneFunction</code> object.
     */
    public ImplicitTimeZoneFunction() {}
    
	/**
	 * Returns the implicit time zone.
	 *
	 * @param context will be ignored
	 * @param args    an empty list
	 * @return a time zone
	 * @throws FunctionCallException if <code>args</code> is not empty or an
	 *                               exception occurred during evaluation.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (!args.isEmpty())
			throw new FunctionCallException(NAME + "() requires no arguments.");

		return evaluate(context).toString();
	}

  
	/**
	 * Returns the ID of the implicit time zone.
	 * 
	 * @return a zone id, not null
	 */
	public static ZoneId evaluate(Context context) {

		FunctionContext fc = context.getContextSupport().getFunctionContext();

		if (fc instanceof SDTFunctionContext)
			return ((SDTFunctionContext) fc).getImplicitTimeZone();
		
		throw new AssertionError(NAME + "() not called from an SDT context.");
	}

}

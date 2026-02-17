package be.baur.sdt.function.dtm;

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
 * zone component is used in a comparison or arithmetic operation. This is not
 * necessarily equal to the system clock default.
 * <p>
 * <i>Note:</i> this function is deterministic and context-dependent; multiple
 * invocations within the same execution context will return the same result.
 * <p>
 * Example:
 * <p>
 * <code>sdt:implicit-timezone()</code> returns
 * <code>Europe/Amsterdam</code>.<br>
 * 
 * @see SDTFunctionContext#getImplicitTimeZone
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
	 * @param context the expression context
	 * @param args    an empty list
	 * @return a time zone id
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (!args.isEmpty())
			throw new FunctionCallException(NAME + "() requires no arguments.");

		return evaluate(NAME, context).toString();
	}

  
	/**
	 * Returns the ID of the implicit time zone.
	 * 
	 * @param fun name of the calling function
	 * @param context the expression context
	 * @return a time zone id, not null
	 * @throws FunctionCallException if not called from an SDT context
	 */
	public static ZoneId evaluate(String fun, Context context) throws FunctionCallException {

		FunctionContext fc = context.getContextSupport().getFunctionContext();

		if (fc instanceof SDTFunctionContext)
			return ((SDTFunctionContext) fc).getImplicitTimeZone();
		
		throw new FunctionCallException(fun + "() not called from an SDT context.");
	}

}

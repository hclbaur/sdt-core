package be.baur.sdt.xpath.function;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>date-time</i> sdt:system-dateTime()</code><br>
 * <p>
 * Returns the current date and time (in extended ISO-8601) format from the
 * system clock in the default time zone.
 * <p>
 * Note: this function is non-deterministic and context-independent; multiple
 * invocations within the same execution context may return a different result.
 * 
 * @see CurrentDateTimeFunction
 */
public class SystemDateTimeFunction implements Function
{
	public static final String NAME = "system-dateTime";
	
    /**
     * Create a new <code>SystemDateTimeFunction</code> object.
     */
    public SystemDateTimeFunction() {}
    
	/**
	 * Returns the system date and time.
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
	 * Returns the system date and time.
	 * 
	 * @param context the expression context
	 * @return a zoned date-time, not null
	 */
	public static TemporalAccessor evaluate(Context context) {
		
		return ZonedDateTime.now();
	}

}

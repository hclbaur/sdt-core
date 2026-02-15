package be.baur.sdt.xpath.function.dtm;

import java.time.ZonedDateTime;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>date-time</i> sdt:system-dateTime()</code><br>
 * <p>
 * Returns the current date and time from the system clock in the default time
 * zone.
 * <p>
 * <i>Note</i>: this function is non-deterministic and context-independent;
 * multiple invocations within the same execution context may return a different
 * result.
 * 
 * @see CurrentDateTimeFunction
 */
public final class SystemDateTimeFunction implements Function
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
	 * @return a zoned date-time
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
	{
		if (args.size() == 0)
			return DateTimeFunction.format( evaluate() );

		throw new FunctionCallException(NAME + "() requires no arguments.");
	}

  
	/**
	 * Returns the system date and time.
	 * 
	 * @return a zoned date-time, not null
	 */
	private static ZonedDateTime evaluate() {
		
		return ZonedDateTime.now();
	}

}

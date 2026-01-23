package be.baur.sdt.xpath.function;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>number</i> sdt:dateTime-to-millis( <i>date-time</i> )</code><br>
 * <p>
 * Converts a date-time into a number of milliseconds elapsed since the epoch. A
 * negative number is returned for a point in time prior to the epoch.
 * <p>
 * <i>Note:</i> if a local date-time is supplied, the implicit time zone will be
 * used to calculate the offset from UTC. 
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime-to-millis('1970-01-01T00:00:00Z')</code> returns
 * <code>0</code>.<br>
 * 
 * @see ImplicitTimeZoneFunction
 */
public final class DateTimeToMillisFunction implements Function
{
	public static final String NAME = "dateTime-to-millis";
	
    /**
     * Create a new <code>DateTimeToMillisFunction</code> object.
     */
    public DateTimeToMillisFunction() {}
 

	/**
	 * Converts a date-time into the number of milliseconds elapsed since the epoch.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains one item.
	 * @return a number of milliseconds
	 * @throws FunctionCallException if <code>args</code> has more than one item or
	 *                               date-time conversion failed.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() == 1)
			return evaluate(args.get(0), context);

		throw new FunctionCallException(NAME + "() requires exactly one argument.");
	}


	/**
	 * Converts a date-time into the number of milliseconds elapsed since the epoch.
	 * 
	 * @param obj a date-time string
	 * @param context the expression context
	 * @return a number of milliseconds
	 * @throws FunctionCallException if date-time conversion failed.
	 */
	public static Double evaluate(Object obj, Context context) throws FunctionCallException {

		TemporalAccessor dtm = DateTimeFunction.evaluate(NAME, obj, context.getNavigator());

		if (dtm instanceof LocalDateTime)
			dtm = ((LocalDateTime) dtm).atZone(ImplicitTimeZoneFunction.evaluate(context));

		try {
			return (double) Instant.from(dtm).toEpochMilli();
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() conversion of '" + dtm + "' failed.", e);
		}
	}
}

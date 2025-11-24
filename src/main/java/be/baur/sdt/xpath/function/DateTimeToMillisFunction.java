package be.baur.sdt.xpath.function;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

/**
 * <code><i>number</i> sdt:dateTime-to-millis( <i>date-time</i> )</code><br>
 * <p>
 * Converts a zoned date-time into the number of milliseconds elapsed since the
 * epoch. A negative number is returned for a point in time prior to the epoch.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime-to-millis('1970-01-01T00:00:00Z')</code> returns
 * <code>0</code>.<br>
 */
public class DateTimeToMillisFunction implements Function
{

    /**
     * Create a new <code>DateTimeToMillisFunction</code> object.
     */
    public DateTimeToMillisFunction() {}
 

	/**
	 * Converts a zoned date-time string into the number of milliseconds elapsed
	 * since the epoch.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains one item.
	 * @return a number of milliseconds
	 * @throws FunctionCallException if <code>args</code> has more than one item, no
	 *                               zoned date-time was supplied or conversion
	 *                               failed.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() == 1)
			return evaluate(args.get(0), context.getNavigator());

		throw new FunctionCallException("dateTime-to-millis() requires exactly one argument.");
	}


	/**
	 * Converts a zoned date-time string into the number of milliseconds elapsed
	 * since the epoch.
	 * 
	 * @param obj a zoned date-time string
	 * @param nav the navigator used
	 * @return a number of milliseconds
	 * @throws FunctionCallException if no (zoned) date-time string was supplied or
	 *                               conversion failed.
	 */
	public static Double evaluate(Object obj, Navigator nav) throws FunctionCallException {

		TemporalAccessor dtm = DateTimeFunction.evaluate("dateTime-to-millis()", obj, nav);

		if (!(dtm instanceof ZonedDateTime))
			throw new FunctionCallException("dateTime-to-millis() requires a zoned date-time.");

		try {
			return (double) Instant.from(dtm).toEpochMilli();
		} catch (Exception e) {
			throw new FunctionCallException("dateTime-to-millis() conversion of '" + dtm + "' failed.", e);
		}
	}
}

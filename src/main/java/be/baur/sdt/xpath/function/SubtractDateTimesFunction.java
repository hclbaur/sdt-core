package be.baur.sdt.xpath.function;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

/**
 * <code><i>double</i> sdt:subtract-dateTimes( <i>date-time</i>, <i>date-time</i> )</code><br>
 * <p>
 * Returns the number of milliseconds elapsed between two date-times. The result
 * will be negative if the first argument precedes the second in time.
 * <p>
 * <code>sdt:subtract-dateTimes('1970-01-01T00:00:00+01:00', '1970-01-01T00:00:00Z')</code>
 * returns <code>-1.0</code>.<br>
 * <code>sdt:subtract-dateTimes(sdt:current-dateTime(),sdt:current-dateTime())</code>
 * returns <code>0.0</code>.<br>
 * <code>sdt:subtract-dateTimes('1970-01-01T00:00:00Z', '1970-01-01T00:00:00+01:00')</code>
 * returns <code>1.0</code>.<br>
 * <p>
 * <i>Note:</i> if either argument is a local date-time, the implicit time zone
 * will be used to calculate the offset from UTC.
 */
public final class SubtractDateTimesFunction implements Function
{
	public static final String NAME = "subtract-dateTimes";
	
    /**
     * Create a new <code>SubtractDateTimesFunction</code> object.
     */
    public SubtractDateTimesFunction() {}

    
	/**
	 * Returns the number of milliseconds elapsed between two date-times.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two items
	 * @return a number of milliseconds
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 2)
			throw new FunctionCallException(NAME + "() requires two arguments.");

		return evaluate(args.get(0), args.get(1), context);
	}
    

	/**
	 * Returns the number of milliseconds elapsed between two date-times.
	 *
	 * @param dtm1 the first date-time
	 * @param dtm2 the second date-time
	 * @param nav  the navigator used
	 * @return a number of milliseconds
	 * @throws FunctionCallException if evaluation failed
	 */
	private static Double evaluate(Object dtm1, Object dtm2, Context context) throws FunctionCallException {

		final Navigator nav = context.getNavigator();
		final ZoneId zid = ImplicitTimeZoneFunction.evaluate(context);
		
		ZonedDateTime zdtm1 = DateTimeToTimeZoneFunction.ifLocal(DateTimeFunction.evaluate(NAME, dtm1, nav), zid);
		ZonedDateTime zdtm2 = DateTimeToTimeZoneFunction.ifLocal(DateTimeFunction.evaluate(NAME, dtm2, nav), zid);

		return (double) Duration.between(zdtm1, zdtm2).toMillis();
	}

}

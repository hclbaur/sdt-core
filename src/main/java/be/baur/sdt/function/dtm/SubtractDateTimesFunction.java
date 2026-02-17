package be.baur.sdt.function.dtm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

/**
 * <code><i>double</i> sdt:subtract-dateTimes( <i>date-time</i>, <i>date-time</i> )</code><br>
 * <p>
 * This function returns the number of seconds elapsed between two date-times,
 * with millisecond precision. This will be a <i>negative</i> number if the first
 * argument precedes the second in time:
 * <p>
 * <code>sdt:subtract-dateTimes('1970-01-01T00:00:00+01:00', '1970-01-01T00:00:00Z')</code>
 * returns <code>-3600.0</code>.<br>
 * <code>sdt:subtract-dateTimes(sdt:current-dateTime(),sdt:current-dateTime())</code>
 * returns <code>0.0</code>.<br>
 * <code>sdt:subtract-dateTimes('1970-01-01T00:00:00Z', '1970-01-01T00:00:00+01:00')</code>
 * returns <code>3600.0</code>.<br>
 * <p>
 * <i>Note:</i> if either argument is a local date-time, the implicit time zone
 * will be used to calculate the offset from UTC.
 */
public final class SubtractDateTimesFunction implements Function
{
	public static final String NAME = "subtract-dateTimes";
    private static final BigDecimal BILLION = BigDecimal.valueOf(1_000_000_000);
    
    /**
     * Create a new <code>SubtractDateTimesFunction</code> object.
     */
    public SubtractDateTimesFunction() {}

    
	/**
	 * Returns the number of seconds elapsed between two date-times.
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

		Double nanos = evaluate(NAME, args.get(0), args.get(1), context);
		return BigDecimal.valueOf(nanos).divide(BILLION, 3, RoundingMode.HALF_UP).doubleValue(); 
	}


	/**
	 * Returns the number of nanoseconds elapsed between two date-times.
	 *
	 * @param fun     name of the calling function
	 * @param dtm1    the first date-time
	 * @param dtm2    the second date-time
	 * @param context the expression context
	 * @return a number of nanoseconds
	 * @throws FunctionCallException if evaluation failed
	 */
	static Double evaluate(String fun, Object dtm1, Object dtm2, Context context) throws FunctionCallException {

		final Navigator nav = context.getNavigator();

		TemporalAccessor tac1 = DateTimeFunction.evaluate(fun, dtm1, nav);
		TemporalAccessor tac2 = DateTimeFunction.evaluate(fun, dtm2, nav);

		final boolean local1 = (tac1 instanceof LocalDateTime);
		final boolean local2 = (tac2 instanceof LocalDateTime);

		try {
			/* If either date-time is local, adjust it to the implicit time zone */
			if (local1 || local2) {

				ZoneId zid = ImplicitTimeZoneFunction.evaluate(NAME, context);
				if (local1)
					tac1 = DateTimeToTimeZoneFunction.evaluate(tac1, zid);
				if (local2)
					tac2 = DateTimeToTimeZoneFunction.evaluate(tac2, zid);
			}

			// both date-times will be zoned at this point
			return (double) Duration.between(Instant.from(tac2), Instant.from(tac1)).toNanos();

		} catch (Exception e) {
			throw new FunctionCallException(fun + "() failed to determine time difference.", e);
		}
	}

}

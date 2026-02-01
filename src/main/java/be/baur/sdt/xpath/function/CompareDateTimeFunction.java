package be.baur.sdt.xpath.function;

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
 * <code><i>double</i> sdt:compare-dateTime( <i>date-time</i>, <i>date-time</i> )</code><br>
 * <p>
 * Compares two instances in time. This function converts its arguments to
 * date-time and returns -1, 0 or 1, depending on whether the first argument
 * precedes, equals or 1 exceeds the second in time:
 * <p>
 * <code>sdt:compare-dateTime('1970-01-01T00:00:00+01:00', '1970-01-01T00:00:00Z')</code>
 * returns <code>-1.0</code>.<br>
 * <code>sdt:compare-dateTime(sdt:current-dateTime(),sdt:current-dateTime())</code>
 * returns <code>0.0</code>.<br>
 * <code>sdt:compare-dateTime('1970-01-01T00:00:00Z', '1970-01-01T00:00:00+01:00')</code>
 * returns <code>1.0</code>.<br>
 * <p>
 * <i>Note:</i> if either argument is a local date-time, the implicit time zone
 * will be used to compare it against the other.
 * <p>
 * This function can be used as a comparator in a sort statement.
 */
public final class CompareDateTimeFunction implements Function
{
	public static final String NAME = "compare-dateTime";
	
    /**
     * Create a new <code>CompareDateTimeFunction</code> object.
     */
    public CompareDateTimeFunction() {}

    
	/**
	 * Compares two date-time values, returning -1, 0 or 1.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two items
	 * @return a signum value
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
	 * Compares two instances in time, returning -1, 0 or 1.
	 *
	 * @param dtm1 the first date-time
	 * @param dtm2 the second date-time
	 * @param nav  the navigator used
	 * @return a signum value
	 * @throws FunctionCallException if evaluation failed
	 */
	static Double evaluate(Object dtm1, Object dtm2, Context context) throws FunctionCallException {

		final Navigator nav = context.getNavigator();

		TemporalAccessor tac1 = DateTimeFunction.evaluate(NAME, dtm1, nav);
		TemporalAccessor tac2 = DateTimeFunction.evaluate(NAME, dtm2, nav);

		final boolean local1 = (tac1 instanceof LocalDateTime);
		final boolean local2 = (tac2 instanceof LocalDateTime);

		/* If either date-time is local, adjust it to the implicit time zone */
		if (local1 || local2) {
			final ZoneId zid = ImplicitTimeZoneFunction.evaluate(context);
			if (local1)
				tac1 = DateTimeToTimeZoneFunction.evaluate(tac1, zid);
			if (local2)
				tac2 = DateTimeToTimeZoneFunction.evaluate(tac2, zid);
		}

		/*
		 * ZonedDateTime.compareTo() treats equal instances in time in different time
		 * zones as not equal, so I am using Instant.compareTo instead. Also note that
		 * at this point both date-times will be zoned, not local.
		 */
		return (double) Math.signum((Instant.from(tac1)).compareTo(Instant.from(tac2)));
	}

}

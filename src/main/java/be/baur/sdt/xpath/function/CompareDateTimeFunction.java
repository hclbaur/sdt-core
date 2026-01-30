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
 * If either argument is a local date-time, the implicit time zone will be used
 * to compare it against the other.
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
	 * Compares two date-time values, returning -1, 0 or 1. The implicit time zone
	 * is assumed for a local date-time.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two or three items.
	 * 
	 * @return a <code>Double</code>
	 * 
	 * @throws FunctionCallException if <code>args</code> has more or less than two
	 *                               items.
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
	 * @param obj1     the first object to be compared
	 * @param obj2     the second object to be compared
	 * @param nav      the navigator used
	 * 
	 * @return a <code>Double</code>
	 * @throws FunctionCallException 
	 */
	public static Double evaluate(Object obj1, Object obj2, Context context) throws FunctionCallException {

		final Navigator nav = context.getNavigator();

		TemporalAccessor dtm1 = DateTimeFunction.evaluate(NAME, obj1, nav);
		TemporalAccessor dtm2 = DateTimeFunction.evaluate(NAME, obj2, nav);

		final boolean local1 = (dtm1 instanceof LocalDateTime);
		final boolean local2 = (dtm2 instanceof LocalDateTime);

		/* If either date-time is local, adjust it to the implicit time zone */
		if (local1 || local2) {
			final ZoneId zid = ImplicitTimeZoneFunction.evaluate(context);
			if (local1)
				dtm1 = DateTimeToTimeZoneFunction.evaluate(dtm1, zid);
			if (local2)
				dtm2 = DateTimeToTimeZoneFunction.evaluate(dtm2, zid);
		}

		/*
		 * ZonedDateTime.compareTo() treats equal instances in time in different time
		 * zones as not equal, so I am using Instant.compareTo instead. Also note that
		 * at this point both date-times will be zoned, not local.
		 */
		return (double) Math.signum((Instant.from(dtm1)).compareTo(Instant.from(dtm2)));
	}

}

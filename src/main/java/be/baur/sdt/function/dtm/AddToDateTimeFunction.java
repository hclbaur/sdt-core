package be.baur.sdt.function.dtm;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;

/**
 * <code><i>date-time</i> add-to-dateTime( <i>date-time</i>, <i>hours</i>, <i>minutes</i>, <i>seconds</i> )</code><br>
 * <p>
 * Returns the result of adding a duration in hours, minutes and/or seconds to
 * the supplied date-time, where negative values can be used to subtract time.
 * When adding a duration (in contrast to adding a period) daylight savings will
 * be accounted for if a time zone ID is provided.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:add-to-dateTime('1968-02-28T23:00:00',1,0,0)</code> returns
 * <code>1968-02-29T00:00:00</code>.<br>
 * <code>sdt:add-to-dateTime('2025-03-30T01:00:00+01:00[Europe/Amsterdam]',1,0,0)</code>
 * returns <code>2025-03-30T03:00:00+02:00</code>.<br>
 * <code>sdt:add-to-dateTime('2025-10-26T03:00:00+02:00[Europe/Amsterdam]',-1,0,0)</code>
 * returns <code>2025-10-26T02:00:00+01:00</code>.<br>
 * 
 * @see AddPeriodToDateTimeFunction
 */
public final class AddToDateTimeFunction implements Function
{
	public static final String NAME = "add-to-dateTime";

    /**
     * Create a new <code>AddToDateTimeFunction</code> object.
     */
    public AddToDateTimeFunction() {}
 

	/**
	 * Adds a number of hours, minutes and/or seconds to a date-time.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains four items
	 * @return a date-time
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 4)
			throw new FunctionCallException(NAME + "() requires four arguments.");

		return DateTimeFunction.format(
			evaluate(args.get(0), args.get(1), args.get(2), args.get(3), context.getNavigator())
		);
	}


	/**
	 * Adds a number of hours, minutes and/or seconds to a date-time.
	 * 
	 * @param dtm     a date-time
	 * @param hours   a number of hours
	 * @param minutes a number of minutes
	 * @param seconds a number of seconds
	 * @param nav     the navigator used
	 * @return a date-time
	 * @throws FunctionCallException if evaluation failed
	 */
    private static TemporalAccessor evaluate(Object dtm, Object hours, Object minutes, Object seconds, Navigator nav) throws FunctionCallException {

		final TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, nav);

		Double d = NumberFunction.evaluate(hours, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() hours must be numeric.");
		long totalsec = 3600L * d.longValue();

		d = NumberFunction.evaluate(minutes, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() minutes must be numeric.");
		totalsec += 60L * d.longValue();

		d = NumberFunction.evaluate(seconds, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() seconds must be numeric.");
		totalsec += d.longValue();

		try {
			if (tac instanceof LocalDateTime)
				return ((LocalDateTime) tac).plus(totalsec, ChronoUnit.SECONDS);
			else
				return ((ZonedDateTime) tac).plus(totalsec, ChronoUnit.SECONDS);
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() failed to add " + totalsec + " to " + tac + ".", e);
		}
	}

}

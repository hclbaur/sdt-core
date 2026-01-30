package be.baur.sdt.xpath.function;

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
 * <code><i>date-time</i> add-to-dateTime( <i>date-time</i>, <i>days</i>, <i>hours</i>, <i>minutes</i>, <i>seconds</i> )</code><br>
 * <p>
 * Returns the result of adding a number of days, hours, minutes and/or seconds
 * to the supplied date-time, where negative values can be used to subtract
 * time. If a time zone ID is provided, daylight savings will be accounted for.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:add-to-dateTime('1968-02-28T23:00:00',0,1,0,0)</code> returns
 * <code>1968-02-29T00:00:00</code>.<br>
 * <code>sdt:add-to-dateTime('2025-03-30T01:00:00+01:00[Europe/Amsterdam]',0,1,0,0)</code>
 * returns <code>2025-03-30T03:00:00+02:00</code>.<br>
 * <code>sdt:add-to-dateTime('2025-10-26T03:00:00+02:00[Europe/Amsterdam]',0,-1,0,0)</code>
 * returns <code>2025-10-26T02:00:00+01:00</code>.<br>
 * 
 */
public final class AddToDateTimeFunction implements Function
{
	public static final String NAME = "add-to-dateTime";

    /**
     * Create a new <code>AddToDateTimeFunction</code> object.
     */
    public AddToDateTimeFunction() {}
 

	/**
	 * Adds a number of days, hours, minutes and/or seconds to a date-time.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains five items.
	 * @return a date-time string
	 * @throws FunctionCallException if <code>args</code> has more or less than five
	 *                               items or evaluation failed.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 5)
			throw new FunctionCallException(NAME + "() requires five arguments.");

		return DateTimeFunction.format(
			evaluate(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), context.getNavigator())
		);
	}


	/**
	 * Adds a number of days, hours, minutes and/or seconds to a date-time.
	 * 
	 * @param dtms    a date-time string
	 * @param days    a number of days
	 * @param hours   a number of hours
	 * @param minutes a number of minutes
	 * @param seconds a number of seconds
	 * @param nav     the navigator used
	 * @return a date-time string
	 * @throws FunctionCallException if no valid date-time was supplied or the
	 *                               addition failed.
	 */
    public static TemporalAccessor evaluate(Object dtms, Object days, Object hours, Object minutes, Object seconds, Navigator nav) throws FunctionCallException {

		final TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtms, nav);
		
		Double d = NumberFunction.evaluate(days, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() days must be numeric.");
		long totalsec = 86400L * d.longValue();

		d = NumberFunction.evaluate(hours, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() hours must be numeric.");
		totalsec += 3600L * d.longValue();

		d = NumberFunction.evaluate(minutes, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() minutes must be numeric.");
		totalsec += 60L * d.longValue();

		d = NumberFunction.evaluate(seconds, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() seconds must be numeric.");
		totalsec += d.longValue();

		if (tac instanceof LocalDateTime)
			return ((LocalDateTime) tac).plus(totalsec, ChronoUnit.SECONDS);
		else
			return ((ZonedDateTime) tac).plus(totalsec, ChronoUnit.SECONDS);

	}

}

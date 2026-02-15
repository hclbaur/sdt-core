package be.baur.sdt.xpath.function.dtm;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;

/**
 * <code><i>date-time</i> add-period-to-dateTime( <i>date-time</i>, <i>years</i>, <i>months</i>, <i>days</i> )</code><br>
 * <p>
 * Returns the result of adding a period of years, months and/or days to the
 * supplied date-time, where negative values can be used to subtract time.
 * Adding a period (in contrast to adding a duration) will never account for
 * daylight savings time, but maintain local time instead.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:add-period-to-dateTime('1968-03-31T12:00:00',0,-1,0)</code> returns
 * <code>1968-02-29T12:00:00</code>.<br>
 * <code>sdt:add-period-to-dateTime('1968-02-29T12:00:00',1,0,0)</code> returns
 * <code>1969-02-28T12:00:00</code>.<br>
 * <code>sdt:add-period-to-dateTime('2025-03-29T12:00:00+01:00[Europe/Amsterdam]',0,0,1)</code>
 * returns <code>2025-03-30T12:00:00+02:00[Europe/Amsterdam]</code>.<br>
 * <code>sdt:add-period-to-dateTime('2025-10-26T12:00:00+01:00[Europe/Amsterdam]',0,0,-1)</code>
 * returns <code>2025-10-25T12:00:00+02:00[Europe/Amsterdam]</code>.<br>
 * 
 * @see AddToDateTimeFunction
 */
public final class AddPeriodToDateTimeFunction implements Function
{
	public static final String NAME = "add-period-to-dateTime";

    /**
     * Create a new <code>AddYearMonthToDateTimeFunction</code> object.
     */
    public AddPeriodToDateTimeFunction() {}
 

	/**
	 * Adds a period of years, months and/or days to a date-time.
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
	 * Adds a period of years, months and/or days to a date-time.
	 * 
	 * @param dtm    a date-time
	 * @param years  a number of years
	 * @param months a number of months
	 * @param days   a number of days
	 * @param nav    the navigator used
	 * @return a date-time
	 * @throws FunctionCallException if evaluation failed
	 */
	private static TemporalAccessor evaluate(Object dtm, Object years, Object months, Object days, Navigator nav)
			throws FunctionCallException {

		final TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, nav);

		Double d = NumberFunction.evaluate(years, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() years must be numeric.");
		final int lyears = d.intValue();

		d = NumberFunction.evaluate(months, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() months must be numeric.");
		final int lmonths = d.intValue();

		d = NumberFunction.evaluate(days, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() days must be numeric.");
		final int ldays = d.intValue();

		final Period period = Period.of(lyears, lmonths, ldays);
		try {
			if (tac instanceof LocalDateTime)
				return ((LocalDateTime) tac).plus(period);
			else
				return ((ZonedDateTime) tac).plus(period);
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() failed to add " + period + " to " + tac + ".", e);
		}
	}

}

package be.baur.sdt.xpath.function;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;

/**
 * <code><i>date-time</i> add-yearMonth-to-dateTime( <i>date-time</i>, <i>years</i>, <i>months</i> )</code><br>
 * <p>
 * Returns the result of adding a period of years and/or months to the supplied
 * date-time, where negative values can be used to subtract time. If a time zone
 * ID is provided, daylight savings will be accounted for.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:add-yearMonth-to-dateTime('1968-02-29T00:00:00',1,0)</code> returns
 * <code>1969-02-28T00:00:00</code>.<br>
 * <code>sdt:add-yearMonth-to-dateTime('2025-03-30T02:00:00+01:00[Europe/Amsterdam]',0,1)</code>
 * returns <code>2025-04-30T03:00:00+02:00</code>.<br>
 * <code>sdt:add-yearMonth-to-dateTime('2025-11-26T02:00:00+01:00[Europe/Amsterdam]',0,-1)</code>
 * returns <code>2025-10-26T02:00:00+01:00</code>.<br>
 */
public final class AddYearMonthToDateTimeFunction implements Function
{
	public static final String NAME = "add-yearMonth-to-dateTime";

    /**
     * Create a new <code>AddYearMonthToDateTimeFunction</code> object.
     */
    public AddYearMonthToDateTimeFunction() {}
 

	/**
	 * Adds a period of years and/or months to a date-time.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains three items.
	 * @return a date-time
	 * @throws FunctionCallException if <code>args</code> has more or less than
	 *                               three items or evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 3)
			throw new FunctionCallException(NAME + "() requires three arguments.");

		return DateTimeFunction.format(
			evaluate(args.get(0), args.get(1), args.get(2), context.getNavigator())
		);
	}


	/**
	 * Adds a period of years and/or months to a date-time.
	 * 
	 * @param dtm    a date-time
	 * @param years  a number of years
	 * @param months a number of months
	 * @param nav    the navigator used
	 * @return a date-time
	 * @throws FunctionCallException if no valid date-time was supplied or the
	 *                               addition (subtraction) failed
	 */
    public static TemporalAccessor evaluate(Object dtm, Object years, Object months, Navigator nav) throws FunctionCallException {

		final TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, nav);
		
		Double d = NumberFunction.evaluate(years, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() years must be numeric.");
		long lyears = d.longValue();

		d = NumberFunction.evaluate(months, nav);
		if (d.isNaN())
			throw new FunctionCallException(NAME + "() months must be numeric.");
		long lmonths = d.longValue();

		if (tac instanceof LocalDateTime)
			return ((LocalDateTime) tac).plusYears(lyears).plusMonths(lmonths);
		else
			return ((ZonedDateTime) tac).plusYears(lyears).plusMonths(lmonths);

	}

}

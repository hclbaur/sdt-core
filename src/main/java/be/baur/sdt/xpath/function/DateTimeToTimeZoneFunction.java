package be.baur.sdt.xpath.function;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

/**
 * <code><i>date-time</i> dateTime-to-timezone( <i>date-time</i>, <i>time-zone</i> )</code><br>
 * <p>
 * Creates a date-time from the given date-time and time zone or offset. If a
 * local date-time is supplied, the result will be a zoned date-time in the
 * requested time zone. Otherwise, the supplied date-time will be translated to
 * the given time zone (while the absolute time stays the same). If appropriate,
 * daylight savings will be accounted for.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime-to-timezone('2025-03-30T01:00:00Z', 'Europe/Amsterdam')</code>
 * returns <code>2025-03-30T03:00:00+02:00</code>.<br>
 * <code>sdt:dateTime-to-timezone('2025-10-26T00:00:00Z', 'Europe/Amsterdam')</code>
 * returns <code>2025-10-26T02:00:00+02:00</code>.<br>
 * <code>sdt:dateTime-to-timezone('2025-10-26T01:00:00Z', 'Europe/Amsterdam')</code>
 * returns <code>2025-10-26T02:00:00+01:00</code>.<br>
 * <code>sdt:dateTime-to-timezone('2025-03-30T02:00:00', 'Europe/Amsterdam')</code>
 * returns <code>2025-03-30T03:00:00+02:00</code>.<br>
 * <code>sdt:dateTime-to-timezone('2025-10-26T02:00:00', 'Europe/Amsterdam')</code>
 * returns <code>2025-10-26T02:00:00+02:00</code>.<br>
 * <code>sdt:dateTime-to-timezone('2025-10-26T03:00:00', 'Europe/Amsterdam')</code>
 * returns <code>2025-10-26T03:00:00+01:00</code>.<br>
 * 
 * @see ZoneId
 */
public class DateTimeToTimeZoneFunction implements Function
{
	public static final String NAME = "dateTime-to-timezone";

    /**
     * Create a new <code>DateTimeToTimeZoneFunction</code> object.
     */
    public DateTimeToTimeZoneFunction() {}
 

	/**
	 * Creates a date-time from the given date-time and time zone or offset.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains two items.
	 * @return a zoned date-time string
	 * @throws FunctionCallException if <code>args</code> has more or less than two
	 *                               items or evaluation failed.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() == 2)
			return evaluate(args.get(0), args.get(1), context.getNavigator());

		throw new FunctionCallException(NAME + "() requires two arguments.");
	}


	/**
	 * Creates a date-time from the given date-time and time zone or offset.
	 * 
	 * @param dtm a local or zoned date-time string
	 * @param tmz a time zone or time zone offset string
	 * @param nav the navigator used
	 * @return a zoned date-time string
	 * @throws FunctionCallException if no valid date-time or time zone was supplied
	 *                               or conversion to the target time zone failed.
	 */
	public static String evaluate(Object dtm, Object tmz, Navigator nav) throws FunctionCallException {

		TemporalAccessor temporal = DateTimeFunction.evaluate(NAME, dtm, nav);
		
		String zone = StringFunction.evaluate(tmz, nav);
		ZoneId zoneId;
		try {
			zoneId = ZoneId.of(zone);
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() time zone '" + zone + "' is invalid.", e);
		}

		ZonedDateTime zdtm;
		try {
			
			if (temporal instanceof LocalDateTime)
				zdtm = ((LocalDateTime) temporal).atZone(zoneId);
			else
				zdtm = Instant.from(temporal).atZone(zoneId);
			
			return DateTimeFunction.format(zdtm);
			
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() conversion of '" + dtm + "' failed.", e);
		}
	}
}

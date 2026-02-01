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
 * Create a date-time adjusted to the supplied time zone or offset. If a local
 * date-time is supplied, the result will be a zoned date-time in the requested
 * time zone. Otherwise, the supplied date-time will be translated to the given
 * time zone (while the absolute time stays the same). If appropriate, daylight
 * savings will be accounted for.
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
public final class DateTimeToTimeZoneFunction implements Function
{
	public static final String NAME = "dateTime-to-timezone";

    /**
     * Create a new <code>DateTimeToTimeZoneFunction</code> object.
     */
    public DateTimeToTimeZoneFunction() {}
 

	/**
	 * Create a date-time adjusted to the supplied time zone or offset.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two items
	 * @return a zoned date-time
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 2)
			throw new FunctionCallException(NAME + "() requires two arguments.");

		return DateTimeFunction.format(evaluate(args.get(0), args.get(1), context.getNavigator()));
	}


	/**
	 * Create a date-time adjusted to the supplied time zone or offset.
	 * 
	 * @param dtm a date-time 
	 * @param tmz a time zone or time zone offset
	 * @param nav the navigator used
	 * @return a zoned date-time 
	 * @throws FunctionCallException if evaluation failed
	 */
    private static ZonedDateTime evaluate(Object dtm, Object tmz, Navigator nav) throws FunctionCallException {

		TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, nav);
		
		String zone = StringFunction.evaluate(tmz, nav);
		ZoneId zoneId;
		try {
			zoneId = ZoneId.of(zone);
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() time zone '" + zone + "' is invalid.", e);
		}

		try {
			return evaluate(tac, zoneId);
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() conversion of '" + dtm + "' failed.", e);
		}
	}
	
	
	/**
	 * Create a date-time adjusted to the supplied time zone or offset.
	 * 
	 * @param dtm a date-time
	 * @param zid a zone id
	 * @return a zoned date-time
	 * @throws DateTimeException if conversion to the target zone failed
	 */
    static ZonedDateTime evaluate(TemporalAccessor dtm, ZoneId zid) {

		if (dtm instanceof LocalDateTime)
			return ((LocalDateTime) dtm).atZone(zid);
		else
			return Instant.from(dtm).atZone(zid);
	}
	
	
	/**
	 * Create a date-time adjusted to the supplied time zone or offset, if a local
	 * date-time is supplied. Otherwise return the supplied (zoned) date-time.
	 * 
	 * @param dtm a date-time
	 * @param zid a zone id
	 * @return a zoned date-time
	 */
	static ZonedDateTime ifLocal(TemporalAccessor dtm, ZoneId zid) {

		if (dtm instanceof LocalDateTime)
			return ((LocalDateTime) dtm).atZone(zid);
		else
			return (ZonedDateTime) dtm;
	}

}

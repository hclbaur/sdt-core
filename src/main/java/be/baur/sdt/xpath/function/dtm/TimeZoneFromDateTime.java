package be.baur.sdt.xpath.function.dtm;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

/**
 * <code><i>time-zone?</i> timezone-from-dateTime( <i>date-time</i> )</code><br>
 * <p>
 * Returns the time zone or UTC offset of a date-time, or an empty string if a
 * local date-time is supplied (this can be used to test for a local date-time).
 * <p>
 * Examples:
 * <p>
 * <code>sdt:timezone-from-dateTime('1970-01-01T00:00:00Z')</code> returns
 * <code>Z</code>.<br>
 * <code>not(sdt:timezone-from-dateTime('1970-01-01T00:00:00'))</code> returns
 * <code>true</code>.<br>
 */
public final class TimeZoneFromDateTime implements Function
{
	public static final String NAME = "timezone-from-dateTime";

    /**
     * Create a new <code>TimeZoneFromDateTime</code> object.
     */
    public TimeZoneFromDateTime() {}
 

	/**
	 * Returns the time zone or UTC offset of a date-time.
	 * 
	 * @param context the expression context
	 * @param args    an argument list that contains one item
	 * @return a time-zone or offset string, maybe empty
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 1)
			throw new FunctionCallException(NAME + "() requires one argument.");

		ZoneId zid = evaluate(args.get(0), context.getNavigator());
		return zid == null ? "" : zid.toString();
	}


	/**
	 * Returns the time zone or UTC offset of a date-time, or null if a local
	 * date-time was supplied.
	 * 
	 * @param dtm a date-time
	 * @param nav the navigator used
	 * @return a zone id, may be null
	 * @throws FunctionCallException if evaluation failed
	 */
	private static ZoneId evaluate(Object dtm, Navigator nav) throws FunctionCallException {

		TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, nav);

		if (tac instanceof ZonedDateTime)
			return ((ZonedDateTime) tac).getZone();

		return null;
	}
}

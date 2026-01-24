package be.baur.sdt.xpath.function;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

/**
 * <code><i>time-zone</i> timezone-from-dateTime( <i>date-time</i> )</code><br>
 * <p>
 * Returns the time zone (or UTC offset) of a date-time.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:timezone-from-dateTime('1970-01-01T00:00:00Z')</code> returns
 * <code>Z</code>.<br>
 */
public final class TimeZoneFromDateTime implements Function
{
	public static final String NAME = "timezone-from-dateTime";

    /**
     * Create a new <code>TimeZoneFromDateTime</code> object.
     */
    public TimeZoneFromDateTime() {}
 

	/**
	 * Returns the time zone (or UTC offset) of a date-time.
	 * 
	 * @param context the expression context
	 * @param args    an argument list that contains one item.
	 * @return a zone id, may be null
	 * @throws FunctionCallException if <code>args</code> has more or less than one
	 *                               item or evaluation failed.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 1)
			throw new FunctionCallException(NAME + "() requires exactly one argument.");

		return evaluate(args.get(0), context.getNavigator());
	}


	/**
	 * Returns the time zone (or UTC offset) of a date-time.
	 * 
	 * @param dtm a date-time string
	 * @param nav the navigator used
	 * @return a zone id, may be null
	 * @throws FunctionCallException if no valid date-time was supplied.
	 */
	public static ZoneId evaluate(Object dtm, Navigator nav) throws FunctionCallException {

		TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, nav);

		if (tac instanceof ZonedDateTime)
			return ((ZonedDateTime) tac).getZone();

		return null;
	}
}

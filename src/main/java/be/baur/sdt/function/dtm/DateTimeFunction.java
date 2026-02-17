package be.baur.sdt.function.dtm;

import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

/**
 * <code><i>date-time</i> sdt:dateTime( <i>object</i> )</code><br>
 * <p>
 * A constructor that returns a local or zoned date-time. Real date-time objects
 * are not supported, so all date and time functions operate on strings instead.
 * <p>
 * The argument must be a temporal object or a string in ISO-like format as
 * specified by {@link DateTimeFormatter#ISO_DATE_TIME}, or an exception will be
 * thrown.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime('1968-02-28T12:00')</code> returns
 * <code>1968-02-28T12:00:00</code>.<br>
 * <code>sdt:dateTime('1968-02-28T12:00+01:00')</code> returns
 * <code>1968-02-28T12:00:00+01:00</code>.<br>
 * <code>sdt:dateTime('1968-02-28T12:00:00.500+01:00[Europe/Amsterdam]')</code> returns
 * <code>1968-02-28T12:00:00:00.5+01:00[Europe/Amsterdam]</code>.<br>
 */
public final class DateTimeFunction implements Function
{
	public static final String NAME = "dateTime";
	
    /**
     * Create a new <code>DateTimeFunction</code> object.
     */
    public DateTimeFunction() {}
 

	/**
	 * Returns a local or zoned date-time.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains one item
	 * @return a local or zoned date-time
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 1)
			throw new FunctionCallException(NAME + "() requires one argument.");
		
		return format(
			evaluate(NAME, args.get(0), context.getNavigator())
		);
	}


	/**
	 * Returns a local or zoned date-time object.
	 * 
	 * @param fun name of the calling function
	 * @param obj the object to be converted to a date-time
	 * @param nav the navigator used
	 * @return a local or zoned date-time, not null
	 * @throws FunctionCallException if evaluation failed
	 */
	public static TemporalAccessor evaluate(String fun, Object obj, Navigator nav) throws FunctionCallException {

//		if (obj instanceof ZonedDateTime || obj instanceof LocalDateTime)
//			return (TemporalAccessor) obj;
//
//		if (obj instanceof Instant)
//			return ZonedDateTime.from((Instant) obj);
		
		try {
			return parse(StringFunction.evaluate(obj, nav));
		} catch (Exception e) {
			throw new FunctionCallException(fun + "() argument '" + obj + "' is not a valid date-time.", e);
		}
	}


	/**
	 * Returns a local or zoned date-time object parsed from a string in ISO-like
	 * format, optionally including a time zone id.
	 * 
	 * @param dtms a string representing a date-time
	 * @return a local or zoned date-time, not null
	 * @throws DateTimeParseException if no date-time could be constructed
	 * 
	 * @see DateTimeFormatter#ISO_DATE_TIME
	 */
	public static TemporalAccessor parse(String dtms) {

		return ParseDateTimeFunction.parse(dtms, DateTimeFormatter.ISO_DATE_TIME);
	}


	/**
	 * Returns a string representation of a temporal object in ISO-like format
	 * including the time zone id (if present).
	 * 
	 * @param dtm a local or zoned date-time, not null
	 * @return a formatted date-time string
	 * @throws DateTimeException if formatting failed
	 * 
	 * @see DateTimeFormatter#ISO_DATE_TIME
	 */
	public static String format(TemporalAccessor dtm) {

		return FormatDateTimeFunction.format(dtm, DateTimeFormatter.ISO_DATE_TIME);
	}

}

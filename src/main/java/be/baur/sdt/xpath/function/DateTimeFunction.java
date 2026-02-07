package be.baur.sdt.xpath.function;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;

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
	 * @return a date-time
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
	static TemporalAccessor evaluate(String fun, Object obj, Navigator nav) throws FunctionCallException {

		if (obj instanceof ZonedDateTime || obj instanceof LocalDateTime)
			return (TemporalAccessor) obj;

		if (obj instanceof Instant)
			return ZonedDateTime.from((Instant) obj);
		
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
	 * @return a temporal object, not null
	 * @throws DateTimeParseException if no date-time could be constructed
	 * 
	 * @see DateTimeFormatter#ISO_DATE_TIME
	 */
	public static TemporalAccessor parse(String dtms) {

		return parse(dtms, DateTimeFormatter.ISO_DATE_TIME);
	}
	
	
	/**
	 * Returns a local or zoned date-time object parsed from a string, using a
	 * formatter.
	 * 
	 * @param dtms a string representing a date-time
	 * @param fmt  a formatter, not null
	 * @return a temporal object, not null
	 * @throws DateTimeParseException if no date-time could be constructed
	 */
	public static TemporalAccessor parse(String dtms, DateTimeFormatter fmt) {

		Objects.requireNonNull(fmt, "formatter must not be null");
		return fmt.parseBest(dtms, ZonedDateTime::from, LocalDateTime::from);
	}


	/**
	 * Returns a string representation of a temporal object in ISO-like format
	 * including the time zone id (if present).
	 * 
	 * @param dtm a temporal object, not null
	 * @return a formatted date-time string
	 * @throws DateTimeException if formatting failed
	 * 
	 * @see DateTimeFormatter#ISO_DATE_TIME
	 */
	public static String format(TemporalAccessor dtm) {

		Objects.requireNonNull(dtm, "date-time must not be null");
		return formatTemporal(dtm, DateTimeFormatter.ISO_DATE_TIME);
	}


	/**
	 * Returns a string representation of a temporal object, using a formatter.
	 * Supported objects are LocalDateTime and ZonedDateTime.
	 * 
	 * @param dtm a temporal object, not null
	 * @param fmt a formatter, not null
	 * @return a formatted date-time string
	 * @throws DateTimeException if formatting failed
	 */
	public static String format(TemporalAccessor dtm, DateTimeFormatter fmt) {
		
		Objects.requireNonNull(fmt, "formatter must not be null");
		return formatTemporal(dtm, fmt);
	}

	
	/*
	 * Private helper that renders a temporal object as a string, using a formatter.
	 * Supported objects are Instant, LocalDateTime and ZonedDateTime. Will throw a
	 * DateTimeException if formatting failed.
	 */
	private static String formatTemporal(TemporalAccessor tac, DateTimeFormatter fmt) {
		
		if (tac instanceof Instant)
			return ((Instant) tac).toString();
		if (tac instanceof LocalDateTime)
			return ((LocalDateTime) tac).format(fmt == null ? DateTimeFormatter.ISO_LOCAL_DATE_TIME : fmt);
		if (tac instanceof ZonedDateTime)
			return ((ZonedDateTime) tac).format(fmt == null ? DateTimeFormatter.ISO_OFFSET_DATE_TIME : fmt);
		
		throw new IllegalArgumentException("unsupported class " + tac.getClass().getName());
	}

}

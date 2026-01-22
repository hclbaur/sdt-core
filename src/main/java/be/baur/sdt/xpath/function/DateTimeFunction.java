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
 * <code><i>date-time</i> sdt:dateTime( <i>string</i> )</code><br>
 * <p>
 * A constructor function that returns a date-time as a <i>string</i> in
 * extended ISO-8601 format. Real date-time objects are currently not supported,
 * so all date and time functions operate on strings instead.
 * <p>
 * If the argument is a string compliant with extended ISO-8601 format, this
 * function returns a local or zoned date-time string in ISO_LOCAL_DATE_TIME or
 * ISO_OFFSET_DATE_TIME format, or it will throw an exception if no date-time
 * string can be constructed.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime('1968-02-28T12:00')</code> returns
 * <code>1968-02-28T12:00:00</code>.<br>
 * <code>sdt:dateTime('1968-02-28T12:00+01:00')</code> returns
 * <code>1968-02-28T12:00:00+01:00</code>.<br>
 */
public class DateTimeFunction implements Function
{
	public static final String NAME = "dateTime";
	
    /**
     * Create a new <code>DateTimeFunction</code> object.
     */
    public DateTimeFunction() {}
 

	/**
	 * Returns a local or zoned date-time string in extended ISO-8601 format.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains one item.
	 * @return a date-time string
	 * @throws FunctionCallException if <code>args</code> has more or less than one
	 *                               item or evaluation failed.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 1)
			throw new FunctionCallException(NAME + "() requires exactly one argument.");
		
		return format(evaluate(NAME, args.get(0), context.getNavigator()));
	}


	/**
	 * Returns a local or zoned date-time object.
	 * 
	 * @param fun name of the calling function
	 * @param obj a date-time string
	 * @param nav the navigator used
	 * @return a date-time object
	 * @throws FunctionCallException if date-time construction failed
	 */
	public static TemporalAccessor evaluate(String fun, Object obj, Navigator nav) throws FunctionCallException {

		try {
			return parse( StringFunction.evaluate(obj, nav) );
		}
		catch (Exception e) {
			throw new FunctionCallException(fun + "() argument '" + obj + "' is invalid.", e);
		}
	}


	/**
	 * Returns a local or zoned date-time object parsed from a string.
	 * 
	 * @param dtms a string in extended ISO-8601 format
	 * @return a Local- or ZonedDateTime
	 * @throws DateTimeParseException if no date-time could be constructed
	 */
	public static TemporalAccessor parse(String dtms) {

		return parse(dtms, DateTimeFormatter.ISO_DATE_TIME);
	}
	
	
	/**
	 * Returns a local or zoned date-time object parsed from a string, using a
	 * formatter.
	 * 
	 * @param dtms a representing a date-time in a custom format, not null
	 * @param fmt  a formatter, not null
	 * @return a Local- or ZonedDateTime
	 * @throws DateTimeParseException if no date-time could be constructed
	 */
	public static TemporalAccessor parse(String dtms, DateTimeFormatter fmt) {

		Objects.requireNonNull(fmt, "formatter must not be null");
		return fmt.parseBest(dtms, ZonedDateTime::from, LocalDateTime::from);
	}


	/**
	 * Renders a local or zoned date-time object as a string in extended ISO-8601
	 * format.
	 * 
	 * @param dtm a temporal object, not null
	 * @return a date-time string
	 * @throws DateTimeException if formatting failed
	 */
	public static String format(TemporalAccessor dtm) {		
		return formatTemporal(dtm, null);
	}


	/**
	 * Renders a local or zoned date-time object as a string, using a formatter.
	 * Supported objects are LocalDateTime and ZonedDateTime.
	 * 
	 * @param dtm a temporal object, not null
	 * @param fmt a formatter, not null
	 * @return a date-time string
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
	private static String formatTemporal(TemporalAccessor dtm, DateTimeFormatter fmt) {
		
		if (dtm instanceof Instant)
			return ((Instant) dtm).toString();
		if (dtm instanceof LocalDateTime)
			return ((LocalDateTime) dtm).format(fmt == null ? DateTimeFormatter.ISO_LOCAL_DATE_TIME : fmt);
		if (dtm instanceof ZonedDateTime)
			return ((ZonedDateTime) dtm).format(fmt == null ? DateTimeFormatter.ISO_OFFSET_DATE_TIME : fmt);
		
		throw new IllegalArgumentException("unsupported class " + dtm.getClass().getName());
	}

}

package be.baur.sdt.xpath.function;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Objects;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;
import org.jaxen.function.StringFunction;

/**
 * <code><i>date-time</i> sdt:dateTime( <i>string</i> )</code><br>
 * <code><i>date-time</i> sdt:dateTime( <i>number</i> )</code><br>
 * <p>
 * A constructor function that returns a date-time as a <i>string</i> in
 * extended ISO-8601 format. Real date-time objects are currently not supported
 * by SDT, so all date and time functions operate on strings.
 * <p>
 * If the argument is a string compliant with extended ISO-8601 format, this
 * function returns a local or zoned date-time string in ISO_LOCAL_DATE_TIME or
 * ISO_OFFSET_DATE_TIME format, or it will throw an exception if no date-time
 * string can be constructed.
 * <p>
 * If a numeric argument is supplied, this is taken to represent the number of
 * milliseconds after the epoch (or before in case of a negative number), and
 * the result will be a UTC zoned date-time string.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime(0)</code> returns <code>1970-01-01T00:00:00Z</code>.<br>
 * <code>sdt:dateTime('1968-02-28T12:00')</code> returns <code>1968-02-28T12:00:00</code>.<br>
 * <code>sdt:dateTime('1968-02-28T12:00+01:00')</code> returns <code>1968-02-28T12:00:00+01:00</code>.<br>
 */
public class DateTimeFunction implements Function
{

    /**
     * Create a new <code>DateTimeFunction</code> object.
     */
    public DateTimeFunction() {}
 

	/**
	 * Returns a local or zoned date-time string in extended ISO-8601 format.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains one item.
	 * @return a date-time string
	 * @throws FunctionCallException if <code>args</code> has more than one item.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() == 1)
			return format(evaluate(args.get(0), context.getNavigator()));

		throw new FunctionCallException("dateTime() requires exactly one argument.");
	}


	/**
	 * Returns a local or zoned date-time string in extended ISO-8601 format.
	 * 
	 * @param obj a string or a number
	 * @param nav the navigator used
	 * @return a date-time string
	 * @throws FunctionCallException if no date-time could be constructed
	 */
	public static TemporalAccessor evaluate(Object obj, Navigator nav) throws FunctionCallException {

		double msecs = NumberFunction.evaluate(obj, nav);

		if (Double.isNaN(msecs))
			return parse(StringFunction.evaluate(obj, nav));

		return ofEpochMilli((long) msecs);
	}


	/**
	 * Returns a local or zoned date-time object parsed from a string value.
	 * 
	 * @param dtms a date-time string in extended ISO-8601 format
	 * @return a LocalDate or ZonedDateTime
	 * @throws FunctionCallException if parsing failed
	 */
	public static TemporalAccessor parse(String dtms) throws FunctionCallException {

		try {
			DateTimeFormatter f = DateTimeFormatter.ISO_DATE_TIME;
			return f.parseBest(dtms, ZonedDateTime::from, LocalDateTime::from);
		} catch (Exception e) {
			throw new FunctionCallException("dateTime() evaluation of '" + dtms + "' failed.", e);
		}
	}


	/**
	 * Renders a local or zoned date-time object as a string in extended ISO-8601
	 * format.
	 * 
	 * @param dtm a temporal object, not null
	 * @return a date-time string
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
	

	/**
	 * Returns a UTC date-time object using milliseconds before or after the epoch.
	 * 
	 * @param msecs a number of milliseconds, may be negative
	 * @return an instant, not null
	 */
	public static TemporalAccessor ofEpochMilli(long msecs) {
		return Instant.ofEpochMilli(msecs);
	}


//	public static void main(String[] args) throws FunctionCallException {
//		System.out.println(format(parse("1968-02-28T12:01")));
//		System.out.println(format(parse("1968-02-28T12:01:02")));
//
//		System.out.println(format(parse("1968-02-28T12:01Z")));
//		System.out.println(format(parse("1968-02-28T12:01:02Z")));
//
//		System.out.println(format(parse("1968-02-28T12:01+03:00")));
//		System.out.println(format(parse("1968-02-28T12:01:02+03:00")));
//
//		System.out.println(format(parse("1968-02-28T12:01-04:00")));
//		System.out.println(format(parse("1968-02-28T12:01:02-04:00")));
//	}
}

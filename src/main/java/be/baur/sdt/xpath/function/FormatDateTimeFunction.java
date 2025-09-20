package be.baur.sdt.xpath.function;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

/**
 * <code><i>string</i> sdt:format-dateTime( <i>date-time</i>, <i>string</i> )</code><br>
 * <p>
 * Returns a formatted date-time string, using a pattern.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:format-dateTime('1968-02-28T12:00','yyyy/MM/dd HH:mm')</code>
 * returns <code>1968/02/28 12:00</code>.<br>
 * 
 * @see DateTimeFormatter
 */
public class FormatDateTimeFunction implements Function
{

    /**
     * Create a new <code>FormatDateTimeFunction</code> object.
     */
    public FormatDateTimeFunction() {}
 

	/**
	 * Returns a formatted date-time string, using a pattern.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains two items.
	 * @return a formatted date-time string
	 * @throws FunctionCallException if <code>args</code> has more or less than two
	 *                               items, or if formatting failed.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {
		
		if (args.size() == 2) 
			return evaluate(args.get(0), args.get(1), context.getNavigator());
		
		throw new FunctionCallException("format-dateTime() requires two arguments.");
	}


	/**
	 * Returns a formatted date-time string, using a pattern.
	 * 
	 * @param datetime the date-time to be formatted, not null
	 * @param pattern  the pattern to be used, not null
	 * @param nav      the navigator used
	 * @return a formatted date-time string
	 * @throws FunctionCallException if formatting failed
	 */
	public static String evaluate(Object datetime, Object pattern, Navigator nav) throws FunctionCallException {

		TemporalAccessor dtm = DateTimeFunction.evaluate(datetime, nav);
		String fmt = StringFunction.evaluate(pattern, nav);
		
		try {
			return format(dtm, DateTimeFormatter.ofPattern(fmt));
		}
		catch (Exception e) {
			throw new FunctionCallException("format-dateTime() formatting failed.", e);
		}
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
		
		if (dtm instanceof LocalDateTime)
			return ((LocalDateTime) dtm).format(fmt);
		if (dtm instanceof ZonedDateTime)
			return ((ZonedDateTime) dtm).format(fmt);
		
		throw new IllegalArgumentException("unsupported class " + dtm.getClass().getName());
	}


//	public static void main(String[] args) throws FunctionCallException {
//		System.out.println(format(LocalDateTime.now(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//		System.out.println(format(ZonedDateTime.now(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//		System.out.println(format(DateTimeFunction.parse("1968-02-28T12:00"), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
//	}
}

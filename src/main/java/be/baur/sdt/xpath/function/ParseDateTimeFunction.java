package be.baur.sdt.xpath.function;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

/**
 * <code><i>date-time</i> sdt:parse-dateTime( <i>string</i>, <i>string</i> )</code><br>
 * <p>
 * Parses the supplied string into a date-time, using the second argument as a
 * formatting pattern. The pattern must be valid and appropriate for the input
 * string.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:parse-dateTime('1968/02/28 12:00','yyyy/MM/dd HH:mm')</code>
 * returns <code>1968-02-28T12:00:00</code>.
 * 
 * @see DateTimeFormatter
 */
public class ParseDateTimeFunction implements Function
{

    /**
     * Create a new <code>ParseDateTimeFunction</code> object.
     */
    public ParseDateTimeFunction() {}
 

	/**
	 * Parses a string into a date-time, using a pattern.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains two items.
	 * @return a date-time string
	 * @throws FunctionCallException if <code>args</code> has more or less than two
	 *                               items, or if parsing failed.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {
		
		if (args.size() == 2) 
			return evaluate(args.get(0), args.get(1), context.getNavigator());
		
		throw new FunctionCallException("parse-dateTime() requires two arguments.");
	}


	/**
	 * Parses a string into a date-time, using a pattern.
	 * 
	 * @param string  the string to be parsed, not null
	 * @param pattern the pattern to be used, not null
	 * @param nav     the navigator used
	 * @return a date-time string
	 * @throws FunctionCallException if formatting failed
	 */
	public static String evaluate(Object string, Object pattern, Navigator nav) throws FunctionCallException {

		String dtms = StringFunction.evaluate(string, nav);
		String fmts = StringFunction.evaluate(pattern, nav);
		
		try {
			DateTimeFormatter dtf;
			try {
				dtf = DateTimeFormatter.ofPattern(fmts);
			}
			catch (Exception e) {
				throw new FunctionCallException("parse-dateTime() pattern is invalid.", e);
			}
			return DateTimeFunction.format(DateTimeFunction.parse(dtms, dtf));
		}
		catch (Exception e) {
			throw new FunctionCallException("parse-dateTime() failed to parse '" + dtms + "'.", e);
		}
	}

}

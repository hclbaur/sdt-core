package be.baur.sdt.xpath.function;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

/**
 * <code><i>string</i> sdt:format-dateTime( <i>date-time</i>, <i>pattern</i> )</code><br>
 * <p>
 * Returns a formatted date-time string, using a formatting pattern. The pattern
 * must be valid and appropriate for the supplied date-time.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:format-dateTime('1968-02-28T12:00','yyyy/MM/dd HH:mm')</code>
 * returns <code>1968/02/28 12:00</code>.<br>
 * <code>sdt:format-dateTime(sdt:millis-to-dateTime(0),'yyyyMMddHHmmss')</code>
 * returns <code>19700101000000</code>.
 * 
 * @see DateTimeFormatter
 */
public final class FormatDateTimeFunction implements Function
{
	public static final String NAME = "format-dateTime";
	
    /**
     * Create a new <code>FormatDateTimeFunction</code> object.
     */
    public FormatDateTimeFunction() {}
 

	/**
	 * Returns a formatted date-time string, using a pattern.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two items
	 * @return a formatted date-time
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {
		
		if (args.size() == 2) 
			return evaluate(args.get(0), args.get(1), context.getNavigator());
		
		throw new FunctionCallException(NAME + "() requires two arguments.");
	}


	/**
	 * Returns a formatted date-time string, using a pattern.
	 * 
	 * @param dtm a date-time string
	 * @param pat the pattern to be used, not null
	 * @param nav the navigator used
	 * @return a formatted date-time string
	 * @throws FunctionCallException if evaluation failed
	 */
    private static String evaluate(Object dtm, Object pat, Navigator nav) throws FunctionCallException {

		TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, nav);

		String fmt = StringFunction.evaluate(pat, nav);
		try {
		
			DateTimeFormatter dtf;
			try {
				dtf = DateTimeFormatter.ofPattern(fmt);
			} catch (Exception e) {
				throw new FunctionCallException(NAME + "() pattern '" + fmt + "' is invalid.", e);
			}
			return DateTimeFunction.format(tac, dtf);
		
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() failed to format "
					+ ((tac instanceof LocalDateTime) ? "local" : "zoned") + " date-time.", e);
		}
	}
}

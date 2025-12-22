package be.baur.sdt.xpath.function;

import java.time.Instant;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;

/**
 * <code><i>date-time</i> sdt:millis-to-dateTime( <i>number</i> )</code><br>
 * <p>
 * Accepts the number of milliseconds after the epoch (or before in case of a
 * negative number), and returns a UTC zoned date-time string.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:millis-to-dateTime(0)</code> returns
 * <code>1970-01-01T00:00:00Z</code>.<br>
 * <code>sdt:millis-to-dateTime(sdt:timestamp())</code> returns the current UTC
 * date and time.<br>
 */
public class MillisToDateTimeFunction implements Function
{
	public static final String NAME = "millis-to-dateTime";
	
    /**
     * Create a new <code>MillisToDateTimeFunction</code> object.
     */
    public MillisToDateTimeFunction() {}
 

	/**
	 * Converts the number of milliseconds since the epoch into a UTC zoned
	 * date-time string.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains one item.
	 * @return a date-time string
	 * @throws FunctionCallException if <code>args</code> has more than one item or
	 *                               no date-time could be constructed.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() == 1)
			return evaluate(args.get(0), context.getNavigator());

		throw new FunctionCallException(NAME + "() requires exactly one argument.");
	}


	/**
	 * Converts the number of milliseconds since the epoch into a UTC zoned
	 * date-time string.
	 * 
	 * @param obj a number
	 * @param nav the navigator used
	 * @return a date-time string
	 * @throws FunctionCallException if no date-time could be constructed
	 */
	public static String evaluate(Object obj, Navigator nav) throws FunctionCallException {

		double msecs = NumberFunction.evaluate(obj, nav);

		if (Double.isNaN(msecs))
			throw new FunctionCallException(NAME + "() requires a number.");

		try {
			return DateTimeFunction.format(Instant.ofEpochMilli((long) msecs));
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() evaluation of '" + msecs + "' failed.", e);
		}
	}

}

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
 */
public final class MillisToDateTimeFunction implements Function
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
	 * @param context the expression context
	 * @param args    an argument list that contains one item
	 * @return a zoned date-time
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 1)
			throw new FunctionCallException(NAME + "() requires one argument.");
		
		return DateTimeFunction.format(evaluate(args.get(0), context.getNavigator()));
	}


	/**
	 * Converts the number of milliseconds since the epoch into a UTC zoned
	 * date-time string.
	 * 
	 * @param obj a number
	 * @param nav the navigator used
	 * @return a zoned date-time
	 * @throws FunctionCallException if evaluation failed
	 */
    private static Instant evaluate(Object obj, Navigator nav) throws FunctionCallException {

		double msecs = NumberFunction.evaluate(obj, nav);

		if (Double.isNaN(msecs))
			throw new FunctionCallException(NAME + "() requires a number.");

		try {
			return Instant.ofEpochMilli((long) msecs);
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() evaluation of '" + msecs + "' failed.", e);
		}
	}

}

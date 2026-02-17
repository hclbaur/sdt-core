package be.baur.sdt.function.dtm;

import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>double</i> sdt:compare-dateTime( <i>date-time</i>, <i>date-time</i> )</code><br>
 * <p>
 * Compares two date-time values. This function returns -1, 0 or 1, depending on
 * whether the first argument precedes, equals or 1 exceeds the second in time:
 * <p>
 * <code>sdt:compare-dateTime('1970-01-01T00:00:00+01:00', '1970-01-01T00:00:00Z')</code>
 * returns <code>-1.0</code>.<br>
 * <code>sdt:compare-dateTime(sdt:current-dateTime(),sdt:current-dateTime())</code>
 * returns <code>0.0</code>.<br>
 * <code>sdt:compare-dateTime('1970-01-01T00:00:00Z', '1970-01-01T00:00:00+01:00')</code>
 * returns <code>1.0</code>.<br>
 * <p>
 * <i>Note:</i> if either argument is a local date-time, the implicit time zone
 * will be used to compare it against the other.
 * <p>
 * This function can be used as a comparator in a sort statement.
 */
public final class CompareDateTimeFunction implements Function
{
	public static final String NAME = "compare-dateTime";
	
    /**
     * Create a new <code>CompareDateTimeFunction</code> object.
     */
    public CompareDateTimeFunction() {}

    
	/**
	 * Compares two date-time values, returning -1, 0 or 1.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two items
	 * @return a signum value
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 2)
			throw new FunctionCallException(NAME + "() requires two arguments.");

		return evaluate(args.get(0), args.get(1), context);
	}
    

	/**
	 * Compares two date-time values, returning -1, 0 or 1.
	 *
	 * @param dtm1    the first date-time
	 * @param dtm2    the second date-time
	 * @param context the expression context
	 * @return a signum value
	 * @throws FunctionCallException if evaluation failed
	 */
	private static Double evaluate(Object dtm1, Object dtm2, Context context) throws FunctionCallException {

		return Math.signum(SubtractDateTimesFunction.evaluate(NAME, dtm1, dtm2, context));
	}

}

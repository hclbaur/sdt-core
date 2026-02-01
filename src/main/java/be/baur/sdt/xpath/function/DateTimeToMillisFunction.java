package be.baur.sdt.xpath.function;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>number</i> sdt:dateTime-to-millis( <i>date-time</i> )</code><br>
 * <p>
 * Converts a date-time into a number of milliseconds elapsed since the epoch. A
 * negative number is returned for a point in time prior to the epoch.
 * <p>
 * <i>If a local date-time is supplied, the implicit time zone will be used to
 * calculate the offset from UTC.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime-to-millis('1970-01-01T00:00:00Z')</code> returns
 * <code>0</code>.<br>
 * 
 * @see ImplicitTimeZoneFunction
 */
public final class DateTimeToMillisFunction implements Function {
	public static final String NAME = "dateTime-to-millis";


	/**
	 * Create a new <code>DateTimeToMillisFunction</code> object.
	 */
	public DateTimeToMillisFunction() {}


	/**
	 * Converts a date-time into the number of milliseconds elapsed since the epoch.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains one item
	 * @return a number of milliseconds
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() == 1)
			return evaluate(args.get(0), context);

		throw new FunctionCallException(NAME + "() requires one argument.");
	}


	/**
	 * Converts a date-time into the number of milliseconds elapsed since the epoch.
	 * 
	 * @param dtm     a date-time
	 * @param context the expression context
	 * @return a number of milliseconds
	 * @throws FunctionCallException if evaluation failed
	 */
	private static Double evaluate(Object dtm, Context context) throws FunctionCallException {

		TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, context.getNavigator());

		try {

			if (tac instanceof LocalDateTime)
				tac = DateTimeToTimeZoneFunction.evaluate(tac, ImplicitTimeZoneFunction.evaluate(NAME, context));

			return (double) Instant.from(tac).toEpochMilli();

		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() conversion of '" + dtm + "' failed.", e);
		}
	}
}

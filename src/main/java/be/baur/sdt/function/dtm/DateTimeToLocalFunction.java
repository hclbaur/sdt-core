package be.baur.sdt.function.dtm;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

/**
 * <code><i>date-time</i> dateTime-to-local( <i>date-time</i> )</code><br>
 * <p>
 * Removes the time zone component from a date-time and returns a local
 * date-time with the same year, month, day and time as the one supplied.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime-to-local('1970-01-01T00:00:00Z')</code> returns
 * <code>1970-01-01T00:00:00</code>.<br>
 */
public final class DateTimeToLocalFunction implements Function
{
	public static final String NAME = "dateTime-to-local";

    /**
     * Create a new <code>DateTimeToLocalFunction</code> object.
     */
    public DateTimeToLocalFunction() {}
 

	/**
	 * Removes the time zone component from a date-time.
	 * 
	 * @param context the expression context
	 * @param args    an argument list that contains one item
	 * @return a local date-time
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 1)
			throw new FunctionCallException(NAME + "() requires one argument.");

		return DateTimeFunction.format(
			evaluate(args.get(0), context.getNavigator())
		);
	}


	/**
	 * Removes the time zone component from a date-time.
	 * 
	 * @param dtm a date-time
	 * @param nav the navigator used
	 * @return a local date-time, not null
	 * @throws FunctionCallException if evaluation failed
	 */
    private static LocalDateTime evaluate(Object dtm, Navigator nav) throws FunctionCallException {

		TemporalAccessor tac = DateTimeFunction.evaluate(NAME, dtm, nav);

		if (tac instanceof ZonedDateTime)
			tac = ((ZonedDateTime) tac).toLocalDateTime();

		return (LocalDateTime) tac;
	}
}

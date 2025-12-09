package be.baur.sdt.xpath.function;

import java.time.ZonedDateTime;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>string</i> fn:current-dateTime()</code><br>
 * <p>
 * Returns the current date and time in extended ISO-8601 format from the system
 * clock in the default time zone.
 * <p>
 * Note: this implementation is non-deterministic.
 * 
 * @see <a href=
 *      "https://www.w3.org/TR/xpath-functions/#func-current-dateTime">Section
 *      15.3 of the XPath Specification</a>
 */
public class CurrentDateTimeFunction implements Function
{

    /**
     * Create a new <code>CurrentDateTimeFunction</code> object.
     */
    public CurrentDateTimeFunction() {}
    
	/**
	 * Returns the current date and time in extended ISO-8601 format.
	 *
	 * @param context will be ignored
	 * @param args    an empty list
	 * @return a zoned date-time string
	 * @throws FunctionCallException if <code>args</code> is not empty
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
	{
		if (args.size() == 0)
			return evaluate();

		throw new FunctionCallException("current-dateTime() requires no arguments.");
	}

  
	/**
	 * Returns the current date and time in extended ISO-8601 format.
	 * 
	 * @return a zoned date-time string
	 */
	public static String evaluate() {
		return DateTimeFunction.format(ZonedDateTime.now());
	}

}

package be.baur.sdt.xpath.function;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>string</i> fn:current-dateTime()</code><br>
 * <p>
 * Returns the current date and time (with time-zone) in ISO-8601 format.
 * 
 * @see <a href=
 *      "https://www.w3.org/TR/xpath-functions/#func-current-dateTime">Section 15.3
 *      of the XPath Specification</a>
 */
public class CurrentDateTimeFunction implements Function
{

    /**
     * Create a new <code>CurrentDateTimeFunction</code> object.
     */
    public CurrentDateTimeFunction() {}
    
	/**
	 * Returns the current date and time (with time-zone).
	 *
	 * @param context will be ignored
	 * @param args    an empty list
	 * 
	 * @return a <code>ZonedDateTime</code>
	 * 
	 * @throws FunctionCallException if <code>args</code> is not empty
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
    {
        if (args.size() == 0)
        {
            return evaluate();
        }

        throw new FunctionCallException( "current-dateTime() requires no arguments." );
    }

  
	/**
	 * Returns the current date and time from the system clock in the default time-zone.
	 * 
	 * @return the current date and time, not null
	 */
	public static String evaluate() {
		return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		//return Instant.now().toString();
		//return Instant.ofEpochMilli(3600000).toString();
		//return ZoneId.of("UTC+02:00").toString();
	}
    
}

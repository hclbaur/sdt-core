package be.baur.sdt.xpath.function;

import java.time.ZoneId;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>time-zone</i> sdt:system-timezone()</code><br>
 * <p>
 * Returns the system default time-zone ID.
 * 
 * @see ZoneId
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/List_of_tz_database_time_zones">List of
 *      tz database time zones</a>
 */
public class SystemTimeZoneFunction implements Function
{
	public static final String NAME = "system-timezone";
	
    /**
     * Create a new <code>SystemTimeZoneFunction</code> object.
     */
    public SystemTimeZoneFunction() {}
    
	/**
	 * Returns the system default time-zone ID.
	 *
	 * @param context will be ignored
	 * @param args    an empty list
	 * @return a time-zone id
	 * @throws FunctionCallException if <code>args</code> is not empty or an
	 *                               exception occurred during evaluation.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (!args.isEmpty())
			throw new FunctionCallException(NAME + "() requires no arguments.");

		return evaluate();
	}

  
	/**
	 * Returns the system default time-zone ID.
	 * 
	 * @return the time-zone id, not null
	 * @throws FunctionCallException if a time zone could not be determined.
	 */
	public static String evaluate() throws FunctionCallException {

		try {
			return ZoneId.systemDefault().toString();
		} catch (Exception e) {
			throw new FunctionCallException(NAME + "() time zone not found or invalid.", e);
		}
	}

}

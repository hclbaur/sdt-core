package be.baur.sdt.xpath.function;

import java.time.ZoneId;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>time-zone</i> sdt:system-timezone()</code><br>
 * <p>
 * Returns the system clock default time zone ID or UTC if no zone id could be
 * determined.
 * 
 * @see ImplicitTimeZoneFunction
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/List_of_tz_database_time_zones">List of
 *      tz database time zones</a>
 */
public final class SystemTimeZoneFunction implements Function
{
	public static final String NAME = "system-timezone";
	
    /**
     * Create a new <code>SystemTimeZoneFunction</code> object.
     */
    public SystemTimeZoneFunction() {}
    
	/**
	 * Returns the system default time zone ID.
	 *
	 * @param context the expression context
	 * @param args    an empty list
	 * @return a time zone
	 * @throws FunctionCallException if <code>args</code> is not empty or an
	 *                               exception occurred during evaluation.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (!args.isEmpty())
			throw new FunctionCallException(NAME + "() requires no arguments.");

		return evaluate().toString();
	}

  
	/**
	 * Returns the system default time zone ID.
	 * 
	 * @return a zone id, not null
	 */
	public static ZoneId evaluate() {

		try {
			return ZoneId.systemDefault();
		} catch (Exception e) {
			return ZoneId.of("UTC"); // ultimate fallback
		}
	}

}

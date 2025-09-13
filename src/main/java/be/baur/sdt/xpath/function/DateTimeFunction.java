package be.baur.sdt.xpath.function;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;
import org.jaxen.function.StringFunction;

/**
 * <code><i>date-time</i> sdt:dateTime()</code><br>
 * <code><i>date-time</i> sdt:dateTime( <i>string</i> | <i>number</i> )</code><br>
 * <p>
 * A constructor function that returns a date-time as a <i>string</i> in
 * ISO-8601 format. Real date-time objects are currently not supported by SDT,
 * so all date and time functions operate on strings. This class supplies static
 * methods for internal use to facilitate this. Use them.
 * <p>
 * Without arguments, <code>sdt:dateTime()</code> returns the current system
 * date and time in UTC. A numeric argument represents the number of
 * milliseconds after the epoch (or before it for a negative number).
 * <p>
 * If the argument is a string in a format compliant with ISO-8601 this function
 * returns a date and time in the canonical format, or throw an exception
 * otherwise.
 * <p>
 * Examples:
 * <p>
 * <code>sdt:dateTime(0)</code> returns <code>1970-01-01T00:00:00Z</code>.<br>
 */
public class DateTimeFunction implements Function
{

    /**
     * Create a new <code>DateTimeFunction</code> object.
     */
    public DateTimeFunction() {}
    
	/**
	 * Returns a date and time string in ISO-8601 format.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains at most one item.
	 * 
	 * @return a <code>String</code>
	 * 
	 * @throws FunctionCallException if <code>args</code> has more than one item.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
	{
		if (args.size() == 0)
			return evaluate();
		else if (args.size() == 1)
			return evaluate(args.get(0), context.getNavigator());

		throw new FunctionCallException("dateTime() requires at most one argument.");
	}

  
	/**
	 * Returns the current system date and time.
	 * 
	 * @return a UTC date-time string, not null
	 */
	public static String evaluate() {
		return now().toString();
	}


	/**
	 * Converts the supplied object to a date and time.
	 * 
	 * @return a UTC date-time string, not null
	 * @throws FunctionCallException 
	 */
	public static String evaluate(Object obj, Navigator nav) throws FunctionCallException {
		
		double msecs = NumberFunction.evaluate(obj, nav);
		
		if (! Double.isNaN(msecs))
			return ofEpochMilli((long) msecs).toString();
		
		String dtm = StringFunction.evaluate(obj, nav);
		
		try {
			return Instant.parse(dtm).toString();
		}
		catch (Exception e) {
			throw new FunctionCallException("dateTime() evaluation of '" + dtm + "' failed.", e);
		}
	}

	
	// Static helper methods, use these in other functions!
	
	/**
	 * Returns the current system date and time in UTC.
	 * 
	 * @return an instant, not null
	 */
	public static Instant now() {
		return Instant.now();
	}
	
	/**
	 * Returns a UTC date and time using milliseconds before or after the epoch.
	 *  
	 * @param msecs a number of milliseconds, may be negative
	 * @return an instant, not null
	 */
	public static Instant ofEpochMilli(long msecs) {
		return Instant.ofEpochMilli(msecs);
	}
	
	/**
	 * Returns a UTC date and time parsed from a string value.
	 * 
	 * @return an instant, not null
	 */
	public static Instant parse(String dtm) {
		/*
		 * ISO_LOCAL_DATE_TIME  196802281200            12
		 * ISO_LOCAL_DATE_TIME  19680228120000          14
		 * ISO_LOCAL_DATE_TIME  1968-02-28T12:00        16
		 * ISO_LOCAL_DATE_TIME  1968-02-28T12:00:00     19
		 * ISO_OFFSET_DATE_TIME 1968-02-28T12:00:00Z    20
		 * ISO_ZONED_DATE_TIME  1968-02-28T12:00:00Z[]  22
		 */

		ZonedDateTime zdt = null;
		LocalDateTime ldt = null;
		
		System.out.println(dtm.indexOf("-", 10));
		
		if (dtm.contains("+") || dtm.contains("Z") || dtm.indexOf("-", 16) > -1)
			zdt = ZonedDateTime.parse(dtm, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		else {
			ldt = LocalDateTime.parse(dtm, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			zdt = ldt.atZone(ZoneId.of("Z"));
		}

		if (zdt != null)
			System.out.println(zdt.toString());
		if (ldt != null)
			System.out.println(ldt.toString());

		return null;//Instant.parse(dtm);
	}
	
	public static void main(String[] args) {
		parse("1968-02-28T12:01");
		parse("1968-02-28T12:01:02");
		
		parse("1968-02-28T12:01Z");
		parse("1968-02-28T12:01:02Z");
		
		parse("1968-02-28T12:01+03:00");
		parse("1968-02-28T12:01:02+03:00");
		
		parse("1968-02-28T12:01-04:00");
		parse("1968-02-28T12:01:02-04:00");
		
		//parse("196802281201-0100");
		//parse("19680228120102");
	}
}

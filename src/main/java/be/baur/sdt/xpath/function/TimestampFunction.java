package be.baur.sdt.xpath.function;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>number</i> sdt:timestamp()</code><br>
 * <p>
 * Returns the current time in milliseconds elapsed since the epoch.
 */
public class TimestampFunction implements Function
{

    /**
     * Create a new <code>TimestampFunction</code> object.
     */
    public TimestampFunction() {}
    
	/**
	 * Returns the current time in milliseconds elapsed since the epoch.
	 *
	 * @param context will be ignored
	 * @param args    an empty list
	 * @return a number of milliseconds
	 * @throws FunctionCallException if <code>args</code> is not empty
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
	{
		if (args.size() == 0)
			return evaluate();

		throw new FunctionCallException("timestamp() requires no arguments.");
	}

  
	/**
	 * Returns the current time in milliseconds elapsed since the epoch.
	 * 
	 * @return a number of milliseconds
	 */
	public static Double evaluate() {
		
		Duration elapsed = Duration.between(Instant.EPOCH, Instant.now());
		return (double) elapsed.toMillis();
	}

}

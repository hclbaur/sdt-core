package be.baur.sdt.xpath.function;

import java.util.Arrays;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

import be.baur.sdt.SDT;

/**
 * <code><i>string</i> sdt:left( <i>string</i>, <i>number</i> )</code>
 * <p>
 * Returns the specified number of characters from the start of the argument
 * string. For example,
 * <p>
 * <code>sdt:left('12345', 3)</code> returns <code>"123"</code>.
 * <p>
 * If the second argument is not a number or less than 1, an empty string is
 * returned. If it exceeds the string length of the first argument, the entire
 * string is returned.
 */
public final class LeftFunction implements Function
{
	public static final String NAME = "left";
	
    /**
     * Create a new <code>LeftFunction</code> object.
     */
    public LeftFunction() {}

    
	/**
	 * Returns the left part of a string.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two items
	 * @return a string
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
    {
        if (args.size() != 2)
            throw new FunctionCallException(NAME + "() requires two arguments." );

        final Object[] subargs = { args.get(0), 1.0, args.get(1) };
        return SDT.SUBSTRING.call(context, Arrays.asList( subargs ));       
    }

}

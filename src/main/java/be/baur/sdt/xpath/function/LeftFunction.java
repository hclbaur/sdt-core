package be.baur.sdt.xpath.function;

import java.util.Arrays;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.function.SubstringFunction;

/**
 * <code><i>string</i> sdt:left( <i>string</i>, <i>number</i> )</code>
 * <p>
 * Returns the specified number of characters from the start of the argument
 * string. For example,
 * <p>
 * <code>sdt:left("12345",3)</code> returns <code>"123"</code>.
 * <p>
 * If the second argument is not a number or less than 1, an empty string is
 * returned. If it exceeds the string length of the first argument, the entire
 * string is returned.
 */
public class LeftFunction implements Function
{

    /**
     * Create a new <code>LeftFunction</code> object.
     */
    public LeftFunction() {}

    
	/**
	 * Returns the left part of an XPath string-value by length.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called.
	 * @param args    an argument list that contains two items, a
	 *                <code>String</code> and a length.
	 * 
	 * @return a <code>String</code>
	 * 
	 * @throws FunctionCallException if <code>args</code> has more or less than two
	 *                               items.
	 */
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
    {
        if (args.size() != 2)
            throw new FunctionCallException( "left() requires two arguments." );

        final Object[] subargs = { args.get(0), 1.0, args.get(1) };
        return (new SubstringFunction()).call(context, Arrays.asList( subargs ));       
    }

}

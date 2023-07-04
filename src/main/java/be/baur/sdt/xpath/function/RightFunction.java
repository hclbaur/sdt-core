package be.baur.sdt.xpath.function;

import java.util.Arrays;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;
import org.jaxen.function.StringLengthFunction;
import org.jaxen.function.SubstringFunction;

/**
 * <code><i>string</i> sdt:right( <i>string</i>, <i>number</i> )</code>
 * <p>
 * Returns the specified number of characters from the end of the argument
 * string. For example,
 * <p>
 * <code>sdt:right("12345",3)</code> returns <code>"345"</code>.
 * <p>
 * If the second argument is not a number or less than 1, an empty string is
 * returned. If it exceeds the string length of the first argument, the entire
 * string is returned.
 */
public class RightFunction implements Function
{

    /**
     * Create a new <code>RightFunction</code> object.
     */
    public RightFunction() {}

    
	/**
	 * Returns the right part of an XPath string-value by length.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains two items,a <code>String</code>
	 *                and a length.
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
            throw new FunctionCallException( "right() requires two arguments." );

        final Navigator nav = context.getNavigator();

        final int len = (StringLengthFunction.evaluate(args.get(0), nav )).intValue();
        if (len == 0) return "";
        
        Double arg1 = NumberFunction.evaluate(args.get(1), nav);
        if (arg1.isNaN()) return "";

        // number of characters to return
        int num = (int) Math.round(arg1); 
        if (num <= 0) return "";

        final Object[] subargs = { args.get(0), new Double(len - num + 1),  arg1 };
        return (new SubstringFunction()).call(context, Arrays.asList( subargs ));    
    }

}

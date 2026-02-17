package be.baur.sdt.function;

import java.util.Arrays;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.NumberFunction;
import org.jaxen.function.StringLengthFunction;

import be.baur.sdt.SDT;

/**
 * <code><i>string</i> sdt:right( <i>string</i>, <i>number</i> )</code>
 * <p>
 * Returns the specified number of characters from the end of the argument
 * string. For example,
 * <p>
 * <code>sdt:right('12345', 3)</code> returns <code>"345"</code>.
 * <p>
 * If the second argument is not a number or less than 1, an empty string is
 * returned. If it exceeds the string length of the first argument, the entire
 * string is returned.
 */
public final class RightFunction implements Function
{
	public static final String NAME = "right";
	
    /**
     * Create a new <code>RightFunction</code> object.
     */
    public RightFunction() {}

    
	/**
	 * Returns the right part of a string.
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

        final Navigator nav = context.getNavigator();

        final int len = (StringLengthFunction.evaluate(args.get(0), nav )).intValue();
        if (len == 0) return "";
        
        Double arg2 = NumberFunction.evaluate(args.get(1), nav);
        if (arg2.isNaN()) return "";

        // number of characters to return
        int num = (int) Math.round(arg2); 
        if (num <= 0) return "";

        final Object[] subargs = { args.get(0), new Double(len - num + 1),  arg2 };
        return SDT.SUBSTRING.call(context, Arrays.asList( subargs ));    
    }

}

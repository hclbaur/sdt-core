package be.baur.sdt.xpath.function;

import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.BooleanFunction;
import org.jaxen.function.NumberFunction;

/**
 * <code><i>double</i> sdt:compare-number( <i>object</i>, <i>object</i>, <i>boolean nanFirst?</i> )</code>
 * <p>
 * Compares two objects numerically. This function converts its arguments to
 * numbers and returns -1 if the second argument precedes the first, 1 if it
 * exceeds it, and 0 if the arguments are numerically equal:
 * <p>
 * <code>sdt:compare-number(1,2)</code> returns <code>-1</code>.<br>
 * <code>sdt:compare-number(3,'3')</code> returns <code>0</code>.<br>
 * <code>sdt:compare-number('5','4')</code> returns <code>1</code>.
 * <p>
 * Objects that are not numbers are considered equal, and greater than all other
 * numbers:
 * <p>
 * <code>sdt:compare-number('a',1)</code> returns <code>1</code>.<br>
 * <code>sdt:compare-number('a','b')</code> returns <code>0</code>.
 * <p>
 * If the optional third argument evaluates to true, objects that are not
 * numbers are considered smaller than all numbers.
 */
public class CompareNumberFunction implements Function
{

    /**
     * Create a new <code>CompareNumberFunction</code> object.
     */
    public CompareNumberFunction() {}

    
	/**
	 * Numerically compares two values, returning -1, 0 or 1 depending on the
	 * outcome.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains two or three items.
	 * 
	 * @return a <code>Double</code>
	 * 
	 * @throws FunctionCallException if <code>args</code> has more than three or
	 *                               less than two items.
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
    {
    	final int argc = args.size();
        if (argc < 2 || argc > 3)
            throw new FunctionCallException( "compare-number() requires two or three arguments." );

        final Navigator nav = context.getNavigator();
        
        Double d1 = NumberFunction.evaluate(args.get(0), nav);
        Double d2 = NumberFunction.evaluate(args.get(1), nav);
        
		/*
		 * The standard Double.comparedTo() considers NaN greater than all other
		 * doubles. So when sorting ascending, they will end up last. If <nanFirst> is
		 * true we treat NaN as smaller than all other doubles, so they end up first.
		 */
        if (argc == 3 && BooleanFunction.evaluate(args.get(2), nav)) {
        
        	boolean nan1 = d1.isNaN(), nan2 = d2.isNaN();
        	if (nan1 && nan2) return 0; // both objects are NaN and considered equal
        	if (nan1 || nan2) return nan1 ? -1 : 1; // if first is NaN it is smaller, otherwise greater
        }
        
        // If we get here a regular compareTo applies. Must return a double!
        return Math.signum((double)d1.compareTo(d2)); 
    }

}

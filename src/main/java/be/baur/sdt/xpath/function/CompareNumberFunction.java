package be.baur.sdt.xpath.function;

import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.BooleanFunction;
import org.jaxen.function.NumberFunction;

/**
 * <code><i>double</i> sdt:compare-number( <i>number</i>, <i>number</i> )</code><br>
 * <code><i>double</i> sdt:compare-number( <i>number</i>, <i>number</i>, <i>boolean nanFirst</i> )</code>
 * <p>
 * Compares two numbers. This function converts its arguments to numbers and
 * returns -1, 0 or 1, depending on whether the first argument is numerically
 * smaller, equal to or larger than the second:
 * <p>
 * <code>sdt:compare-number(1, 3)</code> returns <code>-1.0</code>.<br>
 * <code>sdt:compare-number(3, '3')</code> returns <code>0.0</code>.<br>
 * <code>sdt:compare-number('6', '4')</code> returns <code>1.0</code>.
 * <p>
 * Objects that are not numbers are considered equal, and greater than all other
 * numbers:
 * <p>
 * <code>sdt:compare-number('a', 1)</code> returns <code>1.0</code>.<br>
 * <code>sdt:compare-number('a', 'b')</code> returns <code>0.0</code>.
 * <p>
 * If the optional third argument evaluates to true, objects that are not
 * numbers are considered smaller than all numbers.
 * <p>
 * This function can be used as a comparator in a sort statement.
 */
public final class CompareNumberFunction implements Function
{
	public static final String NAME = "compare-number";
	
    /**
     * Create a new <code>CompareNumberFunction</code> object.
     */
    public CompareNumberFunction() {}

    
	/**
	 * Numerically compares two arguments, returning -1, 0 or 1. An optional third
	 * argument - after boolean evaluation - determines whether NaN is considered
	 * smaller (if true) or greater (the default) than all other numbers.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains two or three items.
	 * 
	 * @return a <code>Double</code>
	 * 
	 * @throws FunctionCallException if <code>args</code> has more than three or
	 *                               less than two items.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		final int argc = args.size();
		if (argc < 2 || argc > 3)
			throw new FunctionCallException(NAME + "() requires two or three arguments.");

		final Navigator nav = context.getNavigator();

		return evaluate(args.get(0), args.get(1), 
			argc == 3 && BooleanFunction.evaluate(args.get(2), nav), nav);
	}
    

	/**
	 * Numerically compares two objects, returning -1, 0 or 1.
	 *
	 * @param obj1     the first object to be compared
	 * @param obj2     the second object to be compared
	 * @param nanFirst whether NaN is considered smaller than all other numbers.
	 * @param nav      the navigator used
	 * 
	 * @return a <code>Double</code>
	 */
	public static Double evaluate(Object obj1, Object obj2, boolean nanFirst, Navigator nav) {

		final Double d1 = NumberFunction.evaluate(obj1, nav);
		final Double d2 = NumberFunction.evaluate(obj2, nav);

		/*
		 * The standard Double.comparedTo() considers NaN greater than all other
		 * doubles. So when sorting ascending, they will end up last. If <nanFirst> is
		 * true we treat NaN as smaller than all other doubles, so they end up first.
		 */
		if (nanFirst) {

			boolean nan1 = d1.isNaN(), nan2 = d2.isNaN();
			if (nan1 && nan2)
				return 0.0; // both objects are NaN and therefore considered equal
			if (nan1 || nan2)
				return nan1 ? -1.0 : 1.0; // if first is NaN it's smaller, else greater
		}

		// if we get here a regular compareTo applies
		return Math.signum((double) d1.compareTo(d2));
	}

}

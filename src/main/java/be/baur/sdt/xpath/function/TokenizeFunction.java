package be.baur.sdt.xpath.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.BooleanFunction;
import org.jaxen.function.NormalizeSpaceFunction;
import org.jaxen.function.StringFunction;

/**
 * <code><i>string*</i> sdt:tokenize( <i>string</i> )</code><br>
 * <code><i>string*</i> sdt:tokenize( <i>string</i>, <i>string pattern</i> )</code><br>
 * <code><i>string*</i> sdt:tokenize( <i>string</i>, <i>string pattern</i>, <i>boolean allowEmpty</i> )</code>
 * <p>
 * Breaks the supplied string into tokens and returns a sequence of strings. The
 * optional second argument is a regular expression that specifies the
 * delimiter(s). If absent, tokens are assumed to be whitespace delimited, and
 * the result is equivalent to
 * <code>sdt:tokenize(normalize-space(<i>string</i>),' ')</code>.
 * <p>
 * The optional third argument is a boolean indicating whether zero length
 * (empty) tokens are retained, the default being false. For example:
 * <p>
 * <code>sdt:tokenize(' a&nbsp;&nbsp;b&nbsp;&nbsp;&nbsp;c&nbsp;&nbsp;&nbsp;&nbsp;')</code>
 * returns <code>("a","b","c")</code>.<br>
 * <code>sdt:tokenize('127.0.0.1:80', '[\\.:]')</code> returns
 * <code>(127, 0, 0, 1, 80)</code>.<br>
 * <code>sdt:tokenize('a; b; ; c; ', '; ', true())</code> returns
 * <code>("a","b","", "c", "")</code>.
 */
public class TokenizeFunction implements Function
{

    /**
     * Create a new <code>TokenizeFunction</code> object.
     */
    public TokenizeFunction() {}

    
	/**
	 * Breaks the supplied string into tokens and returns a sequence of strings.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called.
	 * @param args    an argument list that contains one, two or three items.
	 * 
	 * @return a list of strings
	 * 
	 * @throws FunctionCallException if <code>args</code> has no or more than three
	 *                               items.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
	{
		final int argc = args.size();
		if (argc < 1 || argc > 3)
			throw new FunctionCallException("tokenize() requires one, two or three arguments.");

		final Navigator nav = context.getNavigator();

		if (argc == 1)
			return evaluate(NormalizeSpaceFunction.evaluate(args.get(0), nav), " ", false);
		else if (argc == 2)
			return evaluate(StringFunction.evaluate(args.get(0), nav), 
				StringFunction.evaluate(args.get(1), nav), false);
		else // argc == 3
			return evaluate(StringFunction.evaluate(args.get(0), nav), 
				StringFunction.evaluate(args.get(1), nav),
				BooleanFunction.evaluate(args.get(2), nav));
	}


	/**
	 * Breaks the supplied string into tokens and returns a sequence of strings.
	 *
	 * @param str        the string to be tokenized
	 * @param rex        the delimiter regular expression
	 * @param allowEmpty whether to retain empty strings in the result
	 * 
	 * @return a list of strings
	 */
	public static List<String> evaluate(String str, String rex, boolean allowEmpty) {

		List<String> tokens = Arrays.asList(str.split(rex, allowEmpty ? -1 : 0));

		if (!allowEmpty) {
			tokens = new ArrayList<String>(tokens); // make tokens mutable
			tokens.removeIf(s -> s.isEmpty()); // to remove the empty ones
		}
		return tokens;
	}
}

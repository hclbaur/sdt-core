package be.baur.sdt.xpath.function;

import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;

/**
 * <code><i>string*</i> sdt:tokenize( <i>string</i> )</code><br>
 * <code><i>string*</i> sdt:tokenize( <i>string</i>, <i>pattern</i> )</code><br>
 * <code><i>string*</i> sdt:tokenize( <i>string</i>, <i>string pattern</i>, <i>boolean allowEnpty</i> )</code>
 * <p>
 * Breaks the supplied string into tokens and returns a sequence of strings. The
 * second argument is a regular expression that specifies the delimiter(s). If
 * absent, tokens are assumed to be whitespace delimited, and the result is
 * equivalent to <code>sdt:tokenize( normalize-space(<i>string</i>),' ')</code>.
 * <p>
 * The optional third argument is a boolean indicating whether zero length
 * tokens are retained, the default being false. For example:
 * <p>
 * <code>sdt:tokenize(' a&nbsp;&nbsp;b&nbsp;&nbsp;&nbsp;c&nbsp;&nbsp;&nbsp;&nbsp;')<code> 
 * returns <code>{"a","b","c"}</code>.<br>
 * <code>sdt:tokenize('a|b||d', '|')</code> returns
 * <code>{"a","b","d"}</code>.<br>
 * <code>sdt:tokenize('a|b||d', '|', true())</code> returns
 * <code>{"a","b","","d"}</code>.
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
	 * @return a <code>List<String></code>
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
            throw new FunctionCallException( "tokenize() requires one, two or three arguments." );

        return null;       
    }

}

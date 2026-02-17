package be.baur.sdt.function;

import java.util.Iterator;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

/**
 * <code><i>string</i> fn:string-join( <i>node-set</i> )</code><br>
 * <code><i>string</i> fn:string-join( <i>node-set</i>, <i>string separator</i> )</code>
 * <p>
 * Returns a string created by concatenating the items in a sequence, with an
 * optional separator between adjacent items. If the sequence is empty, the
 * function returns the zero-length string.
 * 
 * @see <a href=
 *      "https://www.w3.org/TR/xpath-functions/#func-string-join">Section 5.4.2
 *      of the XPath Specification</a>
 */
public final class StringJoinFunction implements Function
{
	public static final String NAME = "string-join";
	
    /**
     * Create a new <code>StringJoinFunction</code> object.
     */
    public StringJoinFunction() {}
    
	/**
	 * Returns a concatenation of all items in a list, with or without separator.
	 *
	 * @param context the expression context
	 * @param args    a list that contains one or two items
	 * @return a string
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
    @Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException
    {
    	final int argc = args.size();
        if (argc < 1 || argc > 2)
        	throw new FunctionCallException(NAME + "() requires one or two arguments." );

        if (! (args.get(0) instanceof List) )
        	throw new FunctionCallException(NAME + "() expects a node-set.");
        
        List list = (List) args.get(0);
        if (list.isEmpty()) return "";
        
        Navigator nav = context.getNavigator();      
        final String separator = (argc == 2) ? StringFunction.evaluate(args.get(1), nav) : "";
        
        Iterator it = list.iterator();
        StringBuilder sb = new StringBuilder(StringFunction.evaluate(it.next(), nav));
        while (it.hasNext()) {
        	sb.append(separator).append(StringFunction.evaluate(it.next(), nav));
        }
        return sb.toString();
    }
    
}

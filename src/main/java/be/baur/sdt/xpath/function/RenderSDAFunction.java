package be.baur.sdt.xpath.function;

import java.io.IOException;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;
import org.jaxen.function.BooleanFunction;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sda.io.SDAFormatter;

/**
 * <code><i>string</i> sdt:render-sda( <i>node(set)</i> )</code><br>
 * <code><i>string</i> sdt:render-sda( <i>node(set)</i>, <i>boolean pretty</i> )</code>
 * <p>
 * Renders the first SDA node in the set as an SDA string in "canonical" format.
 * If the optional second argument evaluates to true, the default SDA formatter
 * is used to produce a reader friendly representation.
 * <p>
 * This functions returns an empty string if the node set is empty or contains
 * something that is not an SDA node.
 * 
 * @see DataNode#toString
 * @see SDAFormatter
 */
public final class RenderSDAFunction implements Function
{
	public static final String NAME = "render-sda";
	
	/**
     * Create a new <code>RenderSDAFunction</code> object.
     */
    public RenderSDAFunction() {}

    
	/**
	 * Formats an SDA node as text.
	 *
	 * @param context the expression context
	 * @param args    an argument list that contains one or two items
	 * @return a string
	 * @throws FunctionCallException if an inappropriate number of arguments is
	 *                               supplied, or if evaluation failed
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		final int argc = args.size();
		if (argc < 1 || argc > 2)
			throw new FunctionCallException(NAME + "() requires one or two arguments.");

		Navigator nav = context.getNavigator();
		return evaluate(args, argc == 2 && BooleanFunction.evaluate(args.get(1), nav), nav);
	}
    

	/**
	 * Formats the first node in a list as an SDA string.
	 *
	 * @param list   a list of nodes
	 * @param pretty whether to format reader friendly
	 * @param nav    the navigator used
	 * @return a string
	 * @throws FunctionCallException if evaluation failed
	 */
	@SuppressWarnings("rawtypes")
	private static String evaluate(List list, boolean pretty, Navigator nav) throws FunctionCallException {

		if (! list.isEmpty()) {

			Object first = list.get(0);
			if (first instanceof List)
				return evaluate((List) first, pretty, nav);

			if (first instanceof DataNode) {
				if (pretty) {
					try {
						return SDA.format((DataNode) first);
					} catch (IOException e) {
						throw new FunctionCallException(e);
					}
				} else
					return first.toString();
			}
		}
		return "";
	}
    
}

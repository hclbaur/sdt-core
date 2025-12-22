package be.baur.sdt.xpath.function;

import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.Navigator;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sdt.xpath.DocumentNavigator;

/**
 * <code><i>node</i> sdt:document-node( <i>node(set)</i> )</code><br>
 * <p>
 * Constructs a new document node from the first SDA node in the set.
 * <p>
 * This function is supplied mainly for backwards compatibility reasons.
 * 
 * @see DocumentNavigator#newDocumentNode
 */
public class DocumentNodeFunction implements Function
{
	public static final String NAME = "document-node";
			
	/**
     * Create a new <code>DocumentNodeFunction</code> object.
     */
    public DocumentNodeFunction() {}

    
	/**
	 * Creates a document node.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains one item.
	 * 
	 * @return a document node
	 * 
	 * @throws FunctionCallException if <code>args</code> has more or less than one
	 *                               item.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		if (args.size() != 1)
			throw new FunctionCallException("document-node() expects exactly one argument.");

		return evaluate(args, context.getNavigator());
	}
    

	/**
	 * Constructs a new document node from the first SDA node in the list.
	 *
	 * @param list   a list of nodes
	 * @param nav    the navigator used
	 * 
	 * @return a document node
	 * @throws FunctionCallException if an exception occurs.
	 */
	@SuppressWarnings("rawtypes")
	public static Node evaluate(List list, Navigator nav) throws FunctionCallException {

		if (! list.isEmpty()) {

			Object first = list.get(0);
			if (first instanceof List)
				return evaluate((List) first, nav);

			if (first instanceof DataNode)
				return DocumentNavigator.newDocumentNode(((DataNode) first).copy());
		}
		// else
		throw new FunctionCallException("document-node() expects a data node.");
	}
}

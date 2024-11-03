package be.baur.sdt.xpath.function;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.function.StringFunction;

import be.baur.sda.DataNode;
import be.baur.sda.SDA;
import be.baur.sda.serialization.SDAParseException;
import be.baur.sda.serialization.SDAParser;

/**
 * <code><i>node</i> sdt:parse-sda( <i>string</i> )</code><br>
 * <p>
 * Parses a string in SDA format and returns a data node.
 * 
 * @see SDAParser
 */
public class ParseSDAFunction implements Function
{

	/**
     * Create a new <code>ParseSDAFunction</code> object.
     */
    public ParseSDAFunction() {}

    
	/**
	 * Parses a string in SDA format and returns a data node.
	 *
	 * @param context the context at the point in the expression when the function
	 *                is called
	 * @param args    an argument list that contains one item.
	 * 
	 * @return a <code>DataNode</code>
	 * 
	 * @throws FunctionCallException if <code>args</code> has more or less than one
	 *                               item.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Object call(Context context, List args) throws FunctionCallException {

		final int argc = args.size();
		if (argc != 1)
			throw new FunctionCallException("parse-sda() requires exactly one argument.");

		return evaluate(StringFunction.evaluate(args.get(0), context.getNavigator()));
	}
    

	/**
	 * Parses a string in SDA format and returns a data node.
	 *
	 * @param str a string in SDA format
	 * 
	 * @return a <code>DataNode/code>
	 * @throws FunctionCallException if an exception occurs.
	 */
	public static DataNode evaluate(String str) throws FunctionCallException {

		try {
			return SDA.parse(new StringReader(str));
		} catch (SDAParseException | IOException e) {
			throw new FunctionCallException(e);
		}
	}
    
}

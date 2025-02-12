package be.baur.sdt.transform;

import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;

/**
 * The {@code ParamStatement} evaluates an XPath expression and assigns the
 * result to a variable. The resulting value is considered a default that can be
 * overwritten by the transformation context - in other words - a parameter.
 * Unlike regular variables, parameters can be declared in the context of a
 * transform only, and are not mutable during execution.
 * 
 * @see VariableStatement
 */
public class ParamStatement extends VariableStatement {

	/**
	 * Creates a {@code ParamStatement}.
	 * 
	 * @param name  the name of the parameter, not null
	 * @param xpath the XPath to be evaluated, not null
	 * @throws IllegalArgumentException if name is invalid
	 */
	public ParamStatement(String name, XPath xpath) {
		super(name, xpath);
	}

	
	@Override void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: if the statement context already contains a parameter this name,
		 * an exception is thrown, because parameters can be declared only once.
		 * Otherwise: if the transformation context defines the parameter, use its value
		 * to add the parameter to the statement context. If it does not, execute the
		 * super method - which will evaluate the XPath value of the parameter and add
		 * it to the statement context (as if it were a regular variable).
		 */
		final String param = getVarName();
		
		if (staco.getVariableContext(null, param) != null)
			throw new TransformException(this, "parameter '" + param + "' cannot be reassigned.");
			
		Object value = traco.getParameters().get(param);
		if (value != null)
			staco.setVariableValue(null, param, value);
		else
			super.execute(traco, staco); // wise?
	}
	
	
	/**
	 * @return a data node representing:<br><br>
	 *         <code>param "<i>name</i>" { select "<i>expression</i>" }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = super.toSDA();
		node.setName(Keyword.PARAM.tag);
		return node;
	}

}

package be.baur.sdt.statements;

import be.baur.sda.Node;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The {@code ParamStatement} evaluates an XPath expression and assigns the
 * result to a variable. The resulting value is considered a default that can be
 * overwritten by the transformation context - in other words - a parameter.
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
	public ParamStatement(String name, SDAXPath xpath) {
		super(name, xpath);
		setName(Statements.PARAM.tag);
	}

	
	@Override
	public void execute(TransformContext tracon, StatementContext stacon) throws TransformException {
		/*
		 * Execution: if the statement context already contains a parameter this name,
		 * an exception is thrown, because parameters can be declared only once.
		 * Otherwise: if the transformation context defines the parameter, use its value
		 * to add the parameter to the statement context. If it does not, execute the
		 * super method - which will evaluate the XPath value of the parameter and add
		 * it to the statement context (as if it were a regular variable).
		 */
		String name = getValue(); // icky :(
		
		if (stacon.hasVariable(null, name))
			throw new TransformException(this, "parameter '" + name + "' cannot be redeclared.");
			
		Object value = tracon.getParameters().get(name);
		if (value != null)
			stacon.setVariableValue(null, name, value);
		else
			super.execute(tracon, stacon);
	}
	
	
	/**
	 * @return a node representing<br>
	 *         <code>param "<i>name</i>" { select "<i>expression</i>" }</code>
	 */
	@Override
	public Node toNode() {
		Node node = super.toNode();
		node.setName(Statements.PARAM.tag);
		return node;
	}

}

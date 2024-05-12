package be.baur.sdt.statements;

import java.io.Writer;

import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Attribute;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The {@code PrintStatement} evaluates an XPath expression and writes the
 * result to the output stream with an optional line termination separator.
 */
public class PrintStatement extends XPathStatement {

	private final boolean terminate; // whether to terminate the line
	
	/**
	 * Creates a {@code PrintStatement} with optional line termination.
	 * 
	 * @param xpath     the XPath to be evaluated, not null
	 * @param terminate whether to terminate the line
	 */
	public PrintStatement(SDAXPath xpath, boolean terminate) {
		super(xpath);
		this.terminate = terminate;
	}

	
	/**
	 * Returns true if this print statement terminates the line.
	 * 
	 * @return true or false
	 */
	public boolean isTerminate() {
		return terminate;
	}


	@Override void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context and perform a String evaluation. The result (and an optional EOL
		 * separator) is written to the output and flushed.
		 */
		try {
			SDAXPath xpath = new SDAXPath(getExpression()); 
			xpath.setVariableContext(staco);
			String value = xpath.stringValueOf(staco.getContextNode());
			
			Writer writer = traco.getWriter();
			writer.write(value); 
			if (terminate) 
				writer.write(System.lineSeparator());
			writer.flush();
			
		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}
	

	/**
	 * @return a node representing<br>
	 *         <code>print(ln) { value "<i>expression</i>" }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(terminate ? Statements.PRINTLN.tag : Statements.PRINT.tag);
		node.add( new DataNode(Attribute.VALUE.tag, getExpression()) ); 
		return node;
	}

}

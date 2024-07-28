package be.baur.sdt.transform;

import java.util.List;

import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>CopyStatement</code> evaluates an XPath expression and creates a
 * deep copy of the resulting node(s).
 */
public class CopyStatement extends XPathStatement {

	/**
	 * Creates a CopyStatement with an expression of what to copy.
	 * 
	 * @param xpath an XPath object, not null
	 */
	public CopyStatement(SDAXPath xpath) {
		super(xpath);
	}

	
	@SuppressWarnings("rawtypes")
	@Override void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * Execution: create an XPath from the statement expression, set the variable
		 * context and evaluate. If the result is a node set, copy and add the node(s)
		 * to the current output node. Otherwise, do nothing.
		 */
		try {
			XPath xpath = new SDAXPath(getExpression());
			xpath.setVariableContext(staco);
			Object value = xpath.evaluate(staco.getContextNode());

			if (!(value instanceof List)) return;
			
			for (Object object : (List) value) {
				if (object instanceof DataNode)
					staco.getOutputNode().add(((DataNode) object).copy());
			}

		} catch (Exception e) {
			throw new TransformException(this, e);
		}
	}


	/**
	 * @return a data node representing:<br><br>
	 *         <code>copy { select "<i>expression</i>" }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Keyword.COPY.tag, getExpression());
		return node;
	}

}

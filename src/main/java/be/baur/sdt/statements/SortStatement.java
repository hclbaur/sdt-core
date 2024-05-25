package be.baur.sdt.statements;

import java.util.Comparator;
import java.util.Objects;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.JaxenRuntimeException;

import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.serialization.Statements;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>SortStatement</code> occurs only in the context of a for-each loop.
 * It evaluates an XPath expression and sorts the iterated set on the value of
 * the selected node. The default sort order is ascending, but can be reversed
 * using the REVERSE keyword with an expression that is evaluated to a boolean.
 * 
 * @see ForEachStatement
 */
public class SortStatement extends XPathStatement {

	/* Expression that determines if sort order is reversed (descending). */
	private String reverseExpression;
	

	/**
	 * Creates a SortStatement.
	 * 
	 * @param xpath the XPath to be evaluated, not null
	 */
	public SortStatement(SDAXPath xpath) {
		super(xpath);
	}
	
	
	/**
	 * Sets the XPath expression to determine whether sort order is reversed. If the
	 * expression evaluates to true, sort order is descending instead of ascending.
	 * 
	 * @param xpath an XPath object, not null
	 */
	public void setReverseExpression(SDAXPath xpath) {
		reverseExpression = Objects.requireNonNull(xpath, "xpath must not be null").toString();
	}


	/**
	 * Returns the XPath expression text that determine if sort order is reversed. If the
	 * expression is non-null and evaluates to true, sort order is descending.
	 * 
	 * @return an expression string, may be null
	 */
	public String getReverseExpression() {
		return reverseExpression;
	}


	@Override 
	void execute(TransformContext traco, StatementContext staco) throws TransformException {
		/*
		 * This method does nothing. Execution takes place in the context of the ForEachStatement.
		 */
	}
	
	
	/**
	 * @return a data node representing:
	 *         <code>sort "<i>expression</i>" { [reverse "<i>expression</i>"] }</code>
	 */
	@Override
	public DataNode toSDA() {
		DataNode node = new DataNode(Statements.SORT.tag, getExpression());
		if (getReverseExpression() != null) 
			node.add( new DataNode(Statements.REVERSE.tag, getReverseExpression()) );
		return node;
	}


	
	/**
	 * A {@code NodeComparator} can be used to sort nodes in lexicographical order
	 * on the outcome of an XPath expression evaluated to a string value. For
	 * example, if<br>
	 * <br>
	 * 
	 * <code>node1 = item { name "foo" }</code> and
	 * <code>node2 = item { name "bar" }</code> then<br>
	 * <code>new NodeComparator(new BaseXPath("name")).compare(node1, node2)</code>
	 * returns 1
	 */
	public static class NodeComparator implements Comparator<DataNode> {
		
		final BaseXPath xpath;
		
		NodeComparator(BaseXPath xpath) {
			this.xpath = xpath;
		}
		
		@Override
		public int compare(DataNode n1, DataNode n2) {
		  	String s1, s2;
			try {
				s1 = xpath.stringValueOf(n1);
				s2 = xpath.stringValueOf(n2);
			} catch (JaxenException e) {
				throw new JaxenRuntimeException(e);
			}
			return s1.compareTo(s2);
		}
	  }
}

package be.baur.sdt.statements;

import java.util.Comparator;
import java.util.Objects;

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
 * It evaluates an XPath expression and sorts the iterated set using the value
 * of the selected node as a sorting key. Methods are supplied to control the
 * sorting order and type.
 * 
 * @see ForEachStatement
 */
public class SortStatement extends XPathStatement {

	private String reverseExpression; // Expression that determines if order is reversed (descending)
	private String comparatorExpression; // Expression that determines how keys are compared
	

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
	
	
	/**
	 * Sets the XPath comparator expression to be evaluated during sorting. The
	 * comparator should accept two objects and return -1, 0, or 1 depending on
	 * whether the first object is smaller than, equal to, or greater than the
	 * second object.
	 * <p>
	 * No attempt is made to assert validity of the given expression, other than the
	 * requirement that it must contain exactly two question marks, which act as a
	 * placeholder for the objects to be compared.
	 * <p>
	 * Example: {@code setComparatorExpression("sdt:compare-number(?,?)"); }
	 * 
	 * @param compexpr an expression string, not null
	 * @throws IllegalArgumentException if the expression is invalid
	 */
	public void setComparatorExpression(String expression) {
		comparatorExpression = Objects.requireNonNull(expression, "expression must not be null");
		if (comparatorExpression.chars().filter(c -> c =='?').count() != 2)
			throw new IllegalArgumentException("expression must contain exactly two placeholders");
	}


	/**
	 * Returns the XPath comparator expression text that is evaluated during
	 * sorting, if a specific comparator expression has been set.
	 * 
	 * @return an expression string, may be null
	 */
	public String getComparatorExpression() {
		return comparatorExpression;
	}


	/**
	 * Returns a comparator appropriate for this sort statement. If no specific
	 * comparator expression has been set, a lexicographical compare is implied.
	 * 
	 * @param context the current statement context
	 * @return a Comparator, not null
	 */
	public Comparator<DataNode> getComparator(StatementContext context) throws JaxenException {

		SDAXPath xpath = new SDAXPath(getExpression());
		xpath.setVariableContext(context);

		Comparator<DataNode> comparator = new Comparator<DataNode>() {
			@Override
			public int compare(DataNode node1, DataNode node2) {
				String s1, s2;
				try {
					s1 = xpath.stringValueOf(node1);
					s2 = xpath.stringValueOf(node2);
					if (comparatorExpression != null) {
						String comexpr = comparatorExpression.replaceFirst("\\?", "'"+s1+"'");
						SDAXPath comxp = new SDAXPath(comexpr.replaceFirst("\\?", "'"+s2+"'"));
						comxp.setVariableContext(context);
						return (int) Math.signum((double) comxp.numberValueOf(context.getContextNode()));
					}
				} catch (JaxenException e) {
					throw new JaxenRuntimeException(e);
				}
				return s1.compareTo(s2);
			}
		};

		if (reverseExpression != null) {
			SDAXPath revxp = new SDAXPath(reverseExpression);
			revxp.setVariableContext(context);
			if (revxp.booleanValueOf(context.getContextNode()))
				return comparator.reversed();
		}

		return comparator;
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
		if (getComparatorExpression() != null) 
			node.add( new DataNode(Statements.COMPARATOR.tag, getComparatorExpression()) );
		return node;
	}
}

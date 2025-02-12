package be.baur.sdt.transform;

import java.util.Comparator;
import java.util.Objects;

import org.jaxen.JaxenException;
import org.jaxen.JaxenRuntimeException;
import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sdt.StatementContext;
import be.baur.sdt.TransformContext;
import be.baur.sdt.TransformException;
import be.baur.sdt.parser.Keyword;
import be.baur.sdt.xpath.SDAXPath;

/**
 * The <code>SortStatement</code> can only occur in the context of a for-each
 * loop. It evaluates an XPath expression and sorts the iterated set using the
 * string value of the selected node as a sorting key. Methods are supplied to
 * control the sorting order and type.
 * 
 * @see ForEachStatement
 */
public class SortStatement extends XPathStatement {

	private String reverseExpression; // expression that determines if order is reversed (descending)
	private String comparatorExpression; // expression that determines how keys are compared
	

	/**
	 * Creates a SortStatement.
	 * 
	 * @param xpath the XPath to be evaluated, not null
	 */
	public SortStatement(XPath xpath) {
		super(xpath);
	}
	
	
	/**
	 * Sets the XPath expression to determine whether sort order is reversed. If the
	 * expression evaluates to true, sort order is descending. If no expression was
	 * set, sort order should be ascending by default.
	 * 
	 * @param xpath an XPath object, not null
	 */
	public void setReverseExpression(XPath xpath) {
		reverseExpression = Objects.requireNonNull(xpath, "xpath must not be null").toString();
	}


	/**
	 * Returns the XPath expression text that determines whether sort order is
	 * reversed. If no expression is set (is null), sort order should be ascending
	 * by default.
	 * 
	 * @return an expression string, may be null
	 */
	public String getReverseExpression() {
		return reverseExpression;
	}
	
	
	/**
	 * Sets the comparator XPath expression text to be evaluated during sorting. The
	 * comparator should accept two objects and return a negative integer, zero, or
	 * a positive integer depending on whether the first object is smaller than,
	 * equal to, or greater than the second object.
	 * <p>
	 * No attempt is made to assert validity of the given expression, other than the
	 * requirement that it must contain exactly two question marks, which act as a
	 * placeholder for the objects to be compared.
	 * <p>
	 * Example: {@code setComparatorExpression("sdt:compare-number(?,?)"); }
	 * 
	 * @param expression an expression, not null
	 * @throws IllegalArgumentException if the expression is invalid
	 */
	public void setComparatorExpression(String expression) {
		comparatorExpression = Objects.requireNonNull(expression, "expression must not be null");
		if (comparatorExpression.chars().filter(c -> c =='?').count() != 2)
			throw new IllegalArgumentException("expression must contain exactly two placeholders");
	}


	/**
	 * Returns the comparator XPath expression text that is evaluated during
	 * sorting, if a specific comparator expression has been set.
	 * 
	 * @return an expression, may be null
	 */
	public String getComparatorExpression() {
		return comparatorExpression;
	}


	/**
	 * Returns a comparator appropriate for this sort statement. If no specific
	 * comparator expression has been set, a lexicographical compare is implied.
	 * 
	 * @param context the current statement context
	 * @return a comparator, not null
	 * @throws JaxenException if an XPath evaluation error occurs
	 */
	public Comparator<Object> getComparator(StatementContext context) throws JaxenException {

		XPath xpath = new SDAXPath(getExpression());
		xpath.setVariableContext(context);

		Comparator<Object> comparator = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				String s1, s2;
				try {
					s1 = xpath.stringValueOf(o1);
					s2 = xpath.stringValueOf(o2);
					if (comparatorExpression != null) {
						String comexpr = comparatorExpression.replaceFirst("\\?", "'"+s1+"'");
						XPath comxp = new SDAXPath(comexpr.replaceFirst("\\?", "'"+s2+"'"));
						comxp.setVariableContext(context);
						return (int) Math.signum((double) comxp.numberValueOf(context.getXPathContext()));
					}
				} catch (JaxenException e) {
					throw new JaxenRuntimeException(e);
				}
				return s1.compareTo(s2);
			}
		};

		if (reverseExpression != null) {
			XPath revxp = new SDAXPath(reverseExpression);
			revxp.setVariableContext(context);
			if (revxp.booleanValueOf(context.getXPathContext()))
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
		DataNode node = new DataNode(Keyword.SORT.tag, getExpression());
		if (getReverseExpression() != null) 
			node.add( new DataNode(Keyword.REVERSE.tag, getReverseExpression()) );
		if (getComparatorExpression() != null) 
			node.add( new DataNode(Keyword.COMPARATOR.tag, getComparatorExpression()) );
		return node;
	}
}

package be.baur.sdt.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.jaxen.XPath;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sda.serialization.Parser;
import be.baur.sdt.SDT;
import be.baur.sdt.transform.ChooseStatement;
import be.baur.sdt.transform.CopyStatement;
import be.baur.sdt.transform.ForEachStatement;
import be.baur.sdt.transform.IfStatement;
import be.baur.sdt.transform.NodeStatement;
import be.baur.sdt.transform.OtherwiseStatement;
import be.baur.sdt.transform.ParamStatement;
import be.baur.sdt.transform.PrintStatement;
import be.baur.sdt.transform.SortStatement;
import be.baur.sdt.transform.Statement;
import be.baur.sdt.transform.Transform;
import be.baur.sdt.transform.VariableStatement;
import be.baur.sdt.transform.WhenStatement;
import be.baur.sdt.xpath.SDAXPath;

/**
 * This is the default SDT parser; used to read and parse SDT content to create
 * a {@code Transform}. For example, when processing the following input:
 *
 * <pre>
 * transform {
 *    node "greeting" {
 *       node "message" { value "'hello world'" }
 *    }
 * }
 * </pre>
 *
 * the parser returns a <code>Transform</code> creating an SDA node named
 * 'greeting' with a single child node 'message' and a string value, like:
 *
 * <pre>
 * greeting {
 *    message "hello world"
 * }
 * </pre>
 * 
 * @see Transform
 */
public final class SDTParser implements Parser<Transform> {

	private static final String ATTRIBUTE_EXPECTS_NO_COMPOUND = "attribute '%s' expects no compound statement";
	private static final String ATTRIBUTE_NOT_ALLOWED = "attribute '%s' is not allowed here";
	private static final String KEYWORD_UNKNOWN = "keyword '%s' is unknown";
	private static final String NODE_NAME_INVALID = "node name '%s' is invalid";
	private static final String PARAMETER_REASSIGNED = "parameter '%s' cannot be reassigned";
	private static final String PARAM_OVERWRITES_VARIABLE = "parameter '%s' cannot overwrite variable";
	private static final String STATEMENT_EXPECTED = "'%s' statement expected";
	private static final String ATTRIBUTE_EXPECTED_IN = "'%s' attribute expected in '%s'";
	private static final String STATEMENT_EXPECTED_IN = "'%s' statement expected in '%s'";
	private static final String STATEMENT_EXPECTS_NO_COMPOUND = "statement '%s' expects no compound statement";
	private static final String STATEMENT_MISPLACED = "statement '%s' is misplaced";
	private static final String STATEMENT_NOT_ALLOWED = "statement '%s' is not allowed here";
	private static final String ATTRIBUTE_NOT_SINGULAR = "attribute '%s' can occur only once";
	private static final String STATEMENT_REQUIRES_COMPOUND = "statement '%s' requires a compound statement";
	private static final String STATEMENT_REQUIRES_NODENAME = "statement '%s' requires a node name";
	private static final String STATEMENT_REQUIRES_NO_EXPRESSION = "statement '%s' requires no expression";
	private static final String STATEMENT_REQUIRES_VARIABLE = "statement '%s' requires a variable name";
	private static final String STATEMENT_REQUIRES_EXPRESSION = "statement '%s' requires an expression";
	private static final String ATTRIBUTE_REQUIRES_EXPRESSION = "attribute '%s' requires an expression";
	private static final String VARIABLE_NAME_INVALID = "variable name '%s' is invalid";
	private static final String VARIABLE_OVERWRITES_PARAM = "variable '%s' cannot overwrite parameter";

	/**
	 * Creates a transform from a character input stream in SDT format.
	 * 
	 * @return a transform
	 * @throws IOException       if an I/O operation failed
	 * @throws SDTParseException if an SDT parse exception occurs
	 */
	@Override
	public Transform parse(Reader input) throws IOException, SDTParseException {

		final DataNode sdt;
		try {
			sdt = SDA.parse(input);
		} catch (Exception e) {
			throw new SDTParseException(null, e);
		}
		return parse(sdt);
	}


	/**
	 * Creates a transform from an SDA node representing a transformation recipe
	 * (what an SDA parser returns upon processing an input stream in SDT format).
	 * 
	 * @param sdt a node with a transformation recipe
	 * @return a transform
	 * @throws SDTParseException if a parse exception occurs
	 */
	public static Transform parse(final DataNode sdt) throws SDTParseException {

		if (! sdt.getName().equals(Keyword.TRANSFORM.tag))
			throw exception(sdt, STATEMENT_EXPECTED, Keyword.TRANSFORM);

		if (! sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NO_EXPRESSION, sdt.getName());

		if (sdt.isLeaf()) // a transform should have a compound statement, even if empty
			throw exception(sdt, STATEMENT_REQUIRES_COMPOUND, Keyword.TRANSFORM);

		validateStatement(sdt, null, null);
		
		Transform transform = new Transform();
		for (Node node : sdt.nodes())  // parse and add child statements
			transform.add(parseStatement((DataNode) node));

		return transform;
	}


	/**
	 * This method parses an SDA node representing an SDT statement, and returns a
	 * Statement which itself may contain other statements. Whatever we get must be
	 * an existing statement - the assumption is that validateStatement() has been
	 * called prior to calling this method.
	 */
	private static Statement parseStatement(final DataNode sdt) throws SDTParseException {

		final String name = sdt.getName();
		final Keyword kw = Keyword.get(name);

		Statement stat;

		switch (kw) {
			case CHOOSE: stat = parseChoose(sdt); break;
			case COPY: stat = parseCopy(sdt); break;
			case FOREACH: stat = parseForEach(sdt); break;
			case IF: stat = parseIf(sdt); break;
			case NODE: stat = parseNode(sdt); break;
			case OTHERWISE: stat = parseOtherwise(sdt); break;
			case PARAM: stat = parseVariableOrParam(sdt); break;
			case PRINT: stat = parsePrintOrPrintLn(sdt); break;
			case PRINTLN: stat = parsePrintOrPrintLn(sdt); break;
			case SORT: stat = parseSort(sdt); break;
			case VARIABLE: stat = parseVariableOrParam(sdt); break;
			case WHEN: stat = parseWhen(sdt); break;
			default: // we should never get here, unless we forgot to implement a statement
				throw new RuntimeException("SDT statement '" + name + "' not implemented!");
		}

		return stat;
	}


	/**
	 * This method parses an SDA node representing a CHOOSE statement. Expected is a
	 * parent node with at least one WHEN statement and an optional OTHERWISE
	 * statement.
	 */
	private static ChooseStatement parseChoose(final DataNode sdt) throws SDTParseException {
	
		if (! sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NO_EXPRESSION, sdt.getName());
		
		if (! sdt.isParent()) // at least one "when" statement is expected
			throw exception(sdt, STATEMENT_EXPECTED_IN, "when", sdt.getName());
	
		validateStatement(sdt, null, Arrays.asList(Keyword.WHEN, Keyword.OTHERWISE)); 
	
		ChooseStatement choose = null;
		int iterations = 0, last = sdt.nodes().size();
		for (Node node : sdt.nodes()) {
	
			++iterations; 
			final Statement stat = parseStatement((DataNode) node);
	
			if (iterations == 1) {
				if (! (stat instanceof WhenStatement))
					throw exception(node, STATEMENT_EXPECTED, Keyword.WHEN.tag);
				choose = new ChooseStatement((WhenStatement) stat);
				continue;
			}
	
			if (stat instanceof OtherwiseStatement && iterations < last)
				throw exception(node, STATEMENT_MISPLACED, Keyword.OTHERWISE.tag);
	
			choose.add(stat);
		}
	
		return choose;
	}


	/**
	 * This method parses an SDA node representing a COPY statement. Expected
	 * is a leaf node with an XPath expression as the value.
	 */
	private static CopyStatement parseCopy(final DataNode sdt) throws SDTParseException {

		return new CopyStatement(xpathFromNode(sdt));
	}


	/**
	 * This method parses an SDA node representing an FOREACH statement. Expected is
	 * a parent node with a compound statement and an XPath expression as the value.
	 * The compound may start with one or several consecutive SORT statements.
	 */
	private static ForEachStatement parseForEach(final DataNode sdt) throws SDTParseException {
		
		validateStatement(sdt, Arrays.asList(Keyword.GROUP), null);
		
		ForEachStatement foreach = new ForEachStatement(xpathFromNode(sdt));
		DataNode group = getAttribute(sdt, Keyword.GROUP, false);
		if (group != null) // set the optional value expression
			foreach.setGroupExpression(xpathFromNode(group));
		
		int iterations = 0, sortstatements = 0;
		for (Node node : sdt.nodes()) {

			if (node.getName().equals(Keyword.GROUP.tag)) continue; // skip group attribute
			++iterations; Statement stat = parseStatement((DataNode) node);
			
			if (stat instanceof SortStatement && ++sortstatements != iterations)
				throw exception(node, STATEMENT_MISPLACED, Keyword.SORT.tag);

			foreach.add(stat);
		}
		return foreach;
	}


	/**
	 * This method parses an SDA node representing an IF statement. Expected is a
	 * parent node with a compound statement and an XPath expression as the value.
	 */
	private static IfStatement parseIf(final DataNode sdt) throws SDTParseException {

		validateStatement(sdt, null, null);
		
		final IfStatement stat = new IfStatement(xpathFromNode(sdt));
		for (Node node : sdt.nodes()) // parse and add child statements
			stat.add(parseStatement((DataNode) node));

		return stat;
	}


	/**
	 * This method parses an SDA node representing a NODE statement. Expected is a
	 * parent node with a compound statement and a non-empty node name as the value.
	 * The compound statement may have a VALUE keyword with an XPath expression.
	 */
	private static Statement parseNode(final DataNode sdt) throws SDTParseException {

		validateStatement(sdt, Arrays.asList(Keyword.VALUE), null);
		
		final String nodename = sdt.getValue();
		if (nodename.isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NODENAME, sdt.getName());

		if (! SDA.isName(nodename))
			throw exception(sdt, NODE_NAME_INVALID, nodename);
		
		final NodeStatement stat = new NodeStatement(nodename);
		final DataNode value = getAttribute(sdt, Keyword.VALUE, false);
		if (value != null) // set the optional value expression
			stat.setValueExpression(xpathFromNode(value));

		for (Node node : sdt.find(n -> !n.getName().equals(Keyword.VALUE.tag))) // skip value attribute
			stat.add(parseStatement((DataNode) node)); // parse and add child statements

		return stat;
	}


	/**
	 * This method parses an SDA node representing an OTHERWISE statement. Expected
	 * is a parent node with a compound statement and no value.
	 */
	private static OtherwiseStatement parseOtherwise(final DataNode sdt) throws SDTParseException {

		validateStatement(sdt, null, null);
		
		if (! sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NO_EXPRESSION, sdt.getName());

		final OtherwiseStatement stat = new OtherwiseStatement();
		for (Node node : sdt.nodes()) // parse and add child statements
			stat.add(parseStatement((DataNode) node));

		return stat;
	}


	/**
	 * This method parses an SDA node representing a PRINT(LN) statement. Expected
	 * is a leaf node with an XPath expression as the value.
	 */
	private static PrintStatement parsePrintOrPrintLn(final DataNode sdt) throws SDTParseException {

		return new PrintStatement(xpathFromNode(sdt), sdt.getName().equals(Keyword.PRINTLN.tag));
	}


	/**
	 * This method parses an SDA node representing a SORT statement. Expected is
	 * either a leaf node or a parent node with an XPath expression as the value,
	 * and - in case of a parent node - an optional REVERSE keyword with an XPath
	 * expression and/or a COMPARATOR keyword with the name of an XPath comparator
	 * function.
	 */
	private static SortStatement parseSort(final DataNode sdt) throws SDTParseException {

		validateStatement(sdt, Arrays.asList(Keyword.REVERSE, Keyword.COMPARATOR), Arrays.asList());
		
		final SortStatement sort = new SortStatement(xpathFromNode(sdt));
		if (sdt.isLeaf()) return sort;

		DataNode reverse = getAttribute(sdt, Keyword.REVERSE, false);
		if (reverse != null)
			sort.setReverseExpression(xpathFromNode(reverse));

		DataNode comparator = getAttribute(sdt, Keyword.COMPARATOR, false);
		if (comparator != null)
			sort.setComparatorExpression(comparator.getValue());

		return sort;
	}


	/**
	 * This method parses an SDA node representing a VARIABLE or PARAM statement.
	 * Expected is a parent node with a non-empty variable name as the value, and a
	 * single, mandatory SELECT keyword with an XPath expression. Parameters must be
	 * declared globally (in the transform node) and not more than once. Variables
	 * can be declared anywhere any number of times. It is not possible to have both
	 * a parameter and a variable with the same name.
	 */
	private static VariableStatement parseVariableOrParam(final DataNode sdt) throws SDTParseException {

		validateStatement(sdt, Arrays.asList(Keyword.SELECT), Arrays.asList());
		
		final String varname = sdt.getValue();
		if (varname.isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_VARIABLE, sdt.getName());

		if (! SDT.isVariableName(varname))
			throw exception(sdt, VARIABLE_NAME_INVALID, varname);
		
		final Node parent = sdt.getParent();
		
		// find all declarations of a param with the specified name
		List<Node> params = parent.find(n -> n.getName().equals(Keyword.PARAM.tag) 
				&& ((DataNode) n).getValue().equals(varname));
		
		final boolean isParam = sdt.getName().equals(Keyword.PARAM.tag);
		
		if ( isParam ) {
			
			if (params.size() > 1) // got more than one param
				throw exception(params.get(1), PARAMETER_REASSIGNED, varname);
			
			List<Node> vars = parent.findDescendant(n -> n.getName().equals(Keyword.VARIABLE.tag) 
					&& ((DataNode) n).getValue().equals(varname)); // find variables with this name
			
			if (vars.size() > 0) // got a variable with the same name
				throw exception(vars.get(0), VARIABLE_OVERWRITES_PARAM, varname);
		}
		else { // a variable
			
			if (params.size() > 0) // got a param with the same name
				throw exception(params.get(0), PARAM_OVERWRITES_VARIABLE, varname);
		}
		
		DataNode select = getAttribute(sdt, Keyword.SELECT, true);
		return isParam 
			? new ParamStatement(varname, xpathFromNode(select))
			: new VariableStatement(varname, xpathFromNode(select));
	}


	/**
	 * This method parses an SDA node representing a WHEN statement. Expected is a
	 * parent node with a compound statement and an XPath expression as the value.
	 */
	private static WhenStatement parseWhen(final DataNode sdt) throws SDTParseException {

		validateStatement(sdt, null, null); // no attributes allowed

		WhenStatement stat = new WhenStatement(xpathFromNode(sdt));
		for (Node node : sdt.nodes()) // parse and add child statements
			stat.add(parseStatement((DataNode) node));

		return stat;
	}


	/* HELPER METHODS */


	/**
	 * This helper method creates an SDAXPath from an expression that is contained
	 * in the value of the supplied Node.
	 *
	 * @param node the Node to create the XPath from
	 * @throws SDTParseException if the XPath expression is invalid
	 */
	private static XPath xpathFromNode(final DataNode node) throws SDTParseException {

		if (node.getValue().isEmpty())
			throw exception(node, STATEMENT_REQUIRES_EXPRESSION, node.getName());

		XPath xpath;
		try {
			xpath = new SDAXPath(node.getValue());
		} catch (Exception e) {
			throw new SDTParseException(node, e);
		}
		return xpath;
	}

	
	/**
	 * This helper validates attributes and statements in a parent statement node.
	 *
	 * @param sdt   a node representing an SDT statement, not null
	 * @param atts  a list of allowed attributes, null if NONE are allowed
	 * @param stats a list of allowed statements, null if ALL are allowed
	 */
	private static void validateStatement(final DataNode sdt, List<Keyword> atts, List<Keyword> stats) throws SDTParseException {

		final Keyword parent = Keyword.get(sdt.getName());
		
		for (Node node : sdt.nodes()) {

			final String name = node.getName();
			Keyword kw = Keyword.get(name);
			if (kw == null) // no keyword with that name
				throw exception(node, KEYWORD_UNKNOWN, name);

			if (kw.isAttribute) {
				if (!node.isLeaf()) // all attributes are leaf nodes
					throw exception(node, ATTRIBUTE_EXPECTS_NO_COMPOUND, name);

				if (atts == null || !atts.contains(kw))
					throw exception(node, ATTRIBUTE_NOT_ALLOWED, name);
			}
			else if (kw.isLeaf != null) {
				if (kw.isLeaf && !node.isLeaf()) // statement should be leaf
					throw exception(node, STATEMENT_EXPECTS_NO_COMPOUND, name);

				if (!kw.isLeaf && node.isLeaf()) // statement should not be leaf
					throw exception(node, STATEMENT_REQUIRES_COMPOUND, name);

				if (stats != null && !stats.contains(kw))
					throw exception(node, STATEMENT_NOT_ALLOWED, name);
			}
			
			if (! kw.isAllowedIn(parent)) // this statement is not allowed in this context
				throw exception(node, STATEMENT_NOT_ALLOWED, name);
		}
	}


	/**
	 * This helper gets a specific attribute node from a parent statement node.
	 *
	 * @param sdt      is the statement node, with child nodes.
	 * @param att      is the attribute we want to retrieve.
	 * @param required controls the behavior:<br>
	 *                 when <em>true</em>, the attribute is required and an
	 *                 exception is thrown if absent.<br>
	 *                 when <em>false</em>, the attribute is optional and
	 *                 <code>null</code> is returned if absent.<br>
	 *                 when <em>null</em>, the attribute is forbidden and an
	 *                 exception is thrown if present.<br>
	 *                 An exception is also thrown if more than one attribute is
	 *                 found or if it has an empty value.
	 *
	 * @return a data node, may be null
	 * @throws SDTParseException
	 */
	private static DataNode getAttribute(final DataNode sdt, Keyword att, Boolean required) throws SDTParseException {

		List<DataNode> alist = sdt.find(n -> n.isLeaf() && n.getName().equals(att.tag));
		int size = alist.size();

		if (size == 0) {
			if (required == null || !required)
				return null;
			throw exception(sdt, ATTRIBUTE_EXPECTED_IN, att.tag, sdt.getName());
		}
		if (required == null)
			throw exception(sdt, ATTRIBUTE_NOT_ALLOWED, att.tag);

		DataNode node = alist.get(0);
		if (node.getValue().isEmpty())
			throw exception(node, ATTRIBUTE_REQUIRES_EXPRESSION, att.tag);
		if (size > 1)
			throw exception(node, ATTRIBUTE_NOT_SINGULAR, att.tag);

		return node;
	}

	
	/**
	 * Returns an SDT parse exception with an error node and formatted message.
	 * 
	 * @param node   the node where the error was found
	 * @param format a format message, and
	 * @param args arguments, as in {@link String#format}
	 * @return 
	 */
	private static SDTParseException exception(Node node, String format, Object... args) {
		return new SDTParseException(node, String.format(format, args));
	}
}

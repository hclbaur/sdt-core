package be.baur.sdt.serialization;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import be.baur.sda.DataNode;
import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sda.serialization.Parser;
import be.baur.sdt.statements.ChooseStatement;
import be.baur.sdt.statements.CopyStatement;
import be.baur.sdt.statements.ForEachStatement;
import be.baur.sdt.statements.IfStatement;
import be.baur.sdt.statements.NodeStatement;
import be.baur.sdt.statements.OtherwiseStatement;
import be.baur.sdt.statements.ParamStatement;
import be.baur.sdt.statements.PrintStatement;
import be.baur.sdt.statements.Statement;
import be.baur.sdt.statements.Transform;
import be.baur.sdt.statements.VariableStatement;
import be.baur.sdt.statements.WhenStatement;
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

	private static final String STATEMENT_EXPECTED = "'%s' statement expected";
	private static final String STATEMENT_EXPECTED_IN = "'%s' statement expected in '%s'";
	private static final String STATEMENT_MISPLACED = "statement '%s' is misplaced";
	private static final String STATEMENT_NOT_ALLOWED = "statement '%s' is not allowed here";
	private static final String STATEMENT_REQUIRES_XPATH = "statement '%s' requires an XPath expression";
	//private static final String STATEMENT_REQUIRES_NO_XPATH = "statement '%s' requires no XPath expression";
	private static final String STATEMENT_REQUIRES_VARIABLE = "statement '%s' requires a variable name";
	private static final String STATEMENT_REQUIRES_NODENAME = "statement '%s' requires a node name";
	private static final String STATEMENT_REQUIRES_NO_VALUE = "statement '%s' requires no value";
	private static final String STATEMENT_REQUIRES_COMPOUND = "statement '%s' requires a compound statement";
	private static final String STATEMENT_EXPECTS_NO_COMPOUND = "statement '%s' expects no compound statement";
	private static final String STATEMENT_NOT_SINGULAR = "statement '%s' can occur only once";
	private static final String STATEMENT_UNKNOWN = "statement '%s' is unknown";
	
	private static final String NODE_NAME_INVALID = "node name '%s' is invalid";
	private static final String VARIABLE_NAME_INVALID = "variable name '%s' is invalid";
	private static final String PARAMETER_REDECLARED = "parameter '%s' cannot be redeclared";


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
			throw new SDTParseException(null, e.getMessage(), e);
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

		if (! sdt.getName().equals(Transform.TAG))
			throw exception(sdt, STATEMENT_EXPECTED, Transform.TAG);

		if (! sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NO_VALUE, sdt.getName());

		if (sdt.isLeaf()) // a transform should have a compound statement, even if empty
			throw exception(sdt, STATEMENT_REQUIRES_COMPOUND, Transform.TAG);

		Transform transform = new Transform();
		for (Node node : sdt.nodes())  // parse and add child statements
			transform.add(parseStatement((DataNode) node));

		return transform;
	}


	/**
	 * This method parses an SDA node representing an SDT statement, and returns a
	 * Statement which itself may contain other statements. Whatever we get must be
	 * an existing leaf or parent statement. This method is called recursively and
	 * must deal with every possible statement.
	 */
	private static Statement parseStatement(final DataNode sdt) throws SDTParseException {

		final String name = sdt.getName();
		Statements statement = Statements.get(name);

		if (statement == null) // this statement is unknown
			throw exception(sdt, STATEMENT_UNKNOWN, name);

		if (statement.isLeaf && ! sdt.isLeaf()) // statement should be a leaf node
			throw exception(sdt, STATEMENT_EXPECTS_NO_COMPOUND, name);
		
		if (! statement.isLeaf && sdt.isLeaf()) // statement should not be a leaf node
			throw exception(sdt, STATEMENT_REQUIRES_COMPOUND, name);
		
		Statements parent = Statements.get(sdt.getParent().getName()); // returns null if parent is transform
		if (! statement.isAllowedIn(parent)) // this statement is not allowed in this context
			throw exception(sdt, STATEMENT_NOT_ALLOWED, name);

		Statement stat;

		switch (statement) {
			case CHOOSE: stat = parseChoose(sdt); break;
			case COPY: stat = parseCopy(sdt); break;
			case FOREACH: stat = parseForEach(sdt); break;
			case IF: stat = parseIf(sdt); break;
			case NODE: stat = parseNode(sdt); break;
			case OTHERWISE: stat = parseOtherwise(sdt); break;
			case PARAM: stat = parseVariableOrParam(sdt); break;
			case PRINT: stat = parsePrintOrPrintLn(sdt); break;
			case PRINTLN: stat = parsePrintOrPrintLn(sdt); break;
			case VARIABLE: stat = parseVariableOrParam(sdt); break;
			case WHEN: stat = parseWhen(sdt); break;
			default: // we should never get here, unless we forgot to implement a statement
				throw new RuntimeException("SDT statement '" + name + "' not implemented!");
		}

		return stat;
	}


	/**
	 * This method parses an SDA node representing a print(nl) statement, and
	 * returns a PrintStatement. Expected is a leaf node with an XPath expression
	 * as the value.
	 */
	private static PrintStatement parsePrintOrPrintLn(final DataNode sdt) throws SDTParseException {

		return new PrintStatement(xpathFromNode(sdt), sdt.getName().equals(Statements.PRINTLN.tag));
	}


	/**
	 * This method parses an SDA node representing an SDT variable or param
	 * statement, and returns a VariableStatement (or ParamStatement). Expected is a
	 * parent node with a non-empty variable name as the value, and a single,
	 * mandatory SELECT child statement for the XPath expression. Also, parameters
	 * must be declared globally (in the transform node) and not more than once.
	 */
	private static VariableStatement parseVariableOrParam(final DataNode sdt) throws SDTParseException {

		String varname = sdt.getValue();
		
		boolean isParam = sdt.getName().equals(Statements.PARAM.tag);	
		if ( isParam ) {
			final Node parent = sdt.getParent();
			if (! parent.getName().equals(Transform.TAG)) // parent cannot be null
				throw exception(sdt, STATEMENT_NOT_ALLOWED, sdt.getName());
			
			if (parent.find(n -> 
				n.getName().equals(Statements.PARAM.tag) 
					&& ((DataNode) n).getValue().equals(varname)).size() > 1)
				throw exception(sdt, PARAMETER_REDECLARED, varname);		
		}
		
		if (varname.isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_VARIABLE, sdt.getName());
		if (! VariableStatement.isVarName(varname))
			throw exception(sdt, VARIABLE_NAME_INVALID, varname);
		
		checkParentStatements(sdt, Arrays.asList()); // no parent statements allowed
		checkLeafStatements(sdt, Arrays.asList(Statements.SELECT)); // only select is allowed
		DataNode select = getStatement(sdt, Statements.SELECT, true);

		return isParam 
			? new ParamStatement(varname, xpathFromNode(select))
			: new VariableStatement(varname, xpathFromNode(select));
	}


	/**
	 * This method parses an SDA node representing an SDT foreach statement, and
	 * returns a ForEachStatement. A parent node with child statements and an XPath
	 * expression for a value is expected. Expected is a parent node with a compound
	 * statement and an XPath expression as the value.
	 */
	private static ForEachStatement parseForEach(final DataNode sdt) throws SDTParseException {

		if (sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_XPATH, sdt.getName());

		ForEachStatement statement = new ForEachStatement(xpathFromNode(sdt));
		for (Node node : sdt.nodes()) // parse and add child statements
			statement.add(parseStatement((DataNode) node));

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT if statement, and returns
	 * a IfStatement. Expected is a parent node with a compound statement and an
	 * XPath expression as the value.
	 */
	private static IfStatement parseIf(final DataNode sdt) throws SDTParseException {

		if (sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_XPATH, sdt.getName());

		IfStatement statement = new IfStatement(xpathFromNode(sdt));
		for (Node node : sdt.nodes()) // parse and add child statements
			statement.add(parseStatement((DataNode) node));

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT choose statement, and
	 * returns a ChooseStatement. Expected is a parent node with at least one when
	 * statement and an optional otherwise statement.
	 */
	private static ChooseStatement parseChoose(final DataNode sdt) throws SDTParseException {

		if (! sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NO_VALUE, sdt.getName());
		
		if (! sdt.isParent()) // at least one "when" statement is expected
			throw exception(sdt, STATEMENT_EXPECTED_IN, "when", sdt.getName());

		checkParentStatements(sdt, Arrays.asList(Statements.WHEN, Statements.OTHERWISE));

		ChooseStatement statement = null;
		int i = 0, last = sdt.nodes().size();
		for (Node node : sdt.nodes()) {

			++i;
			Statement substat = parseStatement((DataNode) node);

			if (i == 1) {
				if (! (substat instanceof WhenStatement))
					throw exception(node, STATEMENT_EXPECTED, Statements.WHEN.tag);
				statement = new ChooseStatement((WhenStatement) substat);
				continue;
			}

			if (substat instanceof OtherwiseStatement && i < last)
				throw exception(node, STATEMENT_MISPLACED, Statements.OTHERWISE.tag);

			statement.add(parseStatement((DataNode) node));
		}

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT when statement, and
	 * returns a WhenStatement. Expected is a parent node with a compound statement
	 * and an XPath expression as the value.
	 */
	private static WhenStatement parseWhen(final DataNode sdt) throws SDTParseException {

		if (sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_XPATH, sdt.getName());

		WhenStatement statement = new WhenStatement(xpathFromNode(sdt));
		for (Node node : sdt.nodes()) // parse and add child statements
			statement.add(parseStatement((DataNode) node));

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT when statement, and
	 * returns a OtherwiseStatement. Expected is a parent node with a compound
	 * statement and no value.
	 */
	private static OtherwiseStatement parseOtherwise(final DataNode sdt) throws SDTParseException {

		if (!sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NO_VALUE, sdt.getName());

		OtherwiseStatement statement = new OtherwiseStatement();
		for (Node node : sdt.nodes()) // parse and add child statements
			statement.add(parseStatement((DataNode) node));

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT node statement, and
	 * returns a NodeStatement. Expected is a parent node with a compound statement
	 * and a non-empty node name as the value. The compound statement may have an
	 * VALUE statement with an XPath expression.
	 */
	private static Statement parseNode(final DataNode sdt) throws SDTParseException {

		final String nodename = sdt.getValue();

		if (nodename.isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NODENAME, sdt.getName());
		if (! SDA.isName(nodename))
			throw exception(sdt, NODE_NAME_INVALID, nodename);
		
		checkLeafStatements(sdt, Arrays.asList(Statements.VALUE)); // only value is allowed
		final DataNode nodevalue = getStatement(sdt, Statements.VALUE, false);
			
		Statement stat = (nodevalue == null) ? new NodeStatement(nodename)
			: new NodeStatement(nodename, xpathFromNode(nodevalue));

		for (Node node : sdt.find(n -> ! n.isLeaf()))
			stat.add(parseStatement((DataNode) node)); // parse and add child statements

		return stat;
	}

	
	/**
	 * This method parses an SDA node representing an SDT copy statement, and
	 * returns a CopyStatement. Expected is a parent node with no value, and a
	 * single mandatory SELECT statement with an XPath expression.
	 */
	private static CopyStatement parseCopy(final DataNode sdt) throws SDTParseException {

		if (!sdt.getValue().isEmpty())
			throw exception(sdt, STATEMENT_REQUIRES_NO_VALUE, sdt.getName());

		checkParentStatements(sdt, Arrays.asList()); // no parent statements allowed
		checkLeafStatements(sdt, Arrays.asList(Statements.SELECT)); // only select is allowed
		DataNode select = getStatement(sdt, Statements.SELECT, true);

		return new CopyStatement(xpathFromNode(select));
	}


	/* HELPER METHODS */


	/**
	 * This helper method creates an SDAXPath from an expression that is contained
	 * in the value of the supplied Node.
	 *
	 * @param node the Node to create the XPath from
	 * @throws SDTParseException if the XPath expression is invalid
	 */
	private static SDAXPath xpathFromNode(final DataNode node) throws SDTParseException {

		if (node.getValue().isEmpty())
			throw exception(node, STATEMENT_REQUIRES_XPATH, node.getName());
		
		
		SDAXPath xpath;
		try {
			xpath = new SDAXPath(node.getValue());
		} catch (Exception e) {
			throw new SDTParseException(node, e.getMessage(), e);
		}
		return xpath;
	}


	/**
	 * This helper method iterates all non-leaf nodes in a statement node, and checks
	 * for nodes that do not represent an existing statement, or are not allowed in
	 * this particular statement node. In either case an exception will be thrown.
	 *
	 * @param sdt     a node representing an SDT statement, not null
	 * @param allowed a list of statements, null if none are allowed
	 * @throws SDTParseException if unknown or forbidden statements are found
	 */
	private static void checkParentStatements(final DataNode sdt, List<Statements> allowed) throws SDTParseException {

		checkStatements(sdt, n -> ! n.isLeaf(), allowed);
	}


	/**
	 * This helper method iterates all leaf nodes in a statement node, and checks
	 * for nodes that do not represent an existing statement, or are not allowed in
	 * this particular statement node. In either case an exception will be thrown.
	 *
	 * @param sdt     a node representing an SDT statement, not null
	 * @param allowed a list of statements, null if none are allowed
	 * @throws SDTParseException if unknown or forbidden statements are found
	 */
	private static void checkLeafStatements(final DataNode sdt, List<Statements> allowed) throws SDTParseException {

		checkStatements(sdt, n -> n.isLeaf(), allowed);
	}

	
	/* Private helper for checkLeafStatements and checkParentStatements. */
	private static void checkStatements(final DataNode sdt, Predicate<Node> predicate, List<Statements> allowed) throws SDTParseException {

		for (Node node : sdt.find(predicate)) {

			Statements stat = Statements.get(node.getName());
			if (stat == null) // no statement with that name
				throw exception(node, STATEMENT_UNKNOWN, node.getName());
			if (allowed == null || ! allowed.contains(stat))
				throw exception(node, STATEMENT_NOT_ALLOWED, node.getName());
		}
	}


	/**
	 * This helper method gets a specific statement node from a parent statement node.
	 *
	 * @param sdt       is a statement node, with child nodes.
	 * @param stat is the statement we want to retrieve.
	 * @param required  controls the behavior:<br>
	 *                  when <em>true</em>, the statement is required and an
	 *                  exception is thrown if absent.<br>
	 *                  when <em>false</em>, the statement is optional and
	 *                  <code>null</code> is returned if absent.<br>
	 *                  when <em>null</em>, the statement is forbidden and an
	 *                  exception is thrown if present.<br>
	 *                  An exception is also thrown if more than one statement is
	 *                  found or if it has an empty value.
	 *
	 * @return a data node, may be null
	 * @throws SDTParseException
	 */
	private static DataNode getStatement(final DataNode sdt, Statements stat, Boolean required) throws SDTParseException {

		List<DataNode> alist = sdt.find(n -> n.isLeaf() && n.getName().equals(stat.tag));
		int size = alist.size();
		
		if (size == 0) {
			if (required == null || !required)
				return null;
			throw exception(sdt, STATEMENT_EXPECTED_IN, stat.tag, sdt.getName());
		}
		if (required == null)
			throw exception(sdt, STATEMENT_NOT_ALLOWED, stat.tag);

		DataNode node = alist.get(0);
		if (node.getValue().isEmpty())
			throw exception(node, STATEMENT_REQUIRES_XPATH, stat.tag);
		if (size > 1)
			throw exception(node, STATEMENT_NOT_SINGULAR, stat.tag);

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

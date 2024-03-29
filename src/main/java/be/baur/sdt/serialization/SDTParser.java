package be.baur.sdt.serialization;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import be.baur.sda.Node;
import be.baur.sda.SDA;
import be.baur.sdt.Transform;
import be.baur.sdt.statements.ChooseStatement;
import be.baur.sdt.statements.CopyStatement;
import be.baur.sdt.statements.ForEachStatement;
import be.baur.sdt.statements.IfStatement;
import be.baur.sdt.statements.NodeStatement;
import be.baur.sdt.statements.NodeValueStatement;
import be.baur.sdt.statements.OtherwiseStatement;
import be.baur.sdt.statements.ParamStatement;
import be.baur.sdt.statements.PrintStatement;
import be.baur.sdt.statements.Statement;
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
 * The internal representation of the Transform would be:
 *
 * <pre>
 * Transform {
 *    NodeStatement("greeting") {
 *       NodeStatement("message","'hello world'")
 *   }
 * }
 * </pre>
 *
 * See also {@link Transform}.
 */
public final class SDTParser implements Parser {

	private static final String STATEMENT_EXPECTED = "'%s' statement expected";
	private static final String STATEMENT_INCOMPLETE = "statement '%s' is incomplete";
	private static final String STATEMENT_MISPLACED = "statement '%s' is misplaced";
	private static final String STATEMENT_NOT_ALLOWED = "statement '%s' is not allowed here";
	private static final String STATEMENT_REQUIRES_XPATH = "statement '%s' requires an XPath expression";
	private static final String STATEMENT_REQUIRES_VARIABLE = "statement '%s' requires a variable name";
	private static final String STATEMENT_REQUIRES_NODENAME = "statement '%s' requires a node name";
	private static final String STATEMENT_REQUIRES_NO_VALUE = "statement '%s' requires no value";
	private static final String STATEMENT_UNKNOWN = "statement '%s' is unknown";

	private static final String ATTRIBUTE_EXPECTED = "'%s' attribute expected";
	private static final String ATTRIBUTE_NOT_ALLOWED = "attribute '%s' is not allowed here";
	private static final String ATTRIBUTE_NOT_SINGULAR = "attribute '%s' can occur only once";
	private static final String ATTRIBUTE_REQUIRES_VALUE = "attribute '%s' requires a value";
	private static final String ATTRIBUTE_UNKNOWN = "attribute '%s' is unknown";
	
	private static final String NODE_NAME_INVALID = "node name '%s' is invalid";
	private static final String VARIABLE_NAME_INVALID = "variable name '%s' is invalid";
	private static final String PARAMETER_REDECLARED = "parameter '%s' cannot be redeclared";
	
	@Override
	public Transform parse(Reader input) throws IOException, ParseException, java.text.ParseException {

		Node sdt = SDA.parser().parse(input);
		return parse(sdt);
	}


	/**
	 * Creates a Transform from an SDA node representing a transformation recipe in
	 * SDT notation.
	 *
	 * @param sdt a Node with a Transform definition
	 * @return a Transform
	 * @throws ParseException if a parse exception occurs
	 */
	public static Transform parse(Node sdt) throws ParseException {
		/*
		 * A valid transform has no value, and no attributes.
		 */
		if (!sdt.getName().equals(Transform.TAG))
			throw new ParseException(sdt, String.format(STATEMENT_EXPECTED, Transform.TAG));

		if (!sdt.getValue().isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_NO_VALUE, sdt.getName()));

		if (sdt.isLeaf()) // a transform should have a compound statement, even if empty
			throw new ParseException(sdt, String.format(STATEMENT_INCOMPLETE, Transform.TAG));

		checkAttributes(sdt, null); // no attributes allowed in the transform root

		Transform transform = new Transform();
		for (Node node : sdt.find(n -> ! n.isLeaf()))
			transform.add(parseStatement(node)); // parse and add child statements

		return transform;
	}


	/**
	 * This method parses an SDA node representing an SDT statement, and returns a
	 * Statement which itself may contain other Statements.
	 */
	private static Statement parseStatement(Node sdt) throws ParseException {
		/*
		 * Whatever we get must be an existing statement, and contain attributes and/or
		 * other statements. This method is called recursively and must deal with every
		 * possible statement.
		 */
		final String name = sdt.getName();
		Statements statement = Statements.get(name);

		if (statement == null) // this statement is unknown
			throw new ParseException(sdt, String.format(STATEMENT_UNKNOWN, name));

		Statements parent = Statements.get(sdt.getParent().getName()); // returns null if parent is transform
		if (!statement.isAllowedIn(parent)) // check sub-ordinate statements
			throw new ParseException(sdt, String.format(STATEMENT_NOT_ALLOWED, name));

		if (!sdt.isParent()) // statements must have attributes and/or other statements
			throw new ParseException(sdt, String.format(STATEMENT_INCOMPLETE, name));

		Statement stat;

		switch (statement) {
			case CHOOSE: stat = parseChoose(sdt); break;
			case COPY: stat = parseCopy(sdt); break;
			case FOREACH: stat = parseForEach(sdt); break;
			case IF: stat = parseIf(sdt); break;
			case NODE: stat = parseNode(sdt); break;
			case OTHERWISE: stat = parseOtherwise(sdt); break;
			case PARAM: stat = parseVariableOrParam(sdt); break;
			case PRINT: stat = parsePrintOrPrintLn(sdt, false); break;
			case PRINTLN: stat = parsePrintOrPrintLn(sdt, true); break;
			case VARIABLE: stat = parseVariableOrParam(sdt); break;
			case WHEN: stat = parseWhen(sdt); break;
			default: // we should never get here, unless we forgot to implement a statement
				throw new RuntimeException("SDT statement '" + name + "' not implemented!");
		}

		return stat;
	}


	/**
	 * This method parses an SDA node representing an SDT print or println
	 * statement, and returns a PrintStatement.
	 */
	private static PrintStatement parsePrintOrPrintLn(Node sdt, boolean addEOL) throws ParseException {
		/*
		 * A valid print(nl) statement has no value, a single VALUE attribute containing
		 * an XPath expression, and nothing else.
		 */
		if (!sdt.getValue().isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_NO_VALUE, sdt.getName()));

		checkStatements(sdt, null); // no sub-statements allowed
		checkAttributes(sdt, Arrays.asList(Attribute.VALUE));
		Node value = getAttribute(sdt, Attribute.VALUE, true);

		return new PrintStatement(xpathFromNode(value), addEOL);
	}


	/**
	 * This method parses an SDA node representing an SDT variable or param
	 * statement, and returns a VariableStatement (or ParamStatement).
	 */
	private static VariableStatement parseVariableOrParam(Node sdt) throws ParseException {
		/*
		 * A valid variable/param statement has a non-empty value with a variable name,
		 * a single SELECT attribute containing an XPath expression, and no other
		 * statements or attributes. Also, parameters must be declared globally (in the
		 * transform node) and not more than once.
		 */
		String varname = sdt.getValue();
		
		boolean isParam = sdt.getName().equals(Statements.PARAM.tag);	
		if ( isParam ) {
			if (! sdt.getParent().getName().equals(Transform.TAG)) // parent cannot be null
				throw new ParseException(sdt, String.format(STATEMENT_NOT_ALLOWED, sdt.getName()));	
			if (sdt.getParent().find(n -> n.getName().equals(Statements.PARAM.tag) && n.getValue().equals(varname)).size() > 1)
				throw new ParseException(sdt, String.format(PARAMETER_REDECLARED, varname));		
		}
		
		if (varname.isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_VARIABLE, sdt.getName()));
		if (! VariableStatement.isVarName(varname))
			throw new ParseException(sdt, String.format(VARIABLE_NAME_INVALID, varname));
		
		checkStatements(sdt, null); // no sub-statements allowed
		checkAttributes(sdt, Arrays.asList(Attribute.SELECT));
		Node select = getAttribute(sdt, Attribute.SELECT, true);

		return isParam 
			? new ParamStatement(varname, xpathFromNode(select))
			: new VariableStatement(varname, xpathFromNode(select));
	}


	/**
	 * This method parses an SDA node representing an SDT foreach statement, and
	 * returns a ForEachStatement.
	 */
	private static ForEachStatement parseForEach(Node sdt) throws ParseException {
		/*
		 * A valid foreach statement has a non-empty value with an XPath expression and
		 * contains child statements, but no attributes.
		 */
		if (sdt.getValue().isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_XPATH, sdt.getName()));

		checkAttributes(sdt, null);

		ForEachStatement statement = new ForEachStatement(xpathFromNode(sdt));
		for (Node node : sdt.nodes()) // parse and add child statements
			statement.add(parseStatement(node));

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT if statement, and returns
	 * a IfStatement.
	 */
	private static IfStatement parseIf(Node sdt) throws ParseException {
		/*
		 * A valid if statement has a non-empty value with an XPath expression and
		 * contains child statements, but no attributes.
		 */
		if (sdt.getValue().isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_XPATH, sdt.getName()));

		checkAttributes(sdt, null);

		IfStatement statement = new IfStatement(xpathFromNode(sdt));
		for (Node node : sdt.nodes()) // parse and add child statements
			statement.add(parseStatement(node));

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT choose statement, and
	 * returns a ChooseStatement.
	 */
	private static ChooseStatement parseChoose(Node sdt) throws ParseException {
		/*
		 * A valid choose statement has no value, contains at least one when statement,
		 * an optional otherwise statement, and no attributes or other statements.
		 */
		if (!sdt.getValue().isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_NO_VALUE, sdt.getName()));

		checkStatements(sdt, Arrays.asList(Statements.WHEN, Statements.OTHERWISE));
		checkAttributes(sdt, null);

		ChooseStatement statement = null;
		int i = 0, last = sdt.nodes().size();
		for (Node node : sdt.nodes()) {

			++i;
			Statement substat = parseStatement(node);

			if (i == 1) {
				if (!(substat instanceof WhenStatement))
					throw new ParseException(node, String.format(STATEMENT_EXPECTED, Statements.WHEN.tag));
				statement = new ChooseStatement((WhenStatement) substat);
				continue;
			}

			if (substat instanceof OtherwiseStatement && i < last)
				throw new ParseException(node, String.format(STATEMENT_MISPLACED, Statements.OTHERWISE.tag));

			statement.add(parseStatement(node));
		}

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT when statement, and
	 * returns a WhenStatement.
	 */
	private static WhenStatement parseWhen(Node sdt) throws ParseException {
		/*
		 * A valid when statement has a non-empty value with an XPath expression and
		 * contains child statements, but no attributes.
		 */
		if (sdt.getValue().isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_XPATH, sdt.getName()));

		checkAttributes(sdt, null);

		WhenStatement statement = new WhenStatement(xpathFromNode(sdt));
		for (Node node : sdt.nodes()) // parse and add child statements
			statement.add(parseStatement(node));

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT when statement, and
	 * returns a OtherwiseStatement.
	 */
	private static OtherwiseStatement parseOtherwise(Node sdt) throws ParseException {
		/*
		 * A valid otherwise statement has no value and contains child statements, but
		 * no attributes.
		 */
		if (!sdt.getValue().isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_NO_VALUE, sdt.getName()));

		checkAttributes(sdt, null);

		OtherwiseStatement statement = new OtherwiseStatement();
		for (Node node : sdt.nodes()) // parse and add child statements
			statement.add(parseStatement(node));

		return statement;
	}


	/**
	 * This method parses an SDA node representing an SDT node statement, and
	 * returns a NodeStatement.
	 */
	private static Statement parseNode(Node sdt) throws ParseException {
		/*
		 * A valid node statement has a non-empty value with a node name, a single VALUE
		 * attribute containing an XPath expression, and contains child statements but
		 * no other attributes.
		 */
		final String nodename = sdt.getValue();

		if (nodename.isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_NODENAME, sdt.getName()));
		if (! SDA.isName(nodename))
			throw new ParseException(sdt, String.format(NODE_NAME_INVALID, nodename));
		
		checkAttributes(sdt, Arrays.asList(Attribute.VALUE));
		final Node value = getAttribute(sdt, Attribute.VALUE, false);
			
		Statement stat = (value == null) 
			? new NodeStatement(nodename) 
			: new NodeValueStatement(nodename, xpathFromNode(value));

		for (Node node : sdt.find(n -> ! n.isLeaf()))
			stat.add(parseStatement(node)); // parse and add child statements

		return stat;
	}

	
	/**
	 * This method parses an SDA node representing an SDT copy statement, and
	 * returns a CopyStatement.
	 */
	private static CopyStatement parseCopy(Node sdt) throws ParseException {
		/*
		 * A valid copy statement has no value, a single SELECT attribute containing
		 * an XPath expression, and nothing else.
		 */
		if (!sdt.getValue().isEmpty())
			throw new ParseException(sdt, String.format(STATEMENT_REQUIRES_NO_VALUE, sdt.getName()));

		checkStatements(sdt, null); // no sub-statements allowed
		checkAttributes(sdt, Arrays.asList(Attribute.SELECT));
		Node select = getAttribute(sdt, Attribute.SELECT, true);

		return new CopyStatement(xpathFromNode(select));
	}


	/* HELPER METHODS */

	/**
	 * This helper method creates an SDAXPath from an expression that is contained
	 * in the value of the supplied Node.
	 *
	 * @param node the Node to create the XPath from
	 * @throws ParseException if the XPath expression is invalid
	 */
	private static SDAXPath xpathFromNode(Node node) throws ParseException {

		SDAXPath xpath;
		try {
			xpath = new SDAXPath(node.getValue());
		} catch (Exception e) {
			throw new ParseException(node, e);
		}
		return xpath;
	}

	/**
	 * This helper method iterates all non-leaf nodes in a statement node, and
	 * checks for nodes that do not represent an existing statement, or are not
	 * allowed in this particular statement. In either case an exception will be
	 * thrown.
	 *
	 * @param sdt     a Node representing an SDT statement, not null
	 * @param allowed List of Statements, null if none are allowed
	 * @throws ParseException if unknown or forbidden Statements are found
	 */
	private static void checkStatements(Node sdt, List<Statements> allowed) throws ParseException {

		for (Node node : sdt.find(n -> ! n.isLeaf())) {

			Statements statement = Statements.get(node.getName());
			if (statement == null) // all statements must have a known name tag
				throw new ParseException(node, String.format(STATEMENT_UNKNOWN, node.getName()));
			if (allowed == null || !allowed.contains(statement))
				throw new ParseException(node, String.format(STATEMENT_NOT_ALLOWED, node.getName()));
		}
	}

	/**
	 * This helper method iterates all leaf nodes in a statement node, and checks
	 * for nodes that do not represent an existing attribute, or are not allowed in
	 * this particular statement. In either case an exception will be thrown.
	 *
	 * @param sdt     a Node representing an SDT statement, not null
	 * @param allowed List of Attributes, null if none are allowed
	 * @throws ParseException if unknown or forbidden Attributes are found
	 */
	private static void checkAttributes(Node sdt, List<Attribute> allowed) throws ParseException {

		for (Node node : sdt.find(n -> n.isLeaf())) {

			Attribute attribute = Attribute.get(node.getName());
			if (attribute == null) // all attributes must have a known name tag
				throw new ParseException(node, String.format(ATTRIBUTE_UNKNOWN, node.getName()));
			if (allowed == null || !allowed.contains(attribute))
				throw new ParseException(node, String.format(ATTRIBUTE_NOT_ALLOWED, node.getName()));
		}
	}

	/**
	 * This helper method gets a specific attribute from a statement node.
	 *
	 * @param sdt       is a statement node, with child nodes.
	 * @param attribute is the attribute we want to retrieve.
	 * @param required  controls the behavior:<br>
	 *                  when <em>true</em>, the attribute is required and an
	 *                  exception is thrown if absent.<br>
	 *                  when <em>false</em>, the attribute is optional and
	 *                  <code>null</code> is returned if absent.<br>
	 *                  when <em>null</em>, the attribute is forbidden and an
	 *                  exception is thrown if present.<br>
	 *                  An exception is also thrown if more than one attribute is
	 *                  found or if it has an empty value.
	 *
	 * @return a Node, may be null
	 * @throws ParseException
	 */
	private static Node getAttribute(Node sdt, Attribute attribute, Boolean required) throws ParseException {

		List<Node> alist = sdt.find(n -> n.isLeaf() && n.getName().equals(attribute.tag));
		int size = alist.size();
		
		if (size == 0) {
			if (required == null || !required)
				return null;
			throw new ParseException(sdt, String.format(ATTRIBUTE_EXPECTED, attribute.tag));
		}
		if (required == null)
			throw new ParseException(sdt, String.format(ATTRIBUTE_NOT_ALLOWED, attribute.tag));

		Node node = alist.get(0);
		if (node.getValue().isEmpty())
			throw new ParseException(node, String.format(ATTRIBUTE_REQUIRES_VALUE, attribute.tag));
		if (size > 1)
			throw new ParseException(node, String.format(ATTRIBUTE_NOT_SINGULAR, attribute.tag));

		return node;
	}

}

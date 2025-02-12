# Release Notes

## [1.4.0] - 2025-02-12

This release impacts the way XPath expressions are written (without referencing the root node) 
and how variables are (re)assigned with regards to scope. As a result, existing transformations 
will need to be rewritten. Also, a transform no longer adds a default 'output' node.

- `Removed` StatementContext.hasVariable().
- `Changed` VariableStatement.isVarName() to SDT.isVariableName().
- `Changed` StatementContext.get/setContextNode() to get/setXPathContext().
- Added function parse-sda() and document-node().
- Added StatementContext.getVariableContext().
- Closed issue #16 (restrict user defined variable names).
- Closed issue #17 (document() adds a fake document node).
- Closed issue #18 (update Javadoc of Navigator).
- Closed issue #19 (review scoping of variables).
- Closed issue #20 (remove default output node).

## Compatibility

- Requires Java 8, sda-core 2.3.0 and Jaxen 2.0.0.

## Previous releases

### [1.3.1] - 2024-08-11 (requires SDA v2.2.1)

This release fixes several issues and adds grouping functionality.

- SDT parser improvements.
- Added foreach group iteration.
- Added functions tokenize() and render-sda().
- Fixed issue in sort comparator and statement context.
- Closed issue #11 (impossible to create a vacant parent).
- Closed issue #12 (possible to overwrite a parameter).

### [1.3.0] - 2024-06-26 (requires SDA v2.2.0)

This release has a few changes with respect to the syntax of some SDT statements,
and the parser internals and validation error messages have been improved. This 
release also introduces the sort statement and updates the documentation.

- `Removed` the sdt.serialization.Attribute class.
- `Renamed` sdt.statements package to sdt.transform.
- `Renamed` sdt.serialization package to sdt.parser.
- `Renamed` sdt.serialization.Statements to sdt.parser.Keyword.
- `Changed` print(ln) and copy into "leaf" statements.
- Added compare-number()/compare-string() functions. 
- Added sort statement (closes issue #8).
- Added specification document.
- Closed issue #3 (move copy() to SDA core).
- Closed issue #7 (rendering issue compound statement).
- Closed issue #9 (issue with node statement).
- Closed issue #10 (copy should be a leaf statement).
- Closed issue #13 (rename Statements and sdt.statements package).

### [1.2.0] - 2024-05-12 (requires SDA v2.2.0)

This is a compatibility release for sda-core 2.2.x but has quite a few 
changes with regards to naming and packaging.

- `Removed` NodeValueStatement class.
- `Removed` SDTException and Parser interface.
- `Removed` NodeProcessingException (moved to sda-core).
- `Changed` Statement.execute() is no longer public.
- `Changed` Convenience method SDT.parser() to SDT.parse().
- `Changed` NodeStatement.getName to NodeStatement.getNodeName.
- `Changed` NodeValueStatement.getName to NodeValueStatement.getNodeName.
- `Renamed` ParseException to SDTParseException.
- Closed issue #4.

### [1.1.0] - 2023-10-04 (requires SDA v2.1.0)

This is a compatibility release for sda-core 2.1.x with minor changes.

- `Changed` parameters to be global variables only.
- Added VariableStatement.isVarName().

### [1.0.0] - 2023-07-04 (requires v2.0.0)

More than anything else, this release is a proof of concept.

- Supports basic transformation of SDA content using XPath.
# Release Notes

## [1.3.0] - In development

This release has a few changes with respect to the syntax of some SDT statements,
and the parser internals and validation error messages have been improved.

- `Removed` the sdt.serialization.Attribute class.
- `Changed` the print(ln) statement into a "leaf" statement.
- Added compare-number() function. 
- Added (multi-level) sort statement.
- Closed issue #3 (move copy() to SDA core).
- Closed issue #7 (rendering issue compound statement).

## Compatibility

- Requires at least Java 8, sda-core 2.2.0 and Jaxen 2.0.0.

## Previous releases

### [1.2.0] - 2024-05-12

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

### [1.1.0] - 2023-10-04

This is a compatibility release for sda-core 2.1.x with minor changes.

- `Changed` parameters to be global variables only.
- Added VariableStatement.isVarName().

### [1.0.0] - 2023-07-04 (requires at least SDA v2.0.0)

More than anything else, this release is a proof of concept.

- Supports basic transformation of SDA content using XPath.
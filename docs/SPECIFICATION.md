# SDT Specification

- [Statements](#statements)
	- [choose](#choose)
	- [copy](#copy)
	- [foreach](#foreach)
	- [if](#if)
	- [node](#node)
	- [param](#param)
	- [print(ln)](#println)
	- [sort](#sort)
	- [transform](#transform)
	- [variable](#variable)
- [Functions](#functions)
	- [Standard functions](#standard-functions)
		- [Node-Set functions](#node-set-functions)
		- [String functions](#string-functions)	
		- [Boolean functions](#boolean-functions)
		- [Number functions](#number-functions)
		- [Other functions](#other-functions)
	- [Extensions](#extensions)
		- [SDT string functions](#sdt-string-functions)	
		- [SDT number functions](#sdt-number-functions)

## Statements

Following is an overview of all SDT statements and their syntax, as well as a summary of the functionality they provide. Most statements are named and modeled after their XSLT counterparts.


### choose

<pre>
	choose {
	  when "<i>expression</i>" {
		<i>compound statement</i>
	  }
	  otherwise {
		<i>compound statement</i>
	  }
	}
</pre>

A `choose` conditionally executes a compound statement, supporting multiple conditions. It has at least one `when` clause and an optional `otherwise`. The first when-statement with an expression that evaluates to true is executed, or if none does, the otherwise-statement is executed - if one is present.


### copy

<pre>
	copy { select "<i>expression</i>" }
</pre>

The `copy` statement evaluates an expression and creates a deep copy of the selected node(s).


### foreach

<pre>
	foreach "<i>expression</i>" {
		<i>optional sort statement(s)</i>
		<i>other statements</i>
	}
</pre>

The `foreach` statement evaluates an expression, iterates the resulting node set and executes a compound statement on each iteration. If present, one or more `sort` statements are applied to order the selected node-set prior to iteration.


### if

<pre>
	if "<i>expression</i>" {
		<i>compound statement</i>
	}
</pre>

An `if` statement evaluates an expression and executes a compound statement if the boolean result is true.


### node

<pre>
	node "<i>name</i>" { 
		value "<i>expression</i>" 
	}
</pre>

A `node` statement creates a new node with the specified name and an optional `value` from the string evaluation of an expression. Any number of child nodes can be created by the compound statement.


### param

<pre>
	param "<i>name</i>" { 
		select "<i>expression</i>" 
	}
</pre>

The `param` statement evaluates an expression and assigns the result to a variable. The resulting value is considered a default that can be overwritten by the transformation context - in other words - a parameter. Unlike regular variables, parameters can be declared in the context of a `transform` only, and are not mutable during execution.


### print(ln)

<pre>
	print "<i>expression</i>" | println "<i>expression</i>"
</pre>

A `print` or `println` statement evaluates an expression and writes the result to the output stream with or without a line separator.


### sort

<pre>
	sort "<i>expression</i>" { 
		[ reverse "<i>expression</i>" comparator "<i>expression</i>" ]
	}
</pre>

The `sort` statement can only occur in the context of a `foreach` loop. It evaluates an expression and sorts the iterated set using the string value of the selected node as a sorting key. The order is controlled with an optional `reverse` expression that is evaluated to a boolean (default is false). An optional `comparator` expression determines how objects are compared (default is lexicographically).


### transform

<pre>
	transform {
		<i>compound statement</i>
	}
</pre>

A `transform` is a recipe for mapping and transformation of node content.


### variable

<pre>
	variable "<i>name</i>" { 
		select "<i>expression</i>" 
	}
</pre>

The `variable` statement evaluates an expression and assigns the result to a named variable. Unlike parameters, variables cannot be supplied by the transformation context, and are mutable during transform execution.


## Functions

Following are all functions availabe to XPath expressions in SDT. Obviously, SDA is not XML and functions that assume an XML context or concept may not work as expected, or not work at all on SDA nodes. This will be indicated.


### Standard functions

Functions without namespace-prefix are native Jaxen implementations of the XPath (1.0) specification. Documentation for these functions is readily available on the net. 

#### Node-Set functions

| Function						| Description |
|-------------------------------|-------------|
| **count** ( node-set )		| Returns the number of nodes in the node-set.| 
| **id** ( object )				| *Not supported for SDA nodes; may behave unexpectedly.*| 
| **last** ( )					| Returns the position of the last node in the context list.<br>*Supported in predicates - in an iteration, use $sdt:last instead.*| 
| **local-name** ( node-set )	| Returns the local-name for the first node in the node-set.| 
| **local-name** ( )			| Returns the local-name for the context node.| 
| **name** ( node-set )			| Returns the name for the first node in the node-set.| 
| **name** ( )					| Returns the name for the context node.|
| **namespace-uri** ( node-set )| *Not supported for SDA nodes; may behave unexpectedly.*| 
| **namespace-uri** ( )			| *Not supported for SDA nodes; may behave unexpectedly.*| 
| **position** ( )				| Returns the position of the current context node.<br>*Supported in predicates - in an iteration, use $sdt:position instead.*| 


#### String functions

| Function									| Description |
|-------------------------------------------|-------------|
| **concat** ( string, string+ )			| Returns the concatenation of its arguments.| 
| **contains** ( string, string )			| Returns true if the first argument string contains the second argument string.| 
| **ends-with** ( string, string )			| Returns true if the first argument string ends with the second argument string.|
| **lower-case** ( string )					| Returns the lower case representation of the argument string.|
| **lower-case** ( string, string )			| Returns the lower case representation of the argument, in the locale specified by the second argument.|
| **normalize-space** ( string )			| Returns a white-space normalized string specified by the argument.|
| **normalize-space** ( )					| Returns a white-space normalized string specified by the context-node.|
| **starts-with** ( string, string )		| Returns true if the first argument string starts with the second argument string.|
| **string** ( object )						| Returns the string representation of the object argument.| 
| **string** ( )							| Returns a string value representation of the context node.| 
| **string-length** ( string )				| Returns the length of the string specified by the argument.|
| **string-length** ( )						| Returns the length of the string specified by the context node.|
| **substring** ( string, number )			| Returns the substring of the first argument from the position specified by the second argument.| 
| **substring** ( string, number, number )	| Returns the substring of the first argument starting at the position specified by the second argument and the length specified by the third argument.| 
| **substring-after** ( string, string )	| Returns the substring of the first argument string that comes after the first occurrence of the second argument.| 
| **substring-before** ( string, string )	| Returns the substring of the first argument string that comes before the first occurrence of the second argument.|
| **upper-case** ( string )					| Returns the upper case representation of the argument string.|
| **upper-case** ( string, string )			| Returns the upper case representation of the argument, in the locale specified by the second argument.|
| **translate** ( string, string, string )	| Replaces characters in the string specified by the second argument with characters specified by the third argument.|


#### Boolean functions

| Function				| Description |
|-----------------------|-------------|
| **boolean** ( object )| Returns the boolean representation of the object argument.|
| **false** ( )			| Returns a boolean with the value of false.|
| **lang** ( string )	| *Not supported for SDA nodes; may behave unexpectedly.*|
| **not** ( boolean )	| Returns a boolean with the opposite value of its argument.|
| **true** ( )			| Returns a boolean with the value of true.|


#### Number functions

| Function					| Description |
|---------------------------|-------------|
| **ceiling** ( number )	| Returns the smallest integer value not less than the argument.|
| **floor** ( number )		| Returns the largest integer value not greater than the argument.|
| **number** ( object )		| Returns the number representation of the object argument.|
| **number** ( )			| Returns the number representation of the context node.|
| **round** ( number )		| Returns the integer value closest to the argument.|
| **sum** ( node-set )		| Returns the sum of all nodes in the node-set.|


#### Other functions

| Function					| Description |
|---------------------------|-------------|
| **document** ( URI )		| Loads a document from the given URI.|
| **evaluate** ( string )	| Evaluates the argument as an XPath expression.|


### Extensions

Functions with a namespace-prefix are extensions supplied by the SDT library, and are either SDT specific, or implementations of XPath functions that are not (yet) provided by Jaxen. These functions are explained in more detail.


#### SDT string functions

| Function									| Description |
|-------------------------------------------|-------------|
| **fn : string-join** ( node-set )			| Returns a string created by concatenating the items in the supplied set. If the set is empty, the function returns a zero-length string. |
| **fn : string-join** ( node-set, string )	| Like string-join(node-set), with the second argument as separator string between adjacent items. See also the W3C [recommendation](https://www.w3.org/TR/xpath-functions/#func-string-join).|
| **sdt : left** ( string, number )			| Returns the specified number of characters from the start of the argument string. For example,<br><br> <code>sdt:left("12345",3)</code> returns <code>"123"</code><br><br> If the second argument is not a number or less than 1, an empty string is returned. If it exceeds the string length of the first argument, the entire string is returned.|
| **sdt : right** ( string, number )		| Returns the specified number of characters from the end of the argument string. For example,<br><br> <code>sdt:right("12345",3)</code> returns <code>"345"</code>.<br><br> If the second argument is not a number or less than 1, an empty string is returned. If it exceeds the string length of the first argument, the entire string is returned.|


#### SDT number functions

| Function												| Description |
|-------------------------------------------------------|-------------|
| **sdt : compare-number** ( number, number )			| Compares two objects numerically. This function converts its arguments to numbers and returns -1 if the second argument precedes the first, 1 if it exceeds it, and 0 if the arguments are numerically equal:<br><br> <code>sdt:compare-number(1,3)</code> returns <code>-1.0</code>.<br> <code>sdt:compare-number(3,'3')</code> returns <code>0.0</code>.<br> <code>sdt:compare-number('6','4')</code> returns <code>1.0</code>.<br><br> Objects that are not numbers are considered equal, and greater than all other  numbers:<br><br> <code>sdt:compare-number('a',1)</code> returns <code>1.0</code>.<br> <code>sdt:compare-number('a','b')</code> returns <code>0.0</code>.<br><br> This function can be used as a comparator in a sort statement.|
| **sdt : compare-number** ( number, number, boolean )	| Like compare-number(number,number), but if the third argument evaluates to true, objects that are not numbers are considered smaller than all numbers.|
| **sdt : compare-string** ( string, string )			| Compares two objects locale-sensitive. This function converts its arguments  to strings and returns -1 if the second argument precedes the first, 1 if it  exceeds it, and 0 if the arguments are considered equal in the default  locale:<br><br>  <code>sdt:compare-string('a','A')</code> returns <code>-1.0</code>.<br>  <code>sdt:compare-string(3,'3')</code> returns <code>0.0</code>.<br>  <code>sdt:compare-string('b','A')</code> returns <code>1.0</code>.<br><br> This function can be used as a comparator in a sort statement.|
| **sdt : compare-string** (string, string, string)		| Like compare-string( string, string ) but the third argument specifies the language tag (IETF BCP 47) to obtain  a collation strategy that best fits the tag:<br><br>  <code>sdt:compare-string('Ångström','Zulu','sv')</code> returns  <code>1.0</code> in accordance with Swedish collation rules.|

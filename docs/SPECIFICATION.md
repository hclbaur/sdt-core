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
- [XPath Functions](#xpath-functions)
	- [Node-set functions](#node-set-functions)
	- [String functions](#string-functions)	
	- [Boolean functions](#boolean-functions)
	- [Number functions](#number-functions)
	- [Other functions](#other-functions)
- [SDT Extensions](#sdt-extensions)
	- [compare-number](#compare-number), [compare-string](#compare-string), [current-dateTime](#current-dateTime)
	- [dateTime](#dateTime), [document-node](#document-node)
	- [format-dateTime](#format-dateTime)
	- [left](#left)
	- [millis-to-dateTime](#millis-to-dateTime)
	- [parse-sda](#parse-sda)
	- [render-sda](#render-sda), [right](#right)
	- [string-join](#string-join)
	- [timestamp](#timestamp), [tokenize](#tokenize)


## Statements

Following is an overview of all SDT statements and their syntax, as well as a summary of the functionality they provide. Most statements are named and modeled after their XSLT counterparts.


#### choose

<pre>
	choose {
	  when "<i>expression</i>" {
		<i>[ statement(s) ]</i>
	  }
	  <i>[ when statement(s) ]</i>
	  <i>[ </i>otherwise {
		<i>[ statement(s) ]</i>
	  }<i> }</i>
	}
</pre>

A `choose` conditionally executes a compound statement, supporting multiple conditions. It has at least one `when` clause and an optional `otherwise`. The first when-statement with an expression that evaluates to true is executed, or if none does, the otherwise-statement is executed - if one is present.


#### copy

<pre>
	copy "<i>expression</i>"
</pre>

The `copy` statement evaluates an expression and creates a deep copy of the selected node(s).


#### foreach

<pre>
	foreach "<i>expression</i>" {
		<i>[</i> group "<i>expression</i>" <i>]</i>
		<i>[ sort statement(s) ]</i>
		<i>[ other statement(s) ]</i>
	}
</pre>

The `foreach` statement evaluates an expression, iterates the resulting node set and executes a compound statement on each iteration. If present, one or more `sort` statements are applied to order the selected node-set prior to iteration. During iteration, the following automatic variables are available to the compound statement:

|    			 |    						     |
|----------------|-------------------------------|
| *sdt:position* | the index of the current node |
| *sdt:last*	 | the index of the last node	 |
| *sdt:current*	 | the currently iterated node	 |

A `group` attribute may be used to specify an grouping key expression, causing nodes that share the same key to be grouped together in new node-sets. These groups are then iterated - rather than the nodes in them - and the following automatic variables will be available to the compound statement:

|							 |    							  |
|----------------------------|--------------------------------|
| *sdt:position*			 | the index of the current group |
| *sdt:last*				 | the index of the last group	  |
| *sdt:current-group*		 | the currently iterated group	  |
| *sdt:current-grouping-key* | the key of the current group	  |

Note that if any sort statements are present, sorting is applied before grouping.


#### if

<pre>
	if "<i>expression</i>" {
		<i>[ statement(s) ]</i>
	}
</pre>

An `if` statement evaluates an expression and executes a compound statement if the boolean result is true.


#### node

<pre>
	node "<i>name</i>" { 
		<i>[</i> value "<i>expression</i>" <i>]</i>
		<i>[ statement(s) ]</i>
	}
</pre>

A `node` statement creates a new node with the specified name and an optional `value` from the string evaluation of an expression. Any number of child nodes can be created by the compound statement, which may contain iteration and conditional statements to create repeating or optional nodes.


#### param

<pre>
	param "<i>name</i>" { 
		select "<i>expression</i>" 
	}
</pre>

The `param` statement evaluates an expression and assigns the result to a variable. The resulting value is considered a default that can be overwritten by the transformation context - in other words - a parameter. Unlike regular variables, parameters can be declared in the context of a `transform` only, and are immutable during execution.


#### print(ln)

<pre>
	print "<i>expression</i>" <i>|</i> println "<i>expression</i>"
</pre>

A `print` statement evaluates an expression and writes the string value to the output stream. The `println` statement does the same, and adds a line separator.


#### sort

<pre>
	sort "<i>expression</i>" <i>|</i> 
	sort "<i>expression</i>" { 
		<i>[</i> reverse "<i>expression</i>" <i>]</i>
		<i>[</i> comparator "<i>expression</i>" <i>]</i>
	}
</pre>

The `sort` statement can only occur in the context of a `foreach` loop. It evaluates an expression and sorts the iterated set using the string value of the selected node as a sorting key. The order is controlled with an optional `reverse` expression that is evaluated to a boolean (default is false). 

An optional `comparator` expression determines how objects are compared (default is lexicographically). Comparators are expressions with two question marks as placeholder for the objects to be compared, and must return a negative integer, zero or a positive integer, depending on whether the first object is smaller than, equal to, or greater than the second object.


#### transform

<pre>
	transform {
		<i>[ statement(s) ]</i>
	}
</pre>

A `transform` is a recipe for mapping and transformation of node content.


#### variable

<pre>
	variable "<i>name</i>" { 
		select "<i>expression</i>" 
	}
</pre>

The `variable` statement evaluates an expression and assigns the result to a named variable. Unlike parameters, variables cannot be supplied by the transformation context, and are mutable during transform execution.


## XPath Functions

Functions without namespace-prefix are native Jaxen implementations of the XPath (1.0) specification. Obviously, SDA is not XML and functions that assume an XML context may not work as expected on SDA nodes. This will be indicated.


#### Node-set functions

| 							| 											  |
|---------------------------|---------------------------------------------|
| count ( node-set )		| Returns the number of nodes in the node-set.| 
| id ( object )				| *Unsupported for SDA nodes.*				  | 
| last ( )					| Returns the position of the last node in the context list.<br>*Supported in predicates - in an iteration, use <code>$sdt:last</code> instead.*| 
| local-name ( node-set )	| Returns the local-name for the first node in the node-set.| 
| local-name ( )			| Returns the local-name for the context node.| 
| name ( node-set )			| Returns the name for the first node in the node-set.| 
| name ( )					| Returns the name for the context node.	  |
| namespace-uri ( node-set )| *Unsupported for SDA nodes.*				  | 
| namespace-uri ( )			| *Unsupported for SDA nodes.*				  | 
| position ( )				| Returns the position of the current context node.<br>*Supported in predicates - in an iteration, use <code>$sdt:position</code> instead.*| 


#### String functions

| 										| 											 |
|---------------------------------------|--------------------------------------------|
| concat ( string, string+ )			| Returns the concatenation of its arguments.| 
| contains ( string, string )			| Returns true if the first argument string contains the second argument string.| 
| ends-with ( string, string )			| Returns true if the first argument string ends with the second argument string.|
| lower-case ( string )					| Returns the lower case representation of the argument string.|
| lower-case ( string, string )			| Returns the lower case representation of the argument, in the locale specified by the second argument.|
| normalize-space ( string )			| Returns a white-space normalized string specified by the argument.|
| normalize-space ( )					| Returns a white-space normalized string specified by the context-node.|
| starts-with ( string, string )		| Returns true if the first argument string starts with the second argument string.|
| string ( object )						| Returns the string representation of the object argument.| 
| string ( )							| Returns a string value representation of the context node.| 
| string-length ( string )				| Returns the length of the string specified by the argument.|
| string-length ( )						| Returns the length of the string specified by the context node.|
| substring ( string, number )			| Returns the substring of the first argument from the position specified by the second argument.| 
| substring ( string, number, number )	| Returns the substring of the first argument starting at the position specified by the second argument and the length specified by the third argument.| 
| substring-after ( string, string )	| Returns the substring of the first argument string that comes after the first occurrence of the second argument.| 
| substring-before ( string, string )	| Returns the substring of the first argument string that comes before the first occurrence of the second argument.|
| upper-case ( string )					| Returns the upper case representation of the argument string.|
| upper-case ( string, string )			| Returns the upper case representation of the argument, in the locale specified by the second argument.|
| translate ( string, string, string )	| Replaces characters in the string specified by the second argument with characters specified by the third argument.|


#### Boolean functions

| 					| 															|
|-------------------|-----------------------------------------------------------|
| boolean ( object )| Returns the boolean representation of the object argument.|
| false ( )			| Returns a boolean with the value of false.			    |
| lang ( string )	| *Unsupported for SDA nodes.*						    	|
| not ( boolean )	| Returns a boolean with the opposite value of its argument.|
| true ( )			| Returns a boolean with the value of true.					|


#### Number functions

| 						| 																  |
|-----------------------|-----------------------------------------------------------------|
| ceiling ( number )	| Returns the smallest integer value not less than the argument.  |
| floor ( number )		| Returns the largest integer value not greater than the argument.|
| number ( object )		| Returns the number representation of the object argument.		  |
| number ( )			| Returns the number representation of the context node.		  |
| round ( number )		| Returns the integer value closest to the argument.			  |
| sum ( node-set )		| Returns the sum of all nodes in the node-set.					  |


#### Other functions

| 						| 												|
|-----------------------|-----------------------------------------------|
| document ( URI )		| Loads a document from the given URI.			|
| evaluate ( string )	| Evaluates the argument as an XPath expression.|


## SDT Extensions

Functions with a namespace-prefix are extensions supplied by the SDT library, and are either SDT specific, or implementations of XPath (3.0) functions that are not (yet) provided by Jaxen.


#### compare-number

<code><i>double</i> sdt:compare-number( <i>number</i>, <i>number</i> )</code><br>
<code><i>double</i> sdt:compare-number( <i>number</i>, <i>number</i>, <i>boolean nanFirst</i> )</code>

Compares two numbers. This function converts its arguments to numbers and returns -1 if the second argument precedes the first, 1 if it exceeds it, and 0 if the arguments are numerically equal:

<code>sdt:compare-number(1, 3)</code> returns <code>-1.0</code>.<br>
<code>sdt:compare-number(3, '3')</code> returns <code>0.0</code>.<br>
<code>sdt:compare-number('6', '4')</code> returns <code>1.0</code>.

Objects that are not numbers are considered equal, and greater than all other numbers:

<code>sdt:compare-number('a', 1)</code> returns <code>1.0</code>.<br>
<code>sdt:compare-number('a', 'b')</code> returns <code>0.0</code>.

If the optional third argument evaluates to true, objects that are not numbers are considered smaller than all numbers.

This function can be used as a comparator in a sort statement.


#### compare-string

<code><i>double</i> sdt:compare-string( <i>string</i>, <i>string</i> )</code><br>
<code><i>double</i> sdt:compare-string( <i>string</i>, <i>string</i>, <i>string language</i> )</code>

Compares two strings locale-sensitive. This function returns -1 if the second string precedes the first, 1 if it exceeds it, and 0 if they are considered equal in the default locale:

<code>sdt:compare-string('a', 'A')</code> returns <code>-1.0</code>.<br>
<code>sdt:compare-string(3, '3')</code> returns <code>0.0</code>.<br>
<code>sdt:compare-string('b', 'A')</code> returns <code>1.0</code>.

An optional third argument specifies the language tag (IETF BCP 47) to obtain a collation strategy that best fits the tag:

<code>sdt:compare-string('Ångström', 'Zulu', 'sv')</code> returns <code>1.0</code> in accordance with Swedish collation rules.

This function can be used as a comparator in a sort statement.


#### current-dateTime

<code><i>string</i> fn:current-dateTime()</code>
 
Returns the current date and time in extended ISO-8601 format.

Note: this implementation is non-deterministic.

See also [Section 15.3 of the XPath Specification](https://www.w3.org/TR/xpath-functions/#func-current-dateTime)


#### dateTime


<code><i>date-time</i> sdt:dateTime( <i>string</i> )</code><br>

A constructor function that returns a date-time as a <i>string</i> in extended ISO-8601 format. Real date-time objects are currently not supported by SDT, so all date and time functions operate on strings.

If the argument is a string compliant with extended ISO-8601 format, this function returns a local or zoned date-time string in ISO_LOCAL_DATE_TIME or ISO_OFFSET_DATE_TIME format, or it will throw an exception if no date-time string can be constructed.

Examples:

<code>sdt:dateTime('1968-02-28T12:00')</code> returns <code>1968-02-28T12:00:00</code>.<br>
<code>sdt:dateTime('1968-02-28T12:00+01:00')</code> returns <code>1968-02-28T12:00:00+01:00</code>.


#### document-node

<code><i>node</i> sdt:document-node( <i>node(set)</i> )</code>

Constructs a new document node from the first SDA node in the set. This function is supplied mainly for backwards compatibility reasons.


#### format-dateTime

<code><i>string</i> sdt:format-dateTime( <i>date-time</i>, <i>string</i> )</code><br>

Returns a formatted date-time string, using a pattern.

Examples:

<code>sdt:format-dateTime('1968-02-28T12:00','yyyy/MM/dd HH:mm')</code> 
returns <code>1968/02/28 12:00</code>.<br>
<code>sdt:format-dateTime(sdt:dateTime(0),'yyyyMMddHHmmss')</code> 
returns <code>19700101000000</code>.


#### left

<code><i>string</i> sdt:left( <i>string</i>, <i>number</i> )</code>

Returns the specified number of characters from the start of the argument string. For example,

<code>sdt:left('12345', 3)</code> returns <code>123</code>.

If the second argument is not a number or less than 1, an empty string is returned. If it exceeds the string length of the first argument, the entire string is returned.


#### millis-to-dateTime
<code><i>date-time</i> sdt:millis-to-dateTime( <i>number</i> )</code><br>

Accepts the number of milliseconds after the epoch (or before in case of a
negative number), and returns a UTC zoned date-time string.

Examples:

<code>sdt:millis-to-dateTime(0)</code> returns <code>1970-01-01T00:00:00Z</code>.<br>
<code>sdt:millis-to-dateTime(sdt:timestamp())</code> returns the current UTC date and time.<br>


#### parse-sda

<code><i>node</i> sdt:parse-sda( <i>string</i> )</code><br>

Parses a string in SDA format and returns a data node.


#### render-sda

<code><i>string</i> sdt:render-sda( <i>node(set)</i> )</code><br>
<code><i>string</i> sdt:render-sda( <i>node(set)</i>, <i>boolean pretty</i> )</code>

Renders the first SDA node in the set as an SDA string in "canonical" format. If the optional second argument evaluates to true, the default SDA formatter is used to produce a reader friendly representation.
 
This functions returns an empty string if the node set is empty or contains something that is not an SDA node.


#### right

<code><i>string</i> sdt:right( <i>string</i>, <i>number</i> )</code>

Returns the specified number of characters from the end of the argument string. For example,

<code>sdt:right('12345', 3)</code> returns <code>345</code>.

If the second argument is not a number or less than 1, an empty string is returned. If it exceeds the string length of the first argument, the entire string is returned.


#### string-join

<code><i>string</i> fn:string-join( <i>node-set</i> )</code><br>
<code><i>string</i> fn:string-join( <i>node-set</i>, <i>string separator</i> )</code>

Returns a string created by concatenating the items in a sequence, with an optional separator between adjacent items. If the sequence is empty, the function returns the zero-length string.

See also [Section 5.4.2 of the XPath Specification](https://www.w3.org/TR/xpath-functions/#func-string-join)


#### timestamp

<code><i>number</i> sdt:timestamp()</code><br>

Returns the current time in milliseconds elapsed since the epoch.


#### tokenize

<code><i>string*</i> sdt:tokenize( <i>string</i> )</code><br>
<code><i>string*</i> sdt:tokenize( <i>string</i>, <i>string pattern</i> )</code><br>
<code><i>string*</i> sdt:tokenize( <i>string</i>, <i>string pattern</i>, <i>boolean allowEmpty</i> )</code>

Breaks the supplied string into tokens and returns a sequence of strings. The optional second argument is a regular expression that specifies the delimiter(s). If absent, tokens are assumed to be whitespace delimited, and the result is equivalent to <code>sdt:tokenize(normalize-space(<i>string</i>),' ')</code>.

The optional third argument is a boolean indicating whether zero length (empty) tokens are retained, the default being false. For example:

<code>sdt:tokenize(' a&nbsp;&nbsp;b&nbsp;&nbsp;&nbsp;c&nbsp;&nbsp;&nbsp;&nbsp;')</code> returns <code>("a","b","c")</code>.<br>
<code>sdt:tokenize('127.0.0.1:80', '[\\.:]')</code> returns <code>(127, 0, 0, 1, 80)</code>.<br>
<code>sdt:tokenize('a; b; ; c; ', '; ', true())</code> returns <code>("a","b","", "c", "")</code>.



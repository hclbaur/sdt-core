# SDT Tutorial

- [PART ONE](/docs/TUTORIAL.md#part-one)
- [IOpener](/docs/TUTORIAL.md#iopener)
- [For each their own](/docs/TUTORIAL.md#for-each-their-own)
- [If only we could choose](/docs/TUTORIAL.md#if-only-we-could-choose)
- [On a different node](/docs/TUTORIAL.md#on-a-different-node)
- [PART TWO](/docs/TUTORIAL.md#part-two)
- [Vars and Pars](/docs/TUTORIAL.md#vars-and-pars)
- [Sorting it out](/docs/TUTORIAL.md#sorting-it-out)


## PART ONE

This tutorial will teach you the SDT (Simple Data Transformation) language, which can be used to write recipes for mapping and transformation of SDA data. It is the equivalent of XSLT for transforming XML - although SDT is a procedural language, and XSLT a declarative one. And since SDT is written in SDA format, it looks like a simple programming language, more so than XSLT.

Nevertheless, if you already know XSLT, all concepts introduced by SDT will be familiar and recognisable. Moreover, SDT uses XPath to query the data it aims to transform, just like XSLT. This tutorial assumes you have a working knowledge of XPath (or know how to acquire it).


### IOpener

When you say transformation, you say input and output. So, without further ado, here is the obligatory Hello World example:

	transform {
		println "'Hello World!'"
	}

This will produce the following output:

	Hello World!

As Roy H. Williams put it: the risk of insult is the price of clarity. In case it was not clear, what follows `println` is an XPath expression and whatever is selected is converted to a string - as if by calling the *string()* function. 

In this example, the "expression" is a very basic one, which selects a string literal. But we can bring in some data from the outside world, using a parameter holding whatever subject we want to greet:

	transform {
		param "subject" { 
			select "'World'" 
		}
		print "'Hello '" 
		print "$subject" 
		println "'!'"
	}

First, note that `print` will omit the line separator, and second that this can be written way more concise if we use the XPath *concat()* function to construct our greeting:

	transform {
		param "subject" { select "'World'" }
		println "concat('Hello ', $subject, '!')"
	}

The `param` statement declares a (global) variable *subject* that can be provisioned by the transformation *context*, overriding the selected default ('World'), so we can greet whomever we like. The required default selection allows us to test or debug the transformation recipe in isolation, e.g. without supplying a context.

However, even parameterized versions of "Hello World" get old rather quickly, so let's transform some actual SDA content, like my (hypothetical) address book: 

	addressbook {
		contact "1" {
			firstname "Alice"
			phonenumber "06-11111111"
		}
		contact "2" {
			firstname "Bob"
			phonenumber "06-22222222"
			phonenumber "06-33333333"
		}
		contact "3" {
			firstname "Chris"
			phonenumber "06-44444444"
		}
	}

The address book might be in an SDA file called `addressbook.sda`, located in whatever the transformation context perceives as the current working directory (unless this default filename is overwritten). In any case, this is how we transform it:

	transform {
		param "filename" { select "'addressbook.sda'" }
		variable "doc" { select "document($filename)" }
		println "concat('Hello ', $doc/addressbook/contact[1]/firstname, '!')"
	}

We use the *document()* function to read and parse the file into an SDA node tree, which is bound to a variable *doc* for reference in subsequent expressions. Note that unlike `param`, the `variable` statement declares a variable that can *not* be overriden by the transformation context. Executing this recipe would produce:

	Hello Alice!

And with that, we shall move on to much more interesting stuff.


### For each their own

Suppose we want to iterate over all phone numbers in the address book and list them. The following recipe could be used to do that:

<pre>
transform {
	<i>...(read address book)...</i>
	foreach "$doc/addressbook/contact/phonenumber" {
		println "concat('Number ', $sdt:position, ' of ', $sdt:last, ': ', ., ' (', ../firstname, ')')"
	}
}
</pre>

Assuming the right input file was read, the `foreach` statement will iterate all 'phonenumber' nodes, and for each number print a line of text:

	Number 1 of 4: 06-11111111 (Alice)
	Number 2 of 4: 06-22222222 (Bob)
	Number 3 of 4: 06-33333333 (Bob)
	Number 4 of 4: 06-44444444 (Chris)

Inside the loop, the node currenlty iterated is the *context node* (".") and the 'firstname' node is referenced in a relative way ("../firstname"). Furthermore, the automatic variable *sdt:position* holds the index of the current context node, starting at 1 and ending at *sdt:last*, which is also automatic and equals the size of the iterated set.

Now, we will add a predicate that selects only the "odd" phone numbers (1 and 3) and see what happens:

	...
	foreach "($doc//phonenumber)[(position() mod 2) = 1]" {
		println "concat('Number ', $sdt:position, ' of ', $sdt:last, ': ', ., ' (', ../firstname, ')')"
	}

The result would be:

	Number 1 of 2: 06-11111111 (Alice)
	Number 2 of 2: 06-33333333 (Bob)
	
Note that *sdt:position* and *sdt:last* apply to the iterated set, which is limited by the predicate prior to the actual iteration. Unlike the functions *position()* and *last()*, these variables are not changed within a predicate.

A third automatic variable is available whithin loops, namely *sdt:current*, which holds the current context node. It can be used instead of "." within a predicate, or just for readability.

There's more to say about iterations; we will come back to this subject later.


### If only we could choose

Whether you are iterating a set or not, processing may be *conditional*; you may want to process something in a different way - or not at all - depending on the outcome of some pre-defined test. For example, here is how to print the odd numbers in a different way:

	...
	foreach "$doc//phonenumber" {
		if "($sdt:position mod 2) = 1" {
			println "concat('Number ', $sdt:position, ' of ', $sdt:last, ': ', ., ' (', ../firstname, ')')"
		}
	}

Which would yield:

	Number 1 of 4: 06-11111111 (Alice)
	Number 3 of 4: 06-33333333 (Bob)

(compare this to the result we obtained when using a predicate and recall that *sdt:position* and *sdt:last* apply to the iterated set). 

Whatever follows the `if` statement is evaluated as a Boolean expression, and if true, the statement block is executed. This means that 

	if "$sdt:position mod 2" { ... }

would have worked equally well, since *boolean(1)* evaluates to true.

Just like in XSLT, there is no `else(if)` clause, but we do have a way to test against multiple conditions. Here is how that works:

	...
	foreach "$doc//phonenumber" {
		print "concat('Number ', $sdt:position, ' of ', $sdt:last, ': ', .)"
		choose {
			when "$sdt:position mod 2" {
				println "' (odd)'"
			}
			when "not($sdt:position mod 2)" {
				println "' (even)'"
			}
			otherwise {
				println "' (???)'"
			}
		}
	}

And this would be the result:

	Number 1 of 4: 06-11111111 (odd)
	Number 2 of 4: 06-22222222 (even)
	Number 3 of 4: 06-33333333 (odd)
	Number 4 of 4: 06-44444444 (even)

Obviously, the `otherwise` statement block is never executed; I merely included it to show you the full syntax of the `choose` statement - which requires one or more `when` clauses, followed by an optional `otherwise` clause.  


### On a different node

So far we have been using a `print(ln)` statement to generate output, which is fine for simple things or for debugging purposes. But when generating complex data structures, it is cumbersome. More often than not, you will want to "map" input nodes to output nodes, creating a new SDA node tree from existing ones. Luckily, creating nodes is rather straight-forward (I am omitting the code that reads the address book):

<pre>
transform {
	<i>... read address book ...</i>
		node "contacts" {
			foreach "$doc/addressbook/contact" {
				node "person" { value "upper-case(firstname)" }
				node "phonenumbers" { value "fn:string-join(phonenumber,',')" }
			}
		}
	}
</pre>

This will produce the following SDA document:

	contacts {
		person "ALICE" 
		phonenumbers "06-11111111"
		person "BOB" 
		phonenumbers "06-22222222,06-33333333"
		person "CHRIS" 
		phonenumbers "06-44444444"
	}

The `node` statement will instantiate a new node with the specified name and (an optional) `value` equal to the string evaluation of the given expression. Any child nodes can be created within the statement block.

Note that if an output node should be *identical* to an input node, you can use the `copy` statement to clone the selected node(s):

	...
	node "contacts" {
		foreach "$doc/addressbook/contact" {
			node "person" { 
				value "upper-case(firstname)" 
				copy "phonenumber"
			}
		}
	}

Which will create the following document:

	contacts {
		person "ALICE" {
			phonenumber "06-11111111"
		}
		person "BOB" {
			phonenumber "06-22222222"
			phonenumber "06-33333333"
		}
		person "CHRIS" {
			phonenumber "06-44444444"
		}
	}



## PART TWO

The first part of this tutorial introduced the basic features of SDT; variables, iteration, conditional processing and creation of SDA nodes. The second part will elaborate on these concepts and introduce some advanced ones.


### Vars and Pars

Early in part one of this tutorial, the `param` statement was introduced. Parameters are global variables that are assigned a default value that can be overwritten by the "outside world", prior to execution of the transform. A parameter can be declared and assigned only once, and only on the transform level.

In other words, this is *not* allowed, because *P* is declared twice:

	transform {
		param "P" { select "1" }
		param "P" { select "2" }
	}

And neither is this, because *P* has no global scope:

<pre>
transform {
	if "<i>some condition</i>" {
		param "P" { select "1" }
	}
}
</pre>

Other than that, you can hardly go wrong with parameters.

Variables created with the `variable` statement are quite different. For starters, you can (re)declare them as often as you like, and practically anywhere you like:

This is perfectly fine (*V* will equal 2 at the end of the transform):

	transform {
		variable "V" { select "1" }
		variable "V" { select "$V + 1" }
	}

And so is this (*V* will increment by 1 with each iteration):

<pre>
transform {
	variable "V" { select "1" }
	foreach "<i>some node-set</i>" {
		variable "V" { select "$V + 1" }
	}
}
</pre>

Note that due to the local scope of variables, *V* will equal 1 after the last iteration. After all, the inner *V* shadows the outer one (which is not incremented). 

Also, a variable with the same name can be declared in unrelated contexts:

<pre>
transform {
	if "<i>some condition</i>" {
		variable "V" { select "1" }
	}
	if "<i>some condition</i>" {
		variable "V" { select "2" }
	}
}
</pre>

Obviously, neither *V* can be referenced outside the *if* clauses.

For *automatic* variables, the same rules apply with regards to scoping, but it is not possible to declare or re-assign them. Automatic variables "live" within the reserved SDT namespace, which ensures there is no overlap with the ones you declare yourself (in the "unnamed" namespace). The following example illustrates this:

	transform {
		foreach "..." {
			variable "position" { select "$sdt:position" }
			foreach "..." {
				println "concat('outer ', $position, ' inner ', $sdt:position)" }
			}
		}
	}

There is no way to reference the outer *sdt:position* variable as it is shadowed by the one in the inner loop. But we can save its value in a *position* variable of our own, and reference that from the inner loop, as it doesn't clash with the one in the SDT namespace.


### Sorting it out

In this section, we will revisit the subject of iteration, and address a common requirement: sorting. In its basic form, sorting a node-set is just a matter of specifying the sorting key, like this:

	foreach "$doc/addressbook/contact" {
		sort "firstname"
		println "firstname"
	}

This will print "Alice", "Bob" and "Christopher" - regardless of how the contacts 
are ordered in the addressbook node. Reversing sort order - so the output will be "Christopher", "Bob" and "Alice" - is just a matter of using the `reverse` keyword.
 
 	foreach "$doc/addressbook/contact" {
		sort "firstname" { reverse "true()" }
		println "firstname"
	}

Sorting keys are not limited to node values, but can be any effective value "extracted" by the sort expression. For instance, to sort contacts in order of increasing length of their firstname:

 	foreach ... {
		sort "string-length(firstname)"
	... }

Oops. This will print "Christopher", "Bob" and "Alice" again, when obviously "Christopher" - with length 11 - should come last. What went wrong? 

By default, values are compared lexicographically, which means that "11" comes before "3" and "5", rather than after it. When sorting numeric values, we must use an appropriate `comparator``, as in

 	foreach ... {
		sort "string-length(firstname)" { comparator "sdt:compare-number(?,?)" }
	... }

A comparator is a function with at least two arguments, and returns a negative number, zero, or a positive number, depending on whether the effective value of the first argument collates before, equal to, or after the second one. The question marks - in what is effectively a Lambda expression - act as a placeholder for the objects to be compared.

Let's go back to sorting names. If your addressbook neatly capitalizes all names, you may get away with a lexicographical sort. Otherwise, you are in trouble, because lowercase letters collate after *all* uppercase ones, so for example "alice" would come after "Zoey".

To address this we could sort in a case-insensitive manner, like so:

	sort "lower-case(firstname)"

However, this will ignore case rather than handle it. A better solution is to use a locale-sensitive comparator that takes case differences as well as accented characters into account:

	sort "firstname" { comparator "sdt:compare-string(?,?)" }

This comparator even accepts language tags, so to please our Swedish audience we can ensure that "Ådel" is sorted properly *after* "Zoey", using

 	comparator "sdt:compare-string(?,?,'sv')" }

because to them, Å is a letter that comes after Z - rather than the funny looking A it is to us.

To wrap things up, here is an example that uses multiple sort statements to order contacts first by the number of phones they own, and then by their first name: 

	foreach "$doc/addressbook/contact" {
		sort "count(phonenumber)" sort "firstname" 
		println "concat(firstname, ' has ', count(phonenumber), '.')"
	}

	Christopher has 1.
	Alice has 2.
	Bob has 2.

[ TO BE CONTINUED ]
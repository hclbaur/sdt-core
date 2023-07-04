# SDT Tutorial

- [PART ONE](/docs/TUTORIAL.md#part-one)
- [IOpener](/docs/TUTORIAL.md#iopener)
- [For each their own](/docs/TUTORIAL.md#for-each-their-own)
- [If only we could choose](/docs/TUTORIAL.md#if-only-we-could-choose)
- [On a different node](/docs/TUTORIAL.md#on-a-different-node)
- [PART TWO](/docs/TUTORIAL.md#part-two)


## PART ONE

This tutorial will teach you the SDT (Simple Data Transformation) language, which 
can be used to write recipes for mapping and transformation of SDA data. It is the 
equivalent of XSLT for transforming XML - although SDT is a procedural language, 
and XSLT a declarative one. And since SDT is written in SDA format, it looks like 
a simple programming language, more so than XSLT.

Nevertheless, if you already know XSLT, all concepts introduced by SDT will be 
familiar and recognisable. Moreover, SDT uses XPath to query the data it aims to 
transform, just like XSLT. This tutorial assumes you have a working knowledge of 
XPath (or know how to acquire it).


### IOpener

When you say transformation, you say input and output. So, without further ado, 
here is the obligatory Hello World example:

	transform {
		println { value "'Hello World!'" }
	}

This will produce the following output:

	Hello World!

As Roy H. Williams put it: the risk of insult is the price of clarity. In case it 
was not clear, whatever follows `value` is an XPath expression and whatever is 
selected is converted to a string - as if by calling the *string()* function. 

In this example, the "expression" is a very basic one which selects a string 
literal anyway. But we can change that; let's bring in some input data from the 
outside world, and use the XPath *concat()* function to transform it:

	transform {
		param "subject" { select "'World'" }
		println { value "concat('Hello ', $subject, '!')" }
	}

The `param` statement declares a (global) variable *subject* that should be 
provisioned by the transformation *context*, overriding the selected default. The 
default is useful as it allows us to test or debug the recipe in isolation.

Even parameterised versions of Hello World get old rather quickly, so let's 
transform some actual SDA content, like my (hypothetical) address book:

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

The address book might be in an SDA file called `addressbook.sda`, located in 
whatever the transformation context perceives as the current working directory 
(unless this default location is overwritten). In any case, this is how we 
transform it:

	transform {
		param "filename" { select "'addressbook.sda'" }
		variable "doc" { select "document($filename)" }
		println { 
			value "concat('Hello ', $doc/addressbook/contact[1]/firstname, '!')"
		}
	}

We use the *document()* function to read and parse the file into an SDA node tree,
 which is bound to a variable *doc* for reference in subsequent expressions. Note
 that unlike `param`, the `variable` statement declares a variable that can *not* 
be overriden by the transformation context. Executing this recipe would produce:

	Hello Alice!

And with that, we shall move on to much more interesting stuff.


### For each their own

Suppose we want to iterate over all phone numbers in the address book and 
transform them in one way or another. The following recipe could be used to do 
that:

<pre>
transform {
	<i>...(read address book)...</i>
	foreach "$doc/addressbook/contact/phonenumber" {
		println { 
			value "concat('Number ', $sdt:position, ' of ', $sdt:last, ': ', ., ' (', ../firstname, ')')"
		}
	}
}
</pre>

Assuming the right input file was read, the `foreach` statement will iterate all 
'phonenumber' nodes, and for each number print a line of text:

	Number 1 of 4: 06-11111111 (Alice)
	Number 2 of 4: 06-22222222 (Bob)
	Number 3 of 4: 06-33333333 (Bob)
	Number 4 of 4: 06-44444444 (Chris)

Inside the loop, the node currenlty iterated is the *context node* (".") and the 
'firstname' node is referenced in a relative way ("../firstname"). Furthermore, 
the automatic variable *sdt:position* holds the index of the current context 
node, starting at 1 and ending at *sdt:last*, which is also automatic and equals 
the size of the iterated set.

Now, we will add a predicate that selects only the "odd" phone numbers (1 and 3) 
and see what happens:

	...
	foreach "($doc//phonenumber)[(position() mod 2) = 1]" {
		println { 
			value "concat('Number ', $sdt:position, ' of ', $sdt:last, ': ', ., ' (', ../firstname, ')')"
		}
	}

The result would be:

	Number 1 of 2: 06-11111111 (Alice)
	Number 2 of 2: 06-33333333 (Bob)
	
Note that *sdt:position* and *sdt:last* apply to the iterated set, which is 
limited by the predicate prior to the actual iteration. Unlike the functions 
*position()* and *last()*, these variables are not changed within a predicate.

A third automatic variable is available whithin loops, namely *sdt:current*, 
which holds the current context node. It can be used instead of "." within a 
predicate, or just for readability.

There's more to say about iterations; we will come back to this subject later.


### If only we could choose

Whether you are iterating a set or not, processing may be *conditional*; you may 
want to process something in a different way - or not at all - depending on the 
outcome of some pre-defined test. For example, to print the odd numbers in a 
different way:

	...
	foreach "$doc//phonenumber" {
		if "($sdt:position mod 2) = 1" {
			println {
				value "concat('Number ', $sdt:position, ' of ', $sdt:last, ': ', ., ' (', ../firstname, ')')"
			}
		}
	}

Which would yield:

	Number 1 of 4: 06-11111111 (Alice)
	Number 3 of 4: 06-33333333 (Bob)

(compare this to the result we obtained when using a predicate and recall that 
*sdt:position* and *sdt:last* apply to the iterated set). 

Whatever follows the `if` statement is evaluated as a Boolean expression, and if 
true, the statement block is executed. This means that 

	if "$sdt:position mod 2" { ... }

would have worked equally well, since *boolean(1)* evaluates to true.

Just like in XSLT, there is no `else(if)` clause, but we do have a way to test 
against multiple conditions. Here is how that works:

	...
	foreach "$doc//phonenumber" {
		print { value "concat('Number ', $sdt:position, ' of ', $sdt:last, ': ', .)" }
		choose {
			when "$sdt:position mod 2" {
				println { value "' (odd)'" }
			}
			when "not($sdt:position mod 2)" {
				println { value "' (even)'" }
			}
			otherwise {
				println { value "' (???)'" }
			}
		}
	}

And this would be the result:

	Number 1 of 4: 06-11111111 (odd)
	Number 2 of 4: 06-22222222 (even)
	Number 3 of 4: 06-33333333 (odd)
	Number 4 of 4: 06-44444444 (even)

Obviously, the `otherwise` statement block will never be executed; I included it 
merely to show the full syntax of the `choose` statement - which requires one or 
more `when` clauses, followed by an optional `otherwise` clause.  


### On a different node

So far we have been using `print(ln)` to generate output, which is fine for simple 
things or for debugging purposes. But when generating complex data structures, it 
is cumbersome. More often than not, you will want to "map" input nodes to output 
nodes, creating a new SDA node tree from existing ones. Luckily, creating nodes is 
rather straight-forward:

<pre>
transform {
	<i>...(read address book)...</i>
		node "contacts" {
			foreach "$doc/addressbook/contact" {
				node "person" { value "firstname" }
				node "phonenumbers" { value "fn:string-join(phonenumber,',')" }
			}
		}
	}
</pre>

This will produce the following SDA document:

	contacts {
		person "Alice" 
		phonenumbers "06-11111111"
		person "Bob" 
		phonenumbers "06-22222222,06-33333333"
		person "Chris" 
		phonenumbers "06-44444444"
	}

The `node` statement will instantiate a new node with the specified name and (an 
optional) `value` equal to the string evaluation of the given expression. Any child 
nodes can be created within the statement block.

Note that if an output node should be *identical* to an input node, you can use the
`copy` statement to create a (deep) clone of the selected node(s):

		...
		node "contacts" {
			foreach "$doc/addressbook/contact" {
				node "person" { 
					value "firstname" 
					copy { select "phonenumber" }
				}
			}
		}

Which will create the following document:

	contacts {
		person "Alice" {
			phonenumber "06-11111111"
		}
		person "Bob" {
			phonenumber "06-22222222"
			phonenumber "06-33333333"
		}
		person "Chris" {
			phonenumber "06-44444444"
		}
	}



## PART TWO

The first part of this tutorial introduced the basic features of SDT; variables, 
iteration, conditional processing and creation of SDA nodes. The second part will 
elaborate on these concepts and introduce some advanced ones.


### More on variables

In the second example of this tutorial, the `param` statement was introduced. 
Parameters are variables that are assigned a default value that can be overwritten 
by the "outside world", prior to execution of the transform. They can be declared 
anywhere, as long as each parameter is declared only once within its context. That 
last remark is open to interpretation so here are some examples to clarify this.

This is not allowed:

	transform {
		param "mytext" { select "..." }
		param "mytext" { select "..." }
	}

And neither is this:

	transform {
		param "mytext" { select "..." }
		foreach "..." {
			param "mytext" { select "..." }
		}
	}

But this is fine:

	transform {
		foreach "..." {
			param "mytext" { select "..." }
		}
		foreach "..." {
			param "mytext" { select "..." }
		}
	}


Variables created with the `variable` statement are quite different. For starters,
you can (re)declare them as often as you like, practically anywhere you like:

This is perfectly fine (*total* will equal 2 at the end of the transform):

	transform {
		variable "total" { select "1" }
		variable "total" { select "$total + 1" }
	}

And so is this (*total* will increment by 1 within each iteration):

	transform {
		variable "total" { select "1" }
		foreach "..." {
			variable "total" { select "$total + 1" }
		}
	}

Due to the local scope of variables, *total* equals 1 after the last iteration; the 
inner *total* shadows the one in the outer scope (which is not incremented). Also, 
the same variable can be used in unrelated contexts:

	transform {
		foreach "..." {
			variable "total" { select "1" }
		}
		foreach "..." {
			variable "total" { select "2" }
		}
	}

Obviously, in this case *total* cannot be referenced outside the iterations.

For *automatic* variables, the same rules apply with regards to scoping, but it is 
not possible to declare or re-assign them. Also, automatic variables "live" within 
the predefined SDT namespace, which ensures there is no overlap with the ones you 
declare yourself (in the "unnamed" namespace):

	transform {
		foreach "..." {
			variable "position" { select "sdt:position" }
			foreach "..." {
				println { value "concat('outer ', $position, ' inner ', sdt:position)" }
			}
		}
	}

The inner variable *sdt:position* shadows the one in the outer loop, but if we save 
its current value in our own *position* variable, we can reference that from the 
inner loop. 

[ TO BE CONTINUED ]
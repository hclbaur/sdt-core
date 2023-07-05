# SDT Core

The SDA project was conceived in 2008 and aims to produce Java libraries that 
(ultimately) support parsing, validation and transformation of SDA content. The 
SDT core library supplies classes to parse and execute transformation scripts.
It depends on the [SDA core](https://github.com/hclbaur/sda-core) library and 
on [Jaxen](http://www.cafeconleche.org/jaxen) for XPath support.

## What is SDT

SDT is to SDA what XSLT is to XML. In other words, it is a language that allows 
you to create mapping and transformation recipies to read and write SDA content.

For example:

	transform {

		param "filename" { select "'addressbook.sda'" }
		variable "doc" { select "document($filename)" }

		node "contacts" {
			foreach "$doc/addressbook/contact" {
				node "person" { 
					value "firstname"
					node "phones" {
						value "fn:string-join(phonenumber,',')"
					}
				}
			}
		}
	}

As you can see the transform reads like a scripting language if you are already 
familiar with SDA and XPath. The example reads an 'addressbook' in SDA format
and produces a new SDA document with the same data in a different format. 

I do not expect you to grasp the SDT syntax in a glance, so please refer to the 
[documentation](docs/) for a tutorial.

## Running the demo

You may read up on [SDA](https://github.com/hclbaur/sda-core#what-is-sda) before
running the SDT demo, but this is not required.

In order to run the demo, get `demo.jar`, `addressbook.sda` and `addressbook.sdt` 
from the latest SDT [release](https://github.com/hclbaur/sdt-core/releases/latest) 
and copy these to a temporary directory where you will run the demo. Assuming the 
java executable is in your path, run it like this:

	java -jar demo.jar addressbook.sdt addressbook.sda
	
This will parse the `addressbook.sdt` script and transform the data in the file 
`addressbook.sda` from

	addressbook {
		contact "1" {
			firstname "Alice"
			phonenumber "06-11111111"
			phonenumber "06-22222222"
		}
		contact "2" {
			firstname "Bob"
			phonenumber "06-33333333"
			phonenumber "06-44444444"
		}
	}

to

	document {
		contacts {
			person "Alice" {
				phones "06-11111111,06-22222222"
			}
			person "Bob" {
				phones "06-33333333,06-44444444"
			}
		}
	}

Have a look at the [code](src/test/java/demo.java) to see how it's done. I hope 
this demonstrates how easy it is to use SDT for transformation of SDA content.

----

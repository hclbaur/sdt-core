# SDT Core

The SDA project was conceived in 2008 and aims to produce Java libraries that (ultimately) support parsing, validation and transformation of SDA content. The SDT core library supplies classes to parse and execute transformation scripts. It depends on the [SDA core](https://github.com/hclbaur/sda-core) library and on [Jaxen](http://www.cafeconleche.org/jaxen) for XPath support.

## What is SDT

SDT is to SDA what XSLT is to XML. In other words, it is a language that allows you to create mapping and transformation recipes to read and write SDA content.

For example, this recipe parses an 'addressbook' in SDA format and produces a new SDA document with the same data in a different format, while using functions to transform the node values:

	transform {

		param "filename" { select "'addressbook.sda'" }
		variable "addressbook" { select "document($filename)" }

		node "contacts" {
			foreach "$addressbook/contact" {
				node "person" { 
					value "upper-case(firstname)"
					node "phones" {
						value "fn:string-join(phonenumber,',')"
					}
				}
			}
		}
	}

As you can see the transform reads like a scripting language if you are already familiar with SDA and XPath. I do not expect you to grasp the SDT syntax at a glance, so please refer to the [tutorial](docs/TUTORIAL.md) and [specification](docs/SPECIFICATION.md) for details.

## Running the demo

You may read up on [SDA](https://github.com/hclbaur/sda-core#what-is-sda) before running the SDT demo, but this is not required.

In order to run the demo, get `demo.jar`, `addressbook.sda` and `addressbook.sdt` from the latest [release](https://github.com/hclbaur/sdt-core/releases/latest) and copy these to a temporary directory where you will run the demo. Assuming the java executable is in your path, run it like this:

	java -jar demo.jar addressbook.sdt addressbook.sda
	
This will transform the data in the file `addressbook.sda` according to the recipe shown earlier.

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

into

	output {
		contacts {
			person "ALICE" {
				phones "06-11111111,06-22222222"
			}
			person "BOB" {
				phones "06-33333333,06-44444444"
			}
		}
	}

Have a look at the [code](src/main/java/demo.java) to see how it was done. I hope this demonstrates the use of SDT for transformation of SDA content. This also concludes my SDA trilogy, but who knows - there may be more to come. 

----

package be.baur.sdt;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.jaxen.Function;
import org.jaxen.function.SubstringFunction;

import be.baur.sdt.parser.SDTParseException;
import be.baur.sdt.parser.SDTParser;
import be.baur.sdt.transform.Transform;

/**
 * This class defines static constants and utility methods.
 */
public final class SDT {
	
	private SDT() {} // cannot construct this

	private static final Writer NULL_WRITER = new NullWriter();
	private static class NullWriter extends Writer {
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {}
		@Override
		public void flush() throws IOException {}
		@Override
		public void close() throws IOException {}
	}

	/**
	 * Returns a {@code Writer} that discards everything. The methods {@code write},
	 * {@code flush} and {@code close} do nothing at all. Note that as of Java 11 a
	 * {@code Writer.nullWriter()} can be used instead.
	 * 
	 * @return a NullWriter()
	 */
	public static Writer nullWriter() {
		return NULL_WRITER;
	}

	
	/** Namespace prefix of SDT specific XPath functions. */
	public static final String FUNCTIONS_NS_PFX = "sdt";
	/** Namespace URI for SDT specific XPath functions. */
	public static final String FUNCTIONS_NS_URI = "be.baur.sdt.xpath.function";

	/** Namespace prefix of W3C XPath 2 functions. */
	public static final String W3CFUNCTIONS_NS_PFX = "fn";
	/** Namespace URI for W3C XPath 2 functions. */
	public static final String W3CFUNCTIONS_NS_URI = "http://www.w3.org/2005/xpath-functions";


	/** Singleton instance of SubstringFunction for use in other functions. */
	public static final Function SUBSTRING = new SubstringFunction();
	
	
	private static SDTParser PARSER = new SDTParser(); // singleton parser

	/**
	 * Creates a transform from a character input stream in SDT format, using the
	 * default SDT parser.
	 * 
	 * @param input an input stream
	 * @return a transform
	 * @throws IOException       if an I/O operation failed
	 * @throws SDTParseException if an SDT parse exception occurs
	 * @see SDTParser
	 */
	public static Transform parse(Reader input) throws IOException, SDTParseException {
		return PARSER.parse(input);
	}

}

package be.baur.sdt.serialization;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import be.baur.sda.serialization.ParseException;
import be.baur.sdt.SDT;
import be.baur.sdt.Transform;

/**
 * A {@code Parser} is a <i>deserializer</i> that reads an input stream in a
 * format specific to the type of parser, and creates a {@link Transform}. A
 * sample implementation is the default SDT parser.
 * 
 * @see SDTParser
 */
public interface Parser {


	/**
	 * Creates a Transform from a character input stream.
	 * 
	 * @param input an input stream
	 * @return a Transform
	 * @throws IOException if an input exception occurs
	 * @throws SDTParseException if a parse exception occurs
	 */
	Transform parse(Reader input) throws IOException, ParseException, SDTParseException;


	/**
	 * Verifies a Transform. This method can be used to to validate a Transform that
	 * was not created by the default {@code SDTParser}, but assembled "manually" or
	 * created by another parser. The default implementation serializes the
	 * Transform to SDT format, which is then parsed back to reveal any issues.
	 * 
	 * @param transform the Transform to be verified
	 * @throws IOException    if an input exception occurs
	 * @throws SDTParseException if a parse exception occurs
	 * 
	 */
	default void verify(Transform transform) throws IOException, ParseException, SDTParseException {
		SDT.parser().parse(new StringReader(transform.toString()));
	}
}

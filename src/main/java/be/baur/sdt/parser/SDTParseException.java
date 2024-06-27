package be.baur.sdt.parser;

import be.baur.sda.Node;
import be.baur.sda.ProcessingException;

/**
 * An {@code SDTParseException} is thrown by an {@code SDTParser} if the SDS
 * syntax or semantics are violated.
 */
@SuppressWarnings("serial")
public class SDTParseException extends ProcessingException {

	/**
	 * Creates an SDT parse exception with an error node and message.
	 * 
	 * @param node    the node where the error was found
	 * @param message an error message
	 */
	public SDTParseException(Node node, String message) {
		super(node, message);
	}

	
	/**
	 * Creates an SDT parse exception caused by another exception.
	 * 
	 * @param node    the node where an exception occurred
	 * @param message an error message
	 * @param cause   the exception causing this exception to be thrown
	 */
	public SDTParseException(Node node, String message, Throwable cause) {
		super(node, cause.getMessage()); initCause(cause);
	}

}

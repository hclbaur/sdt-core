package be.baur.sdt.serialization;

import be.baur.sda.Node;
import be.baur.sda.ProcessingException;

/**
 * An {@code SDTParseException} is thrown by an {@code SDTParser} if the SDS
 * syntax or semantics are violated.
 * 
 * @see SDTParser
 */
@SuppressWarnings("serial")
public class SDTParseException extends ProcessingException {

	/**
	 * Creates an SDTParseException with a detail message.
	 * 
	 * @param node    the node where an error was found
	 * @param message an error message
	 */
	public SDTParseException(Node node, String message) {
		super(node, message);
	}

	
	/**
	 * Creates an SDTParseException caused by another exception.
	 * 
	 * @param node  the node where an exception occurred
	 * @param cause the exception causing this exception to be thrown
	 */
	public SDTParseException(Node node, Throwable cause) {
		super(node, cause.getMessage()); initCause(cause);
	}

}

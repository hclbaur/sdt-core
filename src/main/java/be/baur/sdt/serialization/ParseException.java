package be.baur.sdt.serialization;

import be.baur.sda.Node;
import be.baur.sdt.NodeProcessingException;

/**
 * An {@code ParseException} may be thrown by the {@code SDTParser} when
 * creating a {@code Transform}. See also {@link NodeProcessingException}.
 */
@SuppressWarnings("serial")
public class ParseException extends NodeProcessingException {

	/**
	 * Creates a ParseException with a detail message.
	 * 
	 * @param node    the node where the error was found
	 * @param message an error message
	 */
	public ParseException(Node node, String message) {
		super(node, message);
	}

	
	/**
	 * Creates ParseException caused by another exception.
	 * 
	 * @param node  the node where the error was found
	 * @param cause the exception causing this exception to be thrown
	 */
	public ParseException(Node node, Throwable cause) {
		super(node, cause.getMessage(), cause);
	}

}

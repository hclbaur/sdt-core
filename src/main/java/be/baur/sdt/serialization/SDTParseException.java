package be.baur.sdt.serialization;

import be.baur.sda.Node;
import be.baur.sdt.NodeProcessingException;

/**
 * An {@code SDTParseException} may be thrown by the {@code SDTParser} when
 * creating a {@code Transform}. See also {@link NodeProcessingException}.
 */
@SuppressWarnings("serial")
public class SDTParseException extends NodeProcessingException {

	/**
	 * Creates a SDTParseException with a detail message.
	 * 
	 * @param node    the node where the error was found
	 * @param message an error message
	 */
	public SDTParseException(Node node, String message) {
		super(node, message);
	}

	
	/**
	 * Creates SDTParseException caused by another exception.
	 * 
	 * @param node  the node where the error was found
	 * @param cause the exception causing this exception to be thrown
	 */
	public SDTParseException(Node node, Throwable cause) {
		super(node, cause.getMessage(), cause);
	}

}

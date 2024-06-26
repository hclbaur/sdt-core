package be.baur.sdt;

import be.baur.sda.Node;
import be.baur.sda.ProcessingException;

/**
 * An {@code TransformException} may be thrown during the execution of a
 * transformation recipe.
 */
@SuppressWarnings("serial")
public final class TransformException extends ProcessingException {

	/**
	 * Creates a transform exception with an error node and message.
	 * 
	 * @param node    the node where an error occurred
	 * @param message an error message
	 */
	public TransformException(Node node, String message) {
		super(node, message);
	}

	
	/**
	 * Creates a transform exception caused by another exception.
	 * 
	 * @param node    the node where an exception occurred
	 * @param cause   the exception causing this exception to be thrown
	 */
	public TransformException(Node node, Throwable cause) {
		super(node, cause.getMessage()); initCause(cause);
	}

}

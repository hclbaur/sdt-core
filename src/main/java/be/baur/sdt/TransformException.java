package be.baur.sdt;

import be.baur.sda.Node;
import be.baur.sda.serialization.NodeProcessingException;

/**
 * An {@code TransformException} may be thrown during the execution of a
 * transformation recipe. See also {@link NodeProcessingException}.
 */
@SuppressWarnings("serial")
public final class TransformException extends NodeProcessingException {

	/**
	 * Creates a TransformException with a detail message.
	 * 
	 * @param node    the node where the exception was thrown
	 * @param message an error message
	 */
	public TransformException(Node node, String message) {
		super(node, message);
	}

	
	/**
	 * Creates a TransformException caused by another exception.
	 * 
	 * @param node  the node where the exception was thrown
	 * @param cause the exception causing this exception to be thrown
	 */
	public TransformException(Node node, Throwable cause) {
		super(node, cause.getMessage(), cause);
	}

}

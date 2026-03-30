package be.baur.sdt;

import be.baur.sda.NodeException;
import be.baur.sdt.transform.Statement;

/**
 * An {@code TransformException} may be thrown during the execution of a
 * transformation recipe.
 */
@SuppressWarnings("serial")
public final class TransformException extends NodeException {

	/**
	 * Creates a transform exception with an error node and message.
	 * 
	 * @param statement the statement where an error occurred
	 * @param message   an error message
	 */
	public TransformException(Statement statement, String message) {
		super(statement, message);
	}

	
	/**
	 * Creates a transform exception caused by another exception.
	 * 
	 * @param statement the statement where an exception occurred
	 * @param cause     the exception causing this exception to be thrown
	 */
	public TransformException(Statement statement, Throwable cause) {
		super(statement, cause.getMessage()); initCause(cause);
	}

}

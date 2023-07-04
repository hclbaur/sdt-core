package be.baur.sdt;

/**
 * An {@code SDTException} is the root of all SDT related exceptions, and may
 * wrap other exceptions.
 */
@SuppressWarnings("serial")
public class SDTException extends Exception {

	/**
	 * Creates an SDT exception with a detail message.
	 * 
	 * @param message an error message
	 */
	public SDTException(String message) {
		super(message);
	}

	
//	/**
//	 * Creates an SDT exception caused by another exception.
//	 * 
//	 * @param cause the exception causing this exception to be thrown
//	 */
//	public SDTException(Throwable cause) {
//		super(cause.getMessage(), cause);
//	}

	
	/**
	 * Creates an SDT exception with a detail message and a root cause.
	 * 
	 * @param message an error message
	 * @param cause   the exception causing this exception to be thrown
	 */
	public SDTException(String message, Throwable cause) {
		super(message, cause);
	}

}

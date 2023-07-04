package be.baur.sdt;

import be.baur.sda.Node;

/**
 * An {@code NodeProcessingException} may be thrown by a class processing an SDA
 * node tree when a syntactical or semantical violation is encountered. The node
 * where the exception was thrown is available from {@link #getErrorNode()}.
 */
@SuppressWarnings("serial")
public abstract class NodeProcessingException extends SDTException {
	/*
	 * Maybe move this to SDA core?
	 */
	private Node errorNode;
	
	/**
	 * Creates a NodeProcessingException with a detail message.
	 * 
	 * @param node    the node where the exception was thrown
	 * @param message an error message
	 */
	public NodeProcessingException(Node node, String message) {
		super(message); this.errorNode = node;
	}

	
//	/**
//	 * Creates a NodeProcessingException caused by another exception.
//	 * 
//	 * @param node  the node where the exception was thrown
//	 * @param cause the exception causing this exception to be thrown
//	 */
//	public NodeProcessingException(Node node, Throwable cause) {
//		super(cause); this.errorNode = node;
//	}
	
	
	/**
	 * Creates a NodeProcessingException with a detail message and a root cause.
	 * 
	 * @param node    the node where the exception was thrown
	 * @param message an error message
	 * @param cause   the exception causing this exception to be thrown
	 */
	public NodeProcessingException(Node node, String message, Throwable cause) {
		super(message, cause); this.errorNode = node;
	}
	
	
	/**
	 * Returns the node where the exception was thrown.
	 * 
	 * @return a Node 
	 */
	public Node getErrorNode() {
		return errorNode;
	}
}

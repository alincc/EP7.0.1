package com.elasticpath.repo.datasync.tools.exception;

/**
 * This runtime exception should be thrown if an exchange redelivery needs to be attempted.
 */
@SuppressWarnings("serial")
public class RecoverableMessageException extends RuntimeException {

	/**
	 * Instantiates a new recoverable message exception.
	 */
	public RecoverableMessageException() {
		super();
	}

	/**
	 * Instantiates a new recoverable message exception.
	 * 
	 * @param cause the cause
	 */
	public RecoverableMessageException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new recoverable message exception.
	 * 
	 * @param message the message
	 */
	public RecoverableMessageException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new recoverable message exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public RecoverableMessageException(final String message, final Throwable cause) {
		super(message, cause);
	}

}

package com.elasticpath.repo.datasync.tools.exception;

/**
 * This runtime exception should be thrown if an exchange redelivery should not be attempted.
 */
@SuppressWarnings("serial")
public class UnrecoverableMessageException extends RuntimeException {

	/**
	 * Instantiates a new unrecoverable message exception.
	 */
	public UnrecoverableMessageException() {
		super();
	}

	/**
	 * Instantiates a new unrecoverable message exception.
	 * 
	 * @param message the message
	 */
	public UnrecoverableMessageException(final String message) {
		super(message);
	}

	/**
	 * Instantiates a new unrecoverable message exception.
	 * 
	 * @param message the message
	 * @param cause the cause
	 */
	public UnrecoverableMessageException(final String message, final Throwable cause) {
		super(message, cause);
	}

}

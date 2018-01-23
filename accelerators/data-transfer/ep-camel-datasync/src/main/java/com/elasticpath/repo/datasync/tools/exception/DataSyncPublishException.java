package com.elasticpath.repo.datasync.tools.exception;

/**
 * This exception is thrown when an unsuccessful change set DST publish has occurred.
 */
public class DataSyncPublishException extends Exception {

	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;

	/**
	 * Creates a new <code>DataSyncPublishException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>Exception</code>.
	 */
	public DataSyncPublishException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>DataSyncPublishException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>Exception</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>Exception</code>.
	 */
	public DataSyncPublishException(final String message, final Throwable cause) {
		super(message, cause);
	}
}

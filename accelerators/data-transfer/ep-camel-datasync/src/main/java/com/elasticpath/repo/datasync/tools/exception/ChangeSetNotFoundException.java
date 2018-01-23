package com.elasticpath.repo.datasync.tools.exception;

/**
 * This exception is thrown when required <code>ChangeSet</code> couldn't be found.
 */
public class ChangeSetNotFoundException extends RuntimeException {


	private static final long serialVersionUID = 6388579784885275513L;

	private final String changeSetGuid;
	
	/**
	 * @param message message describing error
	 * @param changeSetGuid GUID of the undefined change set.
	 */
	public ChangeSetNotFoundException(final String message, final String changeSetGuid) {
		super(message);
		this.changeSetGuid = changeSetGuid;
	}

	/**
	 * @param message message describing error
	 * @param cause exception causing this error
	 * @param changeSetGuid GUID of the undefined change set.
	 */
	public ChangeSetNotFoundException(final String message, final Throwable cause, final String changeSetGuid) {
		super(message, cause);
		this.changeSetGuid = changeSetGuid;
	}

	/**
	 * Returns change set GUID that cause an exception.
	 * 
	 * @return change set GUID.
	 */
	public String getChangeSetGuid() {
		return changeSetGuid;
	}
}

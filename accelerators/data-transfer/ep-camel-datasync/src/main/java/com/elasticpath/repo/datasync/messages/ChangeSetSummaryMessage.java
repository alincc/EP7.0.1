package com.elasticpath.repo.datasync.messages;

/**
 * <p>
 * Represents an changeset publishing summary message.
 * </p>
 */
public interface ChangeSetSummaryMessage {

	/**
	 * Returns true if publishing was successful.
	 * 
	 * @return if successful or not
	 */
	boolean isSuccess();

	/**
	 * Returns the text summary from the DST.
	 * 
	 * @return a summary message
	 */
	String getDSTSummary();
}

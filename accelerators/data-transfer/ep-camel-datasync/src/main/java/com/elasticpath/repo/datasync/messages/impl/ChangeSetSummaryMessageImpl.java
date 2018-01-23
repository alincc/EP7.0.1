package com.elasticpath.repo.datasync.messages.impl;

import com.elasticpath.repo.datasync.messages.ChangeSetSummaryMessage;

/**
 * Represents a changeset publishing summary message.
 */
public class ChangeSetSummaryMessageImpl implements ChangeSetSummaryMessage {

	private boolean wasSuccess;

	private String dstSummary;

	/**
	 * Creates a new Summary message with applicable status and summary message.
	 * 
	 * @param success whether the publish was sucessfull or not
	 * @param summary the message to show the user
	 */
	public ChangeSetSummaryMessageImpl(final boolean success, final String summary) {
		wasSuccess = success;
		dstSummary = summary;

	}

	@Override
	public boolean isSuccess() {
		return wasSuccess;
	}

	public void setIsSuccess(final boolean wasSucess) {
		this.wasSuccess = wasSucess;
	}

	@Override
	public String getDSTSummary() {
		return dstSummary;
	}

	public void setDstSummary(final String dstSummary) {
		this.dstSummary = dstSummary;
	}

}

package com.elasticpath.repo.datasync.tools.launcher;

/**
 * Helper class to hold the datasync tool messages.
 */
public class DatasyncSummary {

	private static final String NEW_LINE = "\n";
	
	private String changesetGuid;

	@SuppressWarnings("PMD.AvoidStringBufferField")
	private final StringBuilder syncSummary = new StringBuilder();
	
	@SuppressWarnings("PMD.AvoidStringBufferField")
	private final StringBuilder syncError = new StringBuilder();
	
	/**
	 * Creates a summary helper class for sync messages of a specific changeset.
	 *
	 * @param changesetGuid the changeset being synced
	 */
	public DatasyncSummary(final String changesetGuid) {	
		this.changesetGuid = changesetGuid;
	}
	
	/**
	 * Adds a line to the DST summary. 
	 *
	 * @param summaryLine the line to add
	 */
	public void addSummary(final String summaryLine) {
		syncSummary.append(summaryLine).append(NEW_LINE);
	}
	
	/**
	 * Adds an error line to the DST summary.
	 *
	 * @param errorLine the error to add
	 */
	public void addError(final String errorLine) {
		syncError.append(errorLine).append(NEW_LINE);
	}
	
	/**
	 * Gets the DST summary.
	 *
	 * @return String summary message
	 */
	public String getSummaryMessage() {
		return syncSummary.toString();
	}
	
	/**
	 * Gets the DST errors.
	 *
	 * @return String error message
	 */
	public String getErrorMessage() {
		return syncError.toString();
	}
	
	/**
	 * Checks if the summary has errors.
	 *
	 * @return true if the summary contains errors, false otherwise. 
	 */
	public boolean hasErrors() {
		return syncError.length() > 0;
	}

	/**
	 * Gets the changeset GUID.
	 *
	 * @return the GUID of the changeset being synced
	 */
	public String getChangesetGuid() {
		return changesetGuid;
	}

	/**
	 * Sets the changeset GUID of the changeset being synced.
	 *
	 * @param changesetGuid the GUID of the changeset
	 */
	public void setChangesetGuid(final String changesetGuid) {
		this.changesetGuid = changesetGuid;
	}
}

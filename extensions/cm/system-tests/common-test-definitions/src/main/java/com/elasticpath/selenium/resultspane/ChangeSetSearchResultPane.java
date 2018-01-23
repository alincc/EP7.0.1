package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateChangeSetDialog;
import com.elasticpath.selenium.editor.ChangeSetEditor;

/**
 * Change Set Search Results Pane.
 */
public class ChangeSetSearchResultPane extends AbstractPageObject {

	private static final String CREATE_BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages.ChangeSetsView_CreateChangeSetTooltip']";
	private static final String CHANGE_SET_SEARCH_RESULT_PARENT = "div[widget-id='Changeset View Table'][widget-type='Table'] ";
	private static final String CHANGE_SET_SEARCH_RESULT_COLUMN_CSS = CHANGE_SET_SEARCH_RESULT_PARENT + "div[column-id='%s']";
	private static final String CHANGE_SET_SEARCH_RESULT_ROW_CSS = CHANGE_SET_SEARCH_RESULT_PARENT + "div[row-id='%s'] ";
	private static final String LOCK_BUTTON_CSS = "div[widget-id='Locks a change set']";
	private static final String FINALIZE_BUTTON_CSS = "div[widget-id='Finalizes a change set and releases all objects in the change set']";
	private static final String NAME_COLUMN_NAME = "Name";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangeSetSearchResultPane(final WebDriver driver) {
		super(driver);
	}


	/**
	 * Clicks Create button.
	 *
	 * @return CreateChangeSetDialog
	 */
	public CreateChangeSetDialog clickCreateButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_BUTTON_CSS)).click();
		return new CreateChangeSetDialog(getDriver());
	}

	/**
	 * Clicks Lock button.
	 */
	public void clickLockButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(LOCK_BUTTON_CSS)).click();
	}

	/**
	 * Clicks Finalized button.
	 */
	public void clickFinalizedButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(FINALIZE_BUTTON_CSS)).click();
		new ConfirmDialog(getDriver()).clickOK();
	}

	/**
	 * Verifies change set in list.
	 *
	 * @param changeSetName String
	 */
	public void verifyChangeSetExists(final String changeSetName) {
		assertThat(selectItemInCenterPane(CHANGE_SET_SEARCH_RESULT_PARENT, CHANGE_SET_SEARCH_RESULT_COLUMN_CSS, changeSetName, NAME_COLUMN_NAME))
				.as("Expected Change Set does not exist in search result - " + changeSetName)
				.isTrue();
	}

	/**
	 * Selects change set in list.
	 *
	 * @param changeSetName String
	 */
	public void selectChangeSet(final String changeSetName) {
		verifyChangeSetExists(changeSetName);
	}

	/**
	 * Opens Change Set editor.
	 *
	 * @param changeSetName The change set name
	 * @return ChangeSetEditor
	 */
	public ChangeSetEditor openChangeSetEditor(final String changeSetName) {
		verifyChangeSetExists(changeSetName);
		doubleClick(getSelectedElement());
		return new ChangeSetEditor(getDriver());
	}


	/**
	 * Verifies change set status.
	 *
	 * @param changeSetName the change set name
	 * @param status        the change set status
	 */
	public void verifyChangeSetStatus(final String changeSetName, final String status) {
		selectChangeSet(changeSetName);
		assertThat(getDriver().findElement(By.cssSelector(String.format(CHANGE_SET_SEARCH_RESULT_ROW_CSS, changeSetName)
				+ String.format("div[column-id='%s']", status))).getText())
				.as("Change set status validation failed")
				.isEqualTo(status);

	}

}

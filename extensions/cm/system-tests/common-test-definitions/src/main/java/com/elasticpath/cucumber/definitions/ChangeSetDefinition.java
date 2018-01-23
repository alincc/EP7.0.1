package com.elasticpath.cucumber.definitions;

import java.util.UUID;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.CreateChangeSetDialog;
import com.elasticpath.selenium.editor.ChangeSetEditor;
import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;
import com.elasticpath.selenium.navigations.ChangeSet;
import com.elasticpath.selenium.resultspane.ChangeSetSearchResultPane;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.ChangeSetActionToolbar;

/**
 * Change set steps.
 */
public class ChangeSetDefinition {
	private final ChangeSetActionToolbar changeSetActionToolbar;
	private final ChangeSet changeSet;
	private final ActivityToolbar activityToolbar;
	private ChangeSetSearchResultPane changeSetSearchResultPane;
	private CreateChangeSetDialog createChangeSetDialog;
	private ChangeSetEditor changeSetEditor;
	private static final int UUID_END_INDEX = 5;
	private String changeSetName = "";


	/**
	 * Constructor.
	 */
	public ChangeSetDefinition() {
		changeSetActionToolbar = new ChangeSetActionToolbar(SeleniumDriverSetup.getDriver());
		changeSet = new ChangeSet(SeleniumDriverSetup.getDriver());
		activityToolbar = new ActivityToolbar(SeleniumDriverSetup.getDriver());
	}

	/**
	 * Select change set.
	 *
	 * @param changeSetName the change set name.
	 */
	@And("^I select change set (.+)$")
	public void selectChangeSet(final String changeSetName) {
		changeSetActionToolbar.selectChangeSet(changeSetName);
	}

	/**
	 * Clicks the Search button.
	 **/
	@And("^I click change set search button$")
	public void clickSearchButton() {
		changeSetSearchResultPane = changeSet.clickSearchButton();
	}

	/**
	 * Clicks the Create button.
	 **/
	@And("^I click create change set button$")
	public void clickCreateButton() {
		createChangeSetDialog = changeSetSearchResultPane.clickCreateButton();
	}

	/**
	 * Creates a new change set.
	 *
	 * @param changeSetName the change set name
	 **/
	@And("^I create a new change set (.+)$")
	public void clickCreateButton(final String changeSetName) {
		clickSearchButton();
		clickCreateButton();

		this.changeSetName = changeSetName + "_" + UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		createChangeSetDialog.enterChangeSetName(this.changeSetName);
		createChangeSetDialog.clickFinishButton();
		changeSetSearchResultPane.verifyChangeSetExists(this.changeSetName);
		changeSetEditor = changeSetSearchResultPane.openChangeSetEditor(this.changeSetName);
	}

	/**
	 * Selects newly created change set.
	 **/
	@And("^I select newly created change set$")
	public void selectNewChangeSet() {
		selectChangeSet(this.changeSetName);
	}

	/**
	 * Verifies object in change set.
	 **/
	@Then("^I should see (?:newly created|deleted) virtual catalog in the change set$")
	public void verifyVirtualCatalogInChangeSet() {
		changeSetActionToolbar.clickReloadActiveEditor();
		changeSetEditor.selectObjectsTab();
		changeSetEditor.verifyObjectExists(CatalogDefinition.getVirtualCatalogName(), "Object Name");
	}

	/**
	 * Locks and Finalizes change set.
	 */
	@After("@lockAndFinalize")
	public void lockAndFinalize() {
		activityToolbar.clickChangeSetButton();
		changeSetSearchResultPane.selectChangeSet(this.changeSetName);
		changeSetSearchResultPane.clickLockButton();
		changeSetSearchResultPane.selectChangeSet(this.changeSetName);
		changeSetSearchResultPane.clickFinalizedButton();
	}

	/**
	 * Locks and Finalizes latest change set.
	 **/
	@And("^I lock and finalize latest change set")
	public void lockAndFinalizeChangeSet() {
		lockAndFinalize();
	}

	/**
	 * Clicks add item to change set button.
	 **/
	@And("^I click add item to change set button")
	public void clickAddItemToChangeSetButton() {
		changeSetActionToolbar.clickAddItemToChangeSet();
	}

	/**
	 * Locks change set.
	 **/
	@When("^I lock the latest change set")
	public void lockNewChangeSet() {
		activityToolbar.clickChangeSetButton();
		changeSetSearchResultPane.selectChangeSet(this.changeSetName);
		changeSetSearchResultPane.clickLockButton();
	}

	/**
	 * Finalizes change set.
	 **/
	@When("^I finalize the latest change set")
	public void finalizeNewChangeSet() {
		activityToolbar.clickChangeSetButton();
		changeSetSearchResultPane.selectChangeSet(this.changeSetName);
		changeSetSearchResultPane.clickFinalizedButton();
	}

	/**
	 * Verifies change set status.
	 *
	 * @param changeSetStatus the change set name
	 **/
	@Then("^the change set status should be (.+)")
	public void verifyChangeSetStatus(final String changeSetStatus) {
		changeSetSearchResultPane.verifyChangeSetStatus(this.changeSetName, changeSetStatus);
	}

}

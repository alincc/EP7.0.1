package com.elasticpath.selenium.toolbars;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ChangePaginationSettingsDialog;

/**
 * Abstract Toolbar class. Common toolbar actions should be defined here.
 * Toolbar classes should extend this class for common methods.
 */
public abstract class AbstractToolbar extends AbstractPageObject {
//	TODO Save, Reload, Change Pagination

	/**
	 * CSS String of Save All button.
	 */
	protected static final String SAVE_ALL_BUTTON_CSS = "div[widget-id='Save All (Ctrl+Shift+S)']";
	private static final String CHANGE_PAGINATION_SETTINGS_CSS = "div[widget-id='Change Pagination Settings']";
	private static final String RELOAD_ACTIVE_EDITOR_BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.RefreshAction_Tooltip']";

	/**
	 * Constructor.
	 *
	 * @param driver the driver.
	 */
	public AbstractToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks on Save All button.
	 */
	public void saveAll() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SAVE_ALL_BUTTON_CSS)).click();

//		The wait will ensure the save action is finished.
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Go to Change Pagination Setting dialog.
	 *
	 * @return ChangePaginationSettingsDialog
	 */
	public ChangePaginationSettingsDialog changePaginationSetting() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CHANGE_PAGINATION_SETTINGS_CSS)).click();
		return new ChangePaginationSettingsDialog(getDriver());
	}

	/**
	 * Clicks Reloads Active Editor button.
	 */
	public void clickReloadActiveEditor() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(RELOAD_ACTIVE_EDITOR_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}
}

package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Change Pagination Settings Dialog.
 */
public class ChangePaginationSettingsDialog extends AbstractDialog {
	private static final String RESULTS_PER_PAGE_CSS = "div[widget-id=\"[CCombo]Results per Page:\"] input[type=\"text\"]";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangePaginationSettingsDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Changes the results per page.
	 *
	 * @param expectedResultsPerPage String
	 */
	public void changeResultsPerPage(final String expectedResultsPerPage) {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(RESULTS_PER_PAGE_CSS)).click();
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(RESULTS_PER_PAGE_CSS)).sendKeys(expectedResultsPerPage);
		clickSave();
		getWaitDriver().waitForPageLoad();
	}
}

package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Delete Confirm dialog.
 */
public class ConfirmDialog extends AbstractDialog {
	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ConfirmDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	* Clicks OK button.
	*/
	public void clickOKButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(OK_BUTTON_CSS)).click();
		waitTillElementDisappears(By.cssSelector(OK_BUTTON_CSS));
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks Cancel button.
	 */
	public void clickCancelButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CANCEL_BUTTON_CSS)).click();
		waitTillElementDisappears(By.cssSelector(CANCEL_BUTTON_CSS));
		getWaitDriver().waitForPageLoad();
	}
}

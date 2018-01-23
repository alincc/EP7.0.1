package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Abstract dialog class for common dialog actions. Dialog is a single prompt requesting user's confirmation.
 * Dialog classes should extend this class for common methods.
 */
public abstract class AbstractDialog extends AbstractPageObject {

	/**
	 * Cancel button css selector.
	 */
	protected static final String CANCEL_BUTTON_CSS = "div[widget-id='Cancel'][seeable='true']";
	/**
	 * OK button css selector.
	 */
	protected static final String OK_BUTTON_CSS = "div[widget-id='OK'][seeable='true']";
	private static final String SAVE_BUTTON_CSS = "div[widget-id='Save']";


	/**
	 * constructor.
	 *
	 * @param driver the driver.
	 */
	public AbstractDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks cancel.
	 */
	public void clickCancel() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CANCEL_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks save.
	 */
	public void clickSave() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SAVE_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks OK.
	 */
	public void clickOK() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(OK_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

}

package com.elasticpath.selenium.wizards;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Abstract Wizard class for common wizard actions. Wizard is a dialog that contains a series of configurations in sequential order.
 * Wizard classes should extend this class for common methods.
 */
public abstract class AbstractWizard extends AbstractPageObject {

	private static final String NEXT_BUTTON = "div[widget-id='Next >']";
	private static final String CANCEL_BUTTON = "div[widget-id='Cancel']";
	private static final String FINISH_BUTTON = "div[widget-id='Finish']";

	/**
	 * Constructor.
	 *
	 * @param driver the driver.
	 */
	public AbstractWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Next.
	 */
	public void clickNextInDialog() {
		getWaitDriver().waitForElementToBeInteractable(NEXT_BUTTON);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(NEXT_BUTTON)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks Cancel.
	 */
	public void clickCancel() {
		getWaitDriver().waitForElementToBeInteractable(CANCEL_BUTTON);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CANCEL_BUTTON)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks Finish.
	 */
	public void clickFinish() {
		getWaitDriver().waitForElementToBeInteractable(FINISH_BUTTON);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(FINISH_BUTTON)).click();
		waitTillElementDisappears(By.cssSelector(FINISH_BUTTON));
		getWaitDriver().waitForPageLoad();
	}
}

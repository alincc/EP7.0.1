package com.elasticpath.selenium.toolbars;

import com.elasticpath.selenium.editor.PriceListEditor;
import com.elasticpath.selenium.wizards.CreatePriceListAssignmentWizard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Price List Toolbar.
 */
public class PriceListActionToolbar extends AbstractToolbar {

	private static final String CREATE_PRICE_LIST_ASSIGNMENT_BUTTON_CSS =
			"[widget-id='Create Price List Assignment']"; //TODO to be changed
	private static final String CREATE_PRICE_LIST_BUTTON_CSS =
			"[widget-id='Create Price List']"; //TODO to be changed

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceListActionToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Create Price List Assignment icon.
	 *
	 * @return CreatePriceListAssignmentWizard
	 */
	public CreatePriceListAssignmentWizard clickCreatePriceListAssignment() {
		getWaitDriver().waitForElementToBeInteractable(CREATE_PRICE_LIST_ASSIGNMENT_BUTTON_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_PRICE_LIST_ASSIGNMENT_BUTTON_CSS)).click();
		return new CreatePriceListAssignmentWizard(getDriver());
	}

	/**
	 * Clicks Create Price List.
	 *
	 * @return PriceListEditor
	 */
	public PriceListEditor clickCreatePriceList() {
		getWaitDriver().waitForElementToBeInteractable(CREATE_PRICE_LIST_BUTTON_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_PRICE_LIST_BUTTON_CSS)).click();
		return new PriceListEditor(getDriver());
	}

	/**
	 * Verifies Create Price List button is present.
	 */
	public void verifyCreatePriceListButtonIsPresent() {
		getWaitDriver().waitForPageLoad();
		assertThat(isElementPresent(By.cssSelector(CREATE_PRICE_LIST_BUTTON_CSS)))
				.as("Unable to find Create Price List button")
				.isTrue();
	}

}

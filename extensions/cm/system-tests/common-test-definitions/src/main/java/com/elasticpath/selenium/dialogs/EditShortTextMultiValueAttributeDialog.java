package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Edit Attribute Value Dialog.
 */
public class EditShortTextMultiValueAttributeDialog extends AbstractDialog {

	private static final String PARENT_SHORT_TEXT_MULTI_VALUE_CSS = "div[widget-id='Edit Short Text (Multi Value)'] ";
	private static final String ADD_VALUE_BUTTON_CSS = PARENT_SHORT_TEXT_MULTI_VALUE_CSS + "div[widget-id='Add Value...']";
	private static final String OK_BUTTON_CSS = PARENT_SHORT_TEXT_MULTI_VALUE_CSS + "div[widget-id='Save']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditShortTextMultiValueAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks add value button.
	 *
	 * @return the dialog.
	 */
	public AddShortTextAttributeDialog clickAddValueButton() {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(ADD_VALUE_BUTTON_CSS)).click();
		return new AddShortTextAttributeDialog(getDriver());
	}

	/**
	 * Click ok button.
	 */
	public void clickOKButton() {
		getDriver().findElement(By.cssSelector(OK_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}
}

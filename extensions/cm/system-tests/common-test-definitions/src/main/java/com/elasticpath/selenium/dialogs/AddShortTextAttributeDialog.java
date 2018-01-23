package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Short Text Attribute Value Dialog.
 */
public class AddShortTextAttributeDialog extends AbstractDialog {

	private static final String PARENT_ADD_SHORT_TEXT_CSS = "div[widget-id='Add Short Text Value'][widget-type='Shell'] ";
	private static final String TEXTAREA_CSS = PARENT_ADD_SHORT_TEXT_CSS + "textarea";
	private static final String OK_BUTTON_CSS = PARENT_ADD_SHORT_TEXT_CSS + "div[widget-id='OK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddShortTextAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs short text attribute value.
	 *
	 * @param shortText the short text.
	 */
	public void enterShortTextValue(final String shortText) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(TEXTAREA_CSS)), shortText);
	}

	/**
	 * Click ok button.
	 */
	public void clickOKButton() {
		getDriver().findElement(By.cssSelector(OK_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}
}

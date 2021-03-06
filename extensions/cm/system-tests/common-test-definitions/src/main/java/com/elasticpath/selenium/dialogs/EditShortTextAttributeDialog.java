package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Edit Short Text Attribute Value Dialog.
 */
public class EditShortTextAttributeDialog extends AbstractDialog {

	private static final String PARENT_EDIT_SHORT_TEXT_CSS = "div[widget-id='Edit Short Text'][widget-type='Shell'] ";
	private static final String TEXTAREA_CSS = PARENT_EDIT_SHORT_TEXT_CSS + "textarea";
	private static final String OK_BUTTON_CSS = PARENT_EDIT_SHORT_TEXT_CSS + "div[widget-id='OK']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditShortTextAttributeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs short text attribute value.
	 *
	 * @param shortText String
	 */
	public void enterShortTextValue(final String shortText) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(TEXTAREA_CSS)), shortText);
	}

	/**
	 * Clicks OK button.
	 */
	public void clickOKButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(OK_BUTTON_CSS)).click();
	}
}

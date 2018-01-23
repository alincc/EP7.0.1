package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;

/**
 * Category Editor.
 */
public class CategoryEditor extends AbstractPageObject {

	private static final String ATTRIBUTE_PARENT_CSS = "div[widget-id='Attributes'][widget-type='Table'] ";
	private static final String ATTRIBUTE_COLUMN_CSS = ATTRIBUTE_PARENT_CSS + "div[column-id='%s']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CategoryEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks on tab to select it.
	 *
	 * @param tabName the tab name.
	 */
	public void selectTab(final String tabName) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, tabName))).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Verifies attribute value.
	 *
	 * @param attributValue the attribute value.
	 */
	public void verifyAttributeValue(final String attributValue) {
		assertThat(selectItemInEditorPane(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributValue, ""))
				.as("Unable to find attribute value - " + attributValue)
				.isTrue();
		new ConfirmDialog(getDriver()).clickCancelButton();
	}
}

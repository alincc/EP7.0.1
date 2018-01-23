package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Edit Global Attributes Dialog.
 */
public class EditGlobalAttributesDialog extends AbstractDialog {

	private static final String EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.AddEditGlobalAttributesDialog_WindowTitle'] ";
	private static final String ADD_ATTRIBUTE_BUTTON_CSS
			= EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_Add']";
	private static final String REMOVE_ATTRIBUTE_BUTTON_CSS
			= EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_Remove']";
	private static final String SAVE_BUTTON_CSS = EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[widget-id='Save']";
	private static final String GLOBAL_ATTRIBUTES_TABLE_CSS = EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS + "div[widget-id='Global "
			+ "Attributes'][widget-type='Table'] ";
	private static final String GLOBAL_ATTRIBUTES_LIST_CSS = GLOBAL_ATTRIBUTES_TABLE_CSS + "div[column-id='%s']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public EditGlobalAttributesDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Click add attribute button.
	 *
	 * @return the Add attribute dialog.
	 */
	public AddAttributeDialog clickAddAttributeButton() {
		getDriver().findElement(By.cssSelector(ADD_ATTRIBUTE_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new AddAttributeDialog(getDriver());
	}

	/**
	 * Clicks Remove Attribute button.
	 */
	public void clickRemoveAttributeButton() {
		getDriver().findElement(By.cssSelector(REMOVE_ATTRIBUTE_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks Save button.
	 */
	public void clickSaveButton() {
		getDriver().findElement(By.cssSelector(SAVE_BUTTON_CSS)).click();
		waitTillElementDisappears(By.cssSelector(EDIT_GLOBAL_ATTRIBUTES_PARENT_CSS));
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Selects global attribute row.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void selectGlobalAttributeRow(final String attributeValue) {
		assertThat(selectItemInDialog(GLOBAL_ATTRIBUTES_TABLE_CSS, GLOBAL_ATTRIBUTES_LIST_CSS, attributeValue, "Name"))
				.as("Unable to find global attribute value - " + attributeValue)
				.isTrue();
	}

	/**
	 * Verifies global attribute value.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void verifyGlobalAttributeValue(final String attributeValue) {
		assertThat(selectItemInDialog(GLOBAL_ATTRIBUTES_TABLE_CSS, GLOBAL_ATTRIBUTES_LIST_CSS, attributeValue, "Name"))
				.as("Unable to find global attribute value - " + attributeValue)
				.isTrue();
	}

	/**
	 * Verifies global attribute value is not present.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void verifyGlobalAttributeValueIsNotInList(final String attributeValue) {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(selectItemInDialog(GLOBAL_ATTRIBUTES_TABLE_CSS, GLOBAL_ATTRIBUTES_LIST_CSS, attributeValue, "Name"))
				.as("Global attribute is still in list - " + attributeValue)
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Delete global attribute.
	 */
	public void deleteGlobalAttribute() {
		clickRemoveAttributeButton();
		new ConfirmDialog(getDriver()).clickOKButton();
	}
}

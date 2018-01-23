package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Add Category Type Dialog.
 */
public class AddCategoryTypeDialog extends AbstractDialog {

	private static final String ADD_CATEGORY_TYPE_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CategoryTypeAddEditDialog_AddWindowTitle'][widget-type='Shell'] ";
	private static final String CATEGORY_TYPE_NAME_INPUT_CSS = ADD_CATEGORY_TYPE_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CategoryTypeAddEditDialog_Name'] input";
	private static final String MOVE_RIGHT_BUTTON_CSS = ADD_CATEGORY_TYPE_PARENT_CSS + "div[widget-id='>']";
	private static final String AVAILABLE_ATTRIBUTES_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CategoryTypeAddEditDialog_AvailableAttributes']"
			+ "[widget-type='Table'] ";
	private static final String AVAILABLE_ATTRIBUTES_COLUMN_CSS = AVAILABLE_ATTRIBUTES_PARENT_CSS + "div[column-id='%s']";
	private static final String ASSIGNED_ATTRIBUTES_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CategoryTypeAddEditDialog_AssignedAttributes']"
			+ "[widget-type='Table'] ";
	private static final String ASSIGNED_ATTRIBUTES_COLUMN_CSS = ASSIGNED_ATTRIBUTES_PARENT_CSS + "div[column-id='%s']";
	private static final String ADD_BUTTON_CSS = ADD_CATEGORY_TYPE_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.AbstractEpDialog_ButtonSave'][seeable='true']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public AddCategoryTypeDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs category type name.
	 *
	 * @param categoryTypeName the category type name
	 */
	public void enterCategoryTypeName(final String categoryTypeName) {
		clearAndType(getDriver().findElement(By.cssSelector(CATEGORY_TYPE_NAME_INPUT_CSS)), categoryTypeName);
	}

	/**
	 * Clicks '>' button.
	 */
	public void clickMoveRightButton() {
		getDriver().findElement(By.cssSelector(MOVE_RIGHT_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Selects attribute from available attributes list.
	 *
	 * @param attribute the attribute
	 */
	public void selectAvailableAttribute(final String attribute) {
		assertThat(selectItemInDialog(AVAILABLE_ATTRIBUTES_PARENT_CSS, AVAILABLE_ATTRIBUTES_COLUMN_CSS, attribute, ""))
				.as("Unable to find available attribute - " + attribute)
				.isTrue();
	}

	/**
	 * Verifies assigned attributes in list.
	 *
	 * @param attribute the language.
	 */
	public void verifyAssignedAttribute(final String attribute) {
		assertThat(selectItemInDialog(ASSIGNED_ATTRIBUTES_PARENT_CSS, ASSIGNED_ATTRIBUTES_COLUMN_CSS, attribute, ""))
				.as("Unable to find assigned attribute - " + attribute)
				.isTrue();
		getSelectedElement().click();
	}

	/**
	 * Clicks add button.
	 */
	public void clickAddButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(ADD_BUTTON_CSS)).click();
		waitTillElementDisappears(By.cssSelector(ADD_CATEGORY_TYPE_PARENT_CSS));
		getWaitDriver().waitForPageLoad();
	}

}

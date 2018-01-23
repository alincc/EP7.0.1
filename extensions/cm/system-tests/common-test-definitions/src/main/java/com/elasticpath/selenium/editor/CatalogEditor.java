package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.AddAttributeDialog;
import com.elasticpath.selenium.dialogs.AddCategoryTypeDialog;
import com.elasticpath.selenium.dialogs.EditAttributeDialog;
import com.elasticpath.selenium.dialogs.EditCategoryTypeDialog;

/**
 * Catalog Editor.
 */
public class CatalogEditor extends AbstractPageObject {

	private static final String ATTRIBUTE_PARENT_CSS = "div[widget-id='Catalog Attributes'][widget-type='Table'][seeable='true'] ";
	private static final String ATTRIBUTE_COLUMN_CSS = ATTRIBUTE_PARENT_CSS + "div[column-id='%s']";
	private static final String TAB_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.Catalog%sPage_Title'][seeable='true']";
	private static final String BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.core.CoreMessages.Button_%s'][seeable='true']";
	private static final String CATEGORY_TYPE_PARENT_CSS = "div[widget-id='Catalog Category Types'][widget-type='Table'][seeable='true'] ";
	private static final String CATEGORY_TYPE_COLUMN_CSS = CATEGORY_TYPE_PARENT_CSS + "div[column-id='%s']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CatalogEditor(final WebDriver driver) {
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
	 * Verifies catalog attribute value.
	 *
	 * @param attributValue the attribute value.
	 */
	public void verifyCatalogAttributeValue(final String attributValue) {
		assertThat(selectItemInEditorPaneWithScrollBar(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributValue))
				.as("Unable to find attribute value - " + attributValue)
				.isTrue();
	}

	/**
	 * Verify catalog attribute is deleted.
	 *
	 * @param attributeValue the attribute value.
	 */
	public void verifyCatalogAttributeDelete(final String attributeValue) {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(selectItemInEditorPaneWithScrollBar(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributeValue))
				.as("Delete failed, attribute is still in the list - " + attributeValue)
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Selects catalog attribute value.
	 *
	 * @param attributValue the attribute value.
	 */
	public void selectCatalogAttributeValue(final String attributValue) {
		assertThat(selectItemInEditorPaneWithScrollBar(ATTRIBUTE_PARENT_CSS, ATTRIBUTE_COLUMN_CSS, attributValue))
				.as("Unable to find attribute value - " + attributValue)
				.isTrue();
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonName the button name
	 */
	public void clickButton(final String buttonName) {
		getWaitDriver().waitForElementToBeInteractable(String.format(BUTTON_CSS, buttonName));
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(BUTTON_CSS, buttonName))).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks add attribute button.
	 *
	 * @return AddAttributeDialog
	 */
	public AddAttributeDialog clickAddAttributeButton() {
		clickButton("Add");
		return new AddAttributeDialog(getDriver());
	}

	/**
	 * Clicks edit attribute button.
	 *
	 * @return EditAttributeDialog
	 */
	public EditAttributeDialog clickEditAttributeButton() {
		clickButton("Edit");
		return new EditAttributeDialog(getDriver());
	}

	/**
	 * Clicks remove attribute button.
	 */
	public void clickRemoveAttributeButton() {
		clickButton("Remove");
	}

	/**
	 * Clicks add category type button.
	 *
	 * @return AddCategoryTypeDialog
	 */
	public AddCategoryTypeDialog clickAddCategoryTypeButton() {
		clickButton("Add");
		return new AddCategoryTypeDialog(getDriver());
	}

	/**
	 * Verifies category type.
	 *
	 * @param categoryType the category type
	 */
	public void verifyCategoryType(final String categoryType) {
		assertThat(selectItemInEditorPaneWithScrollBar(CATEGORY_TYPE_PARENT_CSS, CATEGORY_TYPE_COLUMN_CSS, categoryType))
				.as("Unable to find category type - " + categoryType)
				.isTrue();
	}

	/**
	 * Selects category type.
	 *
	 * @param categoryType the category type
	 */
	public void selectCategoryType(final String categoryType) {
		verifyCategoryType(categoryType);
	}

	/**
	 * Clicks edit category type button.
	 *
	 * @return EditCategoryTypeDialog
	 */
	public EditCategoryTypeDialog clickEditCategoryTypeButton() {
		clickButton("Edit");
		return new EditCategoryTypeDialog(getDriver());
	}

	/**
	 * Clicks remove category type button.
	 */
	public void clickRemoveCategoryTypeButton() {
		clickButton("Remove");
	}

	/**
	 * Verify category type is deleted.
	 *
	 * @param categoryType the category type
	 */
	public void verifyCategoryTypeDelete(final String categoryType) {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(selectItemInEditorPaneWithScrollBar(CATEGORY_TYPE_PARENT_CSS, CATEGORY_TYPE_PARENT_CSS, categoryType))
				.as("Delete failed, category type is still in the list - " + categoryType)
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

}

package com.elasticpath.selenium.toolbars;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.CreateCatalogDialog;
import com.elasticpath.selenium.dialogs.CreateVirtualCatalogDialog;
import com.elasticpath.selenium.dialogs.EditGlobalAttributesDialog;

/**
 * Catalog Management Toolbar.
 */
public class CatalogManagementActionToolbar extends AbstractToolbar {

	private static final String APPEARANCE_ID_CSS = "[appearance-id='toolbar-button']";
	private static final String CREATE_CATALOG_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Create Catalog']"; //TODO to be changed
	private static final String CREATE_VIRTUAL_CATALOG_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Create Virtual Catalog']"; //TODO to be changed
	private static final String EDIT_GLOBAL_ATTRIBUTE_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Edit Global Attributes']"; //TODO to be changed


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CatalogManagementActionToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Create Catalog button.
	 *
	 * @return CreateCatalogDialog
	 */
	public CreateCatalogDialog clickCreateCatalogButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_CATALOG_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new CreateCatalogDialog(getDriver());
	}

	/**
	 * Clicks Create Virtual Catalog button.
	 *
	 * @return CreateVirtualCatalogDialog
	 */
	public CreateVirtualCatalogDialog clickCreateVirtualCatalogButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_VIRTUAL_CATALOG_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new CreateVirtualCatalogDialog(getDriver());
	}

	/**
	 * Clicks Edit Global Attribute button.
	 *
	 * @return EditGlobalAttributesDialog
	 */
	public EditGlobalAttributesDialog clickEditGlobalAttributesButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(EDIT_GLOBAL_ATTRIBUTE_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new EditGlobalAttributesDialog(getDriver());
	}

	/**
	 * Verifies Create Catalog button is present.
	 */
	public void verifyCreateCatalogButtonIsPresent() {
		getWaitDriver().waitForPageLoad();
		assertThat(isElementPresent(By.cssSelector(CREATE_CATALOG_BUTTON_CSS)))
				.as("Unable to find Crate Catalog button")
				.isTrue();
	}

}

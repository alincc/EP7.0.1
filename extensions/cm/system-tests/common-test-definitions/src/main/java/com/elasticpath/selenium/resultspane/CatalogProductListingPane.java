package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.wizards.CreateBundleWizard;
import com.elasticpath.selenium.wizards.CreateProductWizard;

/**
 * Catalog product listing pane.
 */
public class CatalogProductListingPane extends AbstractPageObject {

	private static final String PRODUCT_LISTING_TOOLBAR_PARENT_CSS = "div[widget-id='ID NOT IMPLEMENTED'][widget-type='ToolBar'] ";
	private static final String CREATE_PRODUCT_BUTTON_CSS = PRODUCT_LISTING_TOOLBAR_PARENT_CSS + "div[widget-id='Create "
			+ "Product'][widget-type='ToolItem']";
	private static final String CREATE_BUNDLE_BUTTON_CSS = PRODUCT_LISTING_TOOLBAR_PARENT_CSS + "div[widget-id='Create "
			+ "Bundle'][widget-type='ToolItem']";
	private static final String PRODUCT_TABLE_PARENT_CSS = "div[widget-id='Browse Product List'][widget-type='Table'] ";
	private static final String PRODUCT_ROW_COLUMN_CSS = PRODUCT_TABLE_PARENT_CSS + "div[column-id='%s']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CatalogProductListingPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if product exists.
	 *
	 * @param expectedProductName the expected product name.
	 */
	public void verifyProductNameExists(final String expectedProductName) {
		assertThat(selectItemInCenterPane(PRODUCT_TABLE_PARENT_CSS, PRODUCT_ROW_COLUMN_CSS, expectedProductName, "Product Name"))
				.as("Expected Product does not exist in product listing - " + expectedProductName)
				.isTrue();
	}

	/**
	 * Clicks Create Product icon.
	 *
	 * @return the CreateProductWizard.
	 */
	public CreateProductWizard clickCreateProductButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_PRODUCT_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new CreateProductWizard(getDriver());
	}

	/**
	 * Clicks Create Bundle icon.
	 *
	 * @return the CreateBundleWizard.
	 */
	public CreateBundleWizard clickCreateBundleButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_BUNDLE_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new CreateBundleWizard(getDriver());
	}

	/**
	 * Close the Catalog product list ipane.
	 * @param tabName The tab name to close.
	 */
	public void close(final String tabName) {
		getWaitDriver().waitForElementToBeInteractable("[widget-id='" + tabName + "'][active-tab='true'][appearance-id='ctab-item']");
			String closeCSS = "[widget-id='" + tabName + "'][active-tab='true'][appearance-id='ctab-item'] :nth-toolTipTextChild(3)";
			WebElement element = getWaitDriver().waitForElementToBeVisible(By.cssSelector(closeCSS));
			element.click();
		}
}

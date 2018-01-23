package com.elasticpath.selenium.navigations;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.editor.InventoryEditor;

/**
 * Shipping/Receiving.
 */
public class ShippingReceiving extends AbstractNavigation {

	//TODO use proper ID
	private static final String SKU_CODE_INPUT_CSS = "div[widget-id='Retrieve SKU Inventory'] div[widget-id='SKU Code'] > input";
	private static final String INVENTORY_RETRIEVE_BUTTON_CSS = "div[widget-id='Retrieve'][widget-type='Button']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ShippingReceiving(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs sku code.
	 *
	 * @param skuCode the sku code.
	 */
	public void enterSkuCode(final String skuCode) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SKU_CODE_INPUT_CSS)).click();
		clearAndType(getDriver().findElement(By.cssSelector(SKU_CODE_INPUT_CSS)), skuCode);
	}

	/**
	 * Clicks on Retrieve button.
	 *
	 * @return InventoryEditor
	 */
	public InventoryEditor clickRetrieveButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(INVENTORY_RETRIEVE_BUTTON_CSS)).click();
		return new InventoryEditor(getDriver());
	}
}

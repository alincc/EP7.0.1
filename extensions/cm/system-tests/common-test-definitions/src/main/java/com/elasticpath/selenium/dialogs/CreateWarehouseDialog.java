package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Warehouse Dialog.
 */
public class CreateWarehouseDialog extends AbstractDialog {

	private static final String CREATE_WAREHOUSE_PARENT_CSS = "div[widget-id='Create Warehouse'][widget-type='Shell'] ";
	private static final String WAREHOUSE_CODE_INPUT_CSS = CREATE_WAREHOUSE_PARENT_CSS + "div[widget-id='Warehouse Code'] input";
	private static final String WAREHOUSE_NAME_INPUT_CSS = CREATE_WAREHOUSE_PARENT_CSS + "div[widget-id='Warehouse Name'] input";
	private static final String WAREHOUSE_ADDRESS_LINE_1_INPUT_CSS = CREATE_WAREHOUSE_PARENT_CSS + "div[widget-id='Address Line 1'] input";
	private static final String WAREHOUSE_CITY_INPUT_CSS = CREATE_WAREHOUSE_PARENT_CSS + "div[widget-id='City'] input";
	private static final String STATE_COMBO_CSS = CREATE_WAREHOUSE_PARENT_CSS + "div[widget-id='State/Province/Region'][widget-type='CCombo']";
	private static final String ZIP_INPUT_CSS = CREATE_WAREHOUSE_PARENT_CSS + "div[widget-id='Zip/Postal Code'] input";
	private static final String COUNTRY_COMBO_CSS = CREATE_WAREHOUSE_PARENT_CSS + "div[widget-id='Country'][widget-type='CCombo']";
	private static final String SAVE_BUTTON_CSS = CREATE_WAREHOUSE_PARENT_CSS + "div[widget-id='Save'][seeable='true']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateWarehouseDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs warehouse code.
	 *
	 * @param warehouseCode String
	 */
	public void enterWarehouseCode(final String warehouseCode) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(WAREHOUSE_CODE_INPUT_CSS)), warehouseCode);
	}

	/**
	 * Inputs warehouse name.
	 *
	 * @param warehouseName String
	 */
	public void enterWarehouseName(final String warehouseName) {
		clearAndType(getDriver().findElement(By.cssSelector(WAREHOUSE_NAME_INPUT_CSS)), warehouseName);
	}

	/**
	 * Inputs address line 1.
	 *
	 * @param addressLine1 String
	 */
	public void enterAddressLine1(final String addressLine1) {
		clearAndType(getDriver().findElement(By.cssSelector(WAREHOUSE_ADDRESS_LINE_1_INPUT_CSS)), addressLine1);
	}

	/**
	 * Inputs city.
	 *
	 * @param city String
	 */
	public void enterCity(final String city) {
		clearAndType(getDriver().findElement(By.cssSelector(WAREHOUSE_CITY_INPUT_CSS)), city);
	}

	/**
	 * Selects state in combo box.
	 *
	 * @param state String
	 */
	public void selectState(final String state) {
		assertThat(selectComboBoxItem(STATE_COMBO_CSS, state))
				.as("Unable to find state - " + state)
				.isTrue();
	}

	/**
	 * Inputs zip.
	 *
	 * @param zip String
	 */
	public void enterZip(final String zip) {
		clearAndType(getDriver().findElement(By.cssSelector(ZIP_INPUT_CSS)), zip);
	}

	/**
	 * Selects country in combo box.
	 *
	 * @param country String
	 */
	public void selectCountry(final String country) {
		assertThat(selectComboBoxItem(COUNTRY_COMBO_CSS, country))
				.as("Unable to find country - " + country)
				.isTrue();
	}

	/**
	 * Clicks save button.
	 */
	public void clickSaveButton() {
		getDriver().findElement(By.cssSelector(SAVE_BUTTON_CSS)).click();
		waitTillElementDisappears(By.cssSelector(CREATE_WAREHOUSE_PARENT_CSS));
		getWaitDriver().waitForPageLoad();
	}
}

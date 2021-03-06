package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Catalog Dialog.
 */
public class CreateShippingServiceLevelDialog extends AbstractDialog {

	private static final String CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS = "div[widget-id='Create Shipping Service Level'][widget-type='Shell'] ";
	private static final String STORE_COMBO_CSS = CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS + "div[widget-id='Store'][widget-type='CCombo']";
	private static final String SHIPPING_REGION_COMBO_CSS = CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS + "div[widget-id='Shipping "
			+ "Region'][widget-type='CCombo']";
	private static final String CARRIER_COMBO_CSS = CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS + "div[widget-id='Carrier'][widget-type='CCombo']";
	private static final String UNIQUE_CODE_INPUT_CSS = CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS + "div[widget-id='Unique Code'] > input";
	private static final String NAME_INPUT_CSS = CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS + "div[widget-id=''] > input";
	private static final String PROPERTIES_TABLE_PARENT_CSS = CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS + "div[widget-id='Property Table'] ";
	private static final String PROPERTY_VALUE_INPUT_CSS = PROPERTIES_TABLE_PARENT_CSS + "input";
	private static final String PROPERTY_VALUE_DIV_XPATH = "//div[@widget-id='Property Table'] //div[text()='<Enter a value>']";
	private static final String SAVE_BUTTON_CSS = CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS + "div[widget-id='Save']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateShippingServiceLevelDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects store.
	 *
	 * @param storeName the store name.
	 */
	public void selectStore(final String storeName) {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(STORE_COMBO_CSS));
		assertThat(selectComboBoxItem(STORE_COMBO_CSS, storeName))
				.as("Unable to find store - " + storeName)
				.isTrue();
	}

	/**
	 * Selects shipping region.
	 *
	 * @param shippingRegion the shipping region.
	 */
	public void selectShippingRegion(final String shippingRegion) {
		assertThat(selectComboBoxItem(SHIPPING_REGION_COMBO_CSS, shippingRegion))
				.as("Unable to find shipping region - " + shippingRegion)
				.isTrue();
	}

	/**
	 * Selects carrier.
	 *
	 * @param carrier the carrier.
	 */
	public void selectCarrier(final String carrier) {
		assertThat(selectComboBoxItem(CARRIER_COMBO_CSS, carrier))
				.as("Unable to find carrier - " + carrier)
				.isTrue();
	}

	/**
	 * Inputs unique code.
	 *
	 * @param uniqueCode the unique code.
	 */
	public void enterUniqueCode(final String uniqueCode) {
		getDriver().findElement(By.cssSelector(UNIQUE_CODE_INPUT_CSS)).click();
		clearAndType(getDriver().findElement(By.cssSelector(UNIQUE_CODE_INPUT_CSS)), uniqueCode);
	}

	/**
	 * Inputs shipping service level name.
	 *
	 * @param name the name.
	 */
	public void enterName(final String name) {
		clearAndType(getDriver().findElement(By.cssSelector(NAME_INPUT_CSS)), name);
	}

	/**
	 * Inputs property value.
	 *
	 * @param value the value.
	 */
	public void enterPropertyValue(final String value) {
		getWaitDriver().waitForElementToBeClickable(By.xpath(PROPERTY_VALUE_DIV_XPATH)).click();
		clearAndType(getDriver().findElement(By.cssSelector(PROPERTY_VALUE_INPUT_CSS)), value);
	}

	/**
	 * Clicks save button.
	 */
	public void clickSaveButton() {
		getDriver().findElement(By.cssSelector(SAVE_BUTTON_CSS)).click();
		waitTillElementDisappears(By.cssSelector(CREATE_SHIPPING_SERVICE_LEVEL_PARENT_CSS));
	}

}

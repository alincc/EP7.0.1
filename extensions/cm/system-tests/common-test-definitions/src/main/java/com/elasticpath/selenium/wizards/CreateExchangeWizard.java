package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.SelectASkuDialog;

/**
 * Create Exchange Wizard.
 */
public class CreateExchangeWizard extends AbstractWizard {

	private static final String CREATE_EXCHANGE_PARENT_CSS = "div[widget-id='Create Exchange'][widget-type='Shell'] ";
	private static final String EXCHANGE_RETURN_PARENT_TABLE_CSS = CREATE_EXCHANGE_PARENT_CSS + "div[widget-id='Return Subject Page "
			+ "Table'][widget-type='Table'] ";
	private static final String EXCHANGE_RETURN_COLUMN_CSS = EXCHANGE_RETURN_PARENT_TABLE_CSS + "div[row-id='%s'] div[column-id='0']";
	private static final String EXCHANGE_RETURN_COLUMN_INPUT_CSS = EXCHANGE_RETURN_PARENT_TABLE_CSS + " input:not([readonly])";
	private static final String EXCHANGE_ORDER_PARENT_TABLE_CSS = CREATE_EXCHANGE_PARENT_CSS + "div[widget-id='Exchange Order Items "
			+ "Table'][widget-type='Table'] ";
	private static final String EXCHANGE_ORDER_COLUMN_CSS = EXCHANGE_ORDER_PARENT_TABLE_CSS + "div[column-id='%s']";
	private static final String ADD_ITEM_BUTTON_CSS = CREATE_EXCHANGE_PARENT_CSS + "div[widget-id='Add Item...'][seeable='true']";
	private static final String REMOVE_ITEM_BUTTON_CSS = CREATE_EXCHANGE_PARENT_CSS + "div[widget-id='Remove Item...'][seeable='true']";
	private static final String SHIPPING_ADDRESS_COMBO_CSS = CREATE_EXCHANGE_PARENT_CSS + "div[widget-id='Shipping Address'][widget-type='CCombo']";
	private static final String SHIPPING_METHOD_COMBO_CSS = CREATE_EXCHANGE_PARENT_CSS + "div[widget-id='Shipping Method'][widget-type='CCombo']";
	private static final String PAYMENT_SOURCE_COMBO_CSS = CREATE_EXCHANGE_PARENT_CSS + "div[widget-id='Payment Source'][widget-type='CCombo']";
	//TODO need widget-id
	private static final String REQUIRED_RETURN_CHECKBOX_CSS = CREATE_EXCHANGE_PARENT_CSS + "div[appearance-id='check-box']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateExchangeWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Enters exchange return quantity.
	 *
	 * @param quantity int
	 * @param skuCode  String
	 */
	public void enterExchangeReturnQuantity(final int quantity, final String skuCode) {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(String.format(EXCHANGE_RETURN_COLUMN_CSS, skuCode))).click();
		clearAndType(getDriver().findElement(By.cssSelector(EXCHANGE_RETURN_COLUMN_INPUT_CSS)), String.valueOf(quantity));
	}

	/**
	 * Clicks Add Item button.
	 *
	 * @return SelectASkuDialog
	 */
	public SelectASkuDialog clickAddItemButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(ADD_ITEM_BUTTON_CSS)).click();
		return new SelectASkuDialog(getDriver());
	}

	/**
	 * Clicks Remove Item button.
	 */
	public void clickRemoveItemButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(REMOVE_ITEM_BUTTON_CSS)).click();
		new ConfirmDialog(getDriver()).clickOKButton();
	}

	/**
	 * Clicks Remove Item button.
	 *
	 * @param skuCode the sku code
	 */
	public void selectItemToExchange(final String skuCode) {
		assertThat(selectItemInDialog(EXCHANGE_ORDER_PARENT_TABLE_CSS, EXCHANGE_ORDER_COLUMN_CSS, skuCode, "SKU CODE"))
				.as("Unable to find sku - " + skuCode)
				.isTrue();
	}

	/**
	 * Selects Shipping Address.
	 *
	 * @param shippingAddress String
	 */
	public void selectShippingAddress(final String shippingAddress) {
		assertThat(selectComboBoxItem(SHIPPING_ADDRESS_COMBO_CSS, shippingAddress))
				.as("Unable to find shipping address - " + shippingAddress)
				.isTrue();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Selects Shipping Method.
	 *
	 * @param shippingMethod String
	 */
	public void selectShippingMethod(final String shippingMethod) {
		assertThat(selectComboBoxItem(SHIPPING_METHOD_COMBO_CSS, shippingMethod))
				.as("Unable to find shipping method - " + shippingMethod)
				.isTrue();
	}

	//TODO need isChecked method for checkBox

	/**
	 * Clicks Physical Return Required Before Refund checkbox.
	 */
	public void clickRequiredReturnCheckbox() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(REQUIRED_RETURN_CHECKBOX_CSS)).click();
	}

	/**
	 * Selects payment source in combo box.
	 *
	 * @param paymentSource the payment source
	 */
	public void selectPaymentSource(final String paymentSource) {
		assertThat(selectComboBoxItem(PAYMENT_SOURCE_COMBO_CSS, paymentSource))
				.as("Unable to find payment source - " + paymentSource)
				.isTrue();
	}

	/**
	 * Creates Exchange.
	 *
	 * @param exchangeMap exchange info map
	 */
	public void createExchange(final Map<String, String> exchangeMap) {
		String returnSku = exchangeMap.get("Return Sku Code");
		String exchangeSku = exchangeMap.get("Exchange Sku Code");
		enterExchangeReturnQuantity(Integer.parseInt(exchangeMap.get("Return Qty")), returnSku);
		clickNextInDialog();
		selectItemToExchange(returnSku);
		clickRemoveItemButton();

		SelectASkuDialog selectASkuDialog = clickAddItemButton();
		selectASkuDialog.selectSkuAndPriceList(exchangeSku, exchangeMap.get("Price List Name"));

		selectShippingAddress(exchangeMap.get("Shipping Address"));
		selectShippingMethod(exchangeMap.get("Shipping Method"));
		clickNextInDialog();
		clickRequiredReturnCheckbox();
		selectPaymentSource(exchangeMap.get("Payment Source"));
		clickNextInDialog();
		clickFinish();
		waitTillElementDisappears(By.cssSelector(CREATE_EXCHANGE_PARENT_CSS));
	}

}
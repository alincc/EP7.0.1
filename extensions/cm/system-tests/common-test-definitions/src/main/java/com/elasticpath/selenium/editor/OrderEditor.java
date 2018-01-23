package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.MoveItemDialog;
import com.elasticpath.selenium.dialogs.SelectASkuDialog;
import com.elasticpath.selenium.wizards.CreateExchangeWizard;
import com.elasticpath.selenium.wizards.CreateRefundWizard;
import com.elasticpath.selenium.wizards.CreateReturnWizard;

/**
 * Order Editor.
 */
public class OrderEditor extends AbstractPageObject {

	private static final String EDITOR_PANE_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String CANCEL_ORDER_BUTTON_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Cancel Order'][seeable='true']";
	private static final String EDITOR_BUTTON_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='%s'][seeable='true']";
	private static final String CREATE_REFUND_BUTTON_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Create Refund'][seeable='true']";
	private static final String ORDER_STATUS_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Order Status'] > input";
	private static final String SHIPMENT_STATUS_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Status'] > input";
	private static final String TAB_CSS = "div[widget-id='%s'][seeable='true']";
	private static final String PAYMENT_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Order Payments History Table'] ";
	private static final String PAYMENT_COLUMN_CSS = PAYMENT_TABLE_PARENT_CSS + "div[column-id='%s']";
	private static final String SKU_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Sku Table'] ";
	private static final String SKU_COLUMN_CSS = SKU_TABLE_PARENT_CSS + "div[column-id='%s']";
	private static final String EXCHANGE_ORDER_NUMBER_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Exchange Order #'] > input";
	private static final String ORDER_NUMBER_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Order #'] > input";
	private static final String EXTERNAL_ORDER_NUMBER_INPUT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='External Order #'] > input";
	private static final String ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-id='Order Details Physical Shipment "
			+ "Table'][widget-type='Table'][seeable='true'] ";
	private static final String ORDER_DETAIL_SHIPMENT_TABLE_COLUMN_CSS = ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS + "div[column-id='%s']";
	private static final int NUMBER_OF_DOWN_KEYS = 10;
	private static final String ATTRIBUTE_VALUE = "value";
	private static final String EMAIL_ADDRESS_CSS = EDITOR_PANE_PARENT_CSS + "div[widget-type='Hyperlink'][seeable='true']";
	private static final int SLEEP_TIME = 500;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public OrderEditor(final WebDriver driver) {
		super(driver);
	}


	/**
	 * Clicks Cancel Order button.
	 *
	 * @return the confirm dialog
	 */
	public ConfirmDialog clickCancelOrderButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CANCEL_ORDER_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new ConfirmDialog(getDriver());
	}

	/**
	 * Clicks button.
	 *
	 * @param buttonWidgetId String
	 */
	public void clickEditorButton(final String buttonWidgetId) {
		scrollDownWithDownArrowKey(getDriver().findElement(By.cssSelector(EDITOR_PANE_PARENT_CSS)), NUMBER_OF_DOWN_KEYS);
		if (isButtonEnabled(String.format(EDITOR_BUTTON_CSS, buttonWidgetId))) {
			getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(EDITOR_BUTTON_CSS, buttonWidgetId))).click();
		}
	}

	/**
	 * Clicks Cancel Shipment button.
	 *
	 * @return the confirm dialog
	 */
	public ConfirmDialog clickCancelShipmentButton() {
		clickEditorButton("Cancel Shipment");
		return new ConfirmDialog(getDriver());
	}

	/**
	 * Clicks Create Return button.
	 *
	 * @return CreateReturnWizard
	 */
	public CreateReturnWizard clickCreateReturnButton() {
		clickEditorButton("Create Return ");
		return new CreateReturnWizard(getDriver());
	}

	/**
	 * Clicks Create Exchange button.
	 *
	 * @return CreateExchangeWizard
	 */
	public CreateExchangeWizard clickCreateExchangeButton() {
		clickEditorButton("Create Exchange");
		return new CreateExchangeWizard(getDriver());
	}

	/**
	 * Clicks Release Shipment button.
	 */
	public void clickReleaseShipmentButton() {
		scrollDownWithDownArrowKey(getDriver().findElement(By.cssSelector(EDITOR_PANE_PARENT_CSS)), NUMBER_OF_DOWN_KEYS);
		if (isButtonEnabled(String.format(EDITOR_BUTTON_CSS, "Release Shipment"))) {
			getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(EDITOR_BUTTON_CSS, "Release Shipment"))).click();
			new ConfirmDialog(getDriver()).clickOKButton();
		}
	}

	/**
	 * Clicks Create Refund button.
	 *
	 * @return CreateRefundWizard
	 */
	public CreateRefundWizard clickCreateRefundButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_REFUND_BUTTON_CSS)).click();
		return new CreateRefundWizard(getDriver());
	}

	/**
	 * Clicks Open Exchange Order button.
	 */
	public void clickOpenExchangeOrderButton() {
		clickEditorButton("Open Exchange Order...");
	}

	/**
	 * Clicks Move Item button.
	 *
	 * @return MoveItemDialog
	 */
	public MoveItemDialog clickMoveItemButton() {
		clickEditorButton("Move Item...");
		return new MoveItemDialog(getDriver());
	}

	/**
	 * Clicks Add Item button.
	 *
	 * @return SelectASkuDialog
	 */
	public SelectASkuDialog clickAddItemButton() {
		clickEditorButton("Add Item...");
		return new SelectASkuDialog(getDriver());
	}

	/**
	 * Clicks Remove Item button.
	 */
	public void clickRemoveItemButton() {
		clickEditorButton("Remove Item...");
		new ConfirmDialog(getDriver()).clickOKButton();
	}

	/**
	 * Verifies Order Status.
	 *
	 * @param expectedOrderStatus the expected order status.
	 */
	public void verifyOrderStatus(final String expectedOrderStatus) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(ORDER_STATUS_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Order Status validation failed")
				.isEqualTo(expectedOrderStatus);
	}

	/**
	 * Verifies Shipment Status.
	 *
	 * @param expectedShipmentStatus the expected shipment status.
	 */
	public void verifyShipmentStatus(final String expectedShipmentStatus) {
		getWaitDriver().waitForTextInInput(getDriver().findElement(By.cssSelector(SHIPMENT_STATUS_INPUT_CSS)), expectedShipmentStatus);
		assertThat(getDriver().findElement(By.cssSelector(SHIPMENT_STATUS_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Shipment status validation failed")
				.isEqualTo(expectedShipmentStatus);
	}

	/**
	 * Clicks to select tab.
	 *
	 * @param tabName the tab name.
	 */
	public void clickTab(final String tabName) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, tabName))).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Verifies Transaction Type.
	 *
	 * @param transactionType the transaction type.
	 */
	public void verifyTransactionType(final String transactionType) {
		verifyPaymentHistoryColumnValue(transactionType, "Transaction Type");
	}

	/**
	 * Verifies Payment History column value.
	 *  @param columnValue  the column value.
	 * @param columnName the column number.
	 */
	public void verifyPaymentHistoryColumnValue(final String columnValue, final String columnName) {
		sleep(SLEEP_TIME);
		assertThat(selectItemInEditorPane(PAYMENT_TABLE_PARENT_CSS, PAYMENT_COLUMN_CSS, columnValue, columnName))
				.as("Unable to find payment history column value - " + columnValue)
				.isTrue();
	}

	/**
	 * Verifies returned sku code.
	 *
	 * @param skuCode the sku code
	 */
	public void verifyReturnSkuCode(final String skuCode) {
		sleep(SLEEP_TIME);
		assertThat(selectItemInDialog(SKU_TABLE_PARENT_CSS, SKU_COLUMN_CSS, skuCode, "SKU Code"))
				.as("Unable to find sku - " + skuCode)
				.isTrue();
	}

	/**
	 * Verifies exchange order number is present.
	 *
	 * @return exchangeOrderNumber String
	 */
	public String verifyExchangeOrderNumberIsPresent() {
		String exchangeOrderNumber = getDriver().findElement(By.cssSelector(EXCHANGE_ORDER_NUMBER_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE);
		assertThat(Integer.parseInt(exchangeOrderNumber) > 0)
				.as("Unable to find exchange order number")
				.isTrue();
		return exchangeOrderNumber;
	}


	/**
	 * Verifies original and exchange order numbers.
	 *
	 * @param originalOrderNubmer String
	 * @param exchangeOrderNumber String
	 */
	public void verifyOriginalAndExchangeOrderNumbers(final String originalOrderNubmer, final String exchangeOrderNumber) {
		assertThat(getDriver().findElement(By.cssSelector(ORDER_NUMBER_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Exchange order number validation failed")
				.isEqualTo(exchangeOrderNumber);

		assertThat(getDriver().findElement(By.cssSelector(EXTERNAL_ORDER_NUMBER_INPUT_CSS)).getAttribute(ATTRIBUTE_VALUE))
				.as("Exchange order number validation failed")
				.isEqualTo(originalOrderNubmer);
	}

	/**
	 * Verifies and select order sku code.
	 *
	 * @param skuCode the sku code
	 */
	public void verifyAndSelectOrderSkuCode(final String skuCode) {
		sleep(SLEEP_TIME);
		assertThat(selectItemInDialog(ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS, ORDER_DETAIL_SHIPMENT_TABLE_COLUMN_CSS, skuCode, "SKU Code"))
				.as("Unable to find order sku - " + skuCode)
				.isTrue();
	}

	/**
	 * Verifies sku is not in the list.
	 *
	 * @param skuCode the sku code
	 */
	public void verifySkuCodeIsNotInList(final String skuCode) {
		assertThat(selectItemInDialog(ORDER_DETAIL_SHIPMENT_TABLE_PARENT_CSS, ORDER_DETAIL_SHIPMENT_TABLE_COLUMN_CSS, skuCode, "SKU Code"))
				.as("Sku is still in the list - " + skuCode)
				.isFalse();
	}


	/**
	 * Verifies shipment number.
	 *
	 * @param shipmentNumber String
	 */
	public void verifyShipmentNumber(final String shipmentNumber) {
		assertThat(getDriver().findElement(By.cssSelector(EDITOR_PANE_PARENT_CSS)).getText().contains(shipmentNumber))
				.as("Unable to find shipment number - " + shipmentNumber)
				.isTrue();
	}

	/**
	 * Returns customer's email address.
	 *
	 * @return cusomer's email
	 */
	public String getCustomerEmail() {
		return getDriver().findElement(By.cssSelector(EMAIL_ADDRESS_CSS)).getText();
	}
}

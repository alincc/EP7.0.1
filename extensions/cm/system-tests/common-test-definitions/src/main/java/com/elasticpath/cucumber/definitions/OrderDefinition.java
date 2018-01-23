package com.elasticpath.cucumber.definitions;

import java.util.List;
import java.util.Map;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.log4j.Logger;

import com.elasticpath.cucumber.util.CortexMacrosTestBase;
import com.elasticpath.selenium.dialogs.CompleteShipmentDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.MoveItemDialog;
import com.elasticpath.selenium.dialogs.SelectASkuDialog;
import com.elasticpath.selenium.editor.OrderEditor;
import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;
import com.elasticpath.selenium.navigations.CustomerService;
import com.elasticpath.selenium.resultspane.OrderSearchResultPane;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.CustomerServiceActionToolbar;
import com.elasticpath.selenium.toolbars.ShippingReceivingActionToolbar;
import com.elasticpath.selenium.wizards.CreateExchangeWizard;
import com.elasticpath.selenium.wizards.CreateRefundWizard;
import com.elasticpath.selenium.wizards.CreateReturnWizard;
import com.elasticpath.selenium.wizards.PaymentAuthorizationWizard;

/**
 * Order step definitions.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods"})
public class OrderDefinition {

	private static final String CUSTOMERNAME_COLUUMNNAME = "Customer Name";
	private static final String ORDER_STATUS_COLUMNNAME = "Order Status";
	private static final String ORDER_NUMBER_COLUMNNAME = "Order #";
	private final CustomerService customerService;
	private OrderSearchResultPane orderSearchResultPane;
	private OrderEditor orderEditor;
	private CreateRefundWizard createRefundWizard;
	private CreateReturnWizard createReturnWizard;
	private CreateExchangeWizard createExchangeWizard;
	private CompleteShipmentDialog completeShipmentDialog;
	private MoveItemDialog moveItemDialog;
	private SelectASkuDialog selectASkuDialog;
	private ConfirmDialog confirmDialog;
	private PaymentAuthorizationWizard paymentAuthorizationWizard;
	private final ActivityToolbar activityToolbar;
	private final ShippingReceivingActionToolbar shippingReceivingActionToolbar;
	private final CustomerServiceActionToolbar customerServiceActionToolbar;
	private String exchangeOrderNumber;
	private static final Logger LOGGER = Logger.getLogger(OrderDefinition.class);

	/**
	 * Constructor.
	 */
	public OrderDefinition() {
		activityToolbar = new ActivityToolbar((SeleniumDriverSetup.getDriver()));
		shippingReceivingActionToolbar = new ShippingReceivingActionToolbar(SeleniumDriverSetup.getDriver());
		customerService = new CustomerService(SeleniumDriverSetup.getDriver());
		customerServiceActionToolbar = new CustomerServiceActionToolbar(SeleniumDriverSetup.getDriver());
	}

	/**
	 * Search order by number.
	 *
	 * @param orderNum the order number.
	 */
	@When("^I search for an order by number (.+)$")
	public void searchOrderByNumber(final String orderNum) {
		customerService.enterOrderNumber(orderNum);
		orderSearchResultPane = customerService.clickOrderSearch();
	}

	/**
	 * Search order by email.
	 *
	 * @param email the email.
	 */
	@When("^I search for orders by email (.+)$")
	public void searchOrderByEmail(final String email) {
		customerService.enterEmailUserID(email);
		orderSearchResultPane = customerService.clickOrderSearch();
	}

	/**
	 * Search latest order by email.
	 */
	@When("^I search for the latest orders by email$")
	public void searchLatestByEmail() {
		searchAndOpenOrderEditor();
		customerService.clearInputFields();
		customerService.enterEmailUserID(orderEditor.getCustomerEmail());
		orderSearchResultPane = customerService.clickOrderSearch();
	}

	/**
	 * Verify order number in search results pane.
	 *
	 * @param orderNumber the order number.
	 */
	@Then("^I should see the order number (.+) in search results pane$")
	public void verifyOrderNumberInSearchResultsPane(final String orderNumber) {
		orderSearchResultPane.verifyOrderColumnValueAndSelectRow(orderNumber, ORDER_NUMBER_COLUMNNAME);
	}

	/**
	 * Verifies latest order in search results pane.
	 */
	@Then("^I should see the latest order in results pane$")
	public void verifyLatestOrderInResultsPane() {
		orderSearchResultPane.verifyOrderColumnValueAndSelectRow(CortexMacrosTestBase.PURCHASE_NUMBER, ORDER_NUMBER_COLUMNNAME);
	}

	/**
	 * Verify customer name in search results pane.
	 *
	 * @param orderNumber the order number.
	 */
	@Then("^I should see customer name (.+) in search results pane$")
	public void verifyCustomerNameInSearchResultsPane(final String orderNumber) {
		orderSearchResultPane.verifyOrderColumnValueAndSelectRow(orderNumber, CUSTOMERNAME_COLUUMNNAME);
	}

	/**
	 * Open order editor.
	 *
	 * @param orderStatus the order status.
	 */
	@And("^I open the order editor for (?:a|an) (.+) order$")
	public void openOrderEditor(final String orderStatus) {
		orderEditor = orderSearchResultPane.selectOrderAndOpenOrderEditor(orderStatus, ORDER_STATUS_COLUMNNAME);
	}

	/**
	 * Cancel order.
	 */
	@And("^I cancel the order$")
	public void cancelOrder() {
		confirmDialog = orderEditor.clickCancelOrderButton();
		confirmDialog.clickOKButton();
		customerServiceActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Cancel shipment.
	 */
	@And("^I cancel the shipment")
	public void cancelShipment() {
		confirmDialog = orderEditor.clickCancelShipmentButton();
		confirmDialog.clickOKButton();
		customerServiceActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Click Create Return button.
	 */
	@And("^I click create return button")
	public void clickCreateReturnButton() {
		createReturnWizard = orderEditor.clickCreateReturnButton();
	}

	/**
	 * Creates return for digital item.
	 *
	 * @param quantity the quantity
	 * @param skuCode  the sku code
	 */
	@And("^I create return for digital item with quantity (.+) for sku (.+)")
	public void createDigitalReturn(final int quantity, final String skuCode) {
		clickCreateReturnButton();
		createReturnWizard.createDigitalReturn(quantity, skuCode);
	}

	/**
	 * Creates return for digital item.
	 *
	 * @param quantity the quantity
	 * @param skuCode  the sku code
	 */
	@And("^I create return for physical item with quantity (.+) for sku (.+)")
	public void createPhysicalReturn(final int quantity, final String skuCode) {
		shippingReceivingActionToolbar.clickReloadActiveEditor();
		clickCreateReturnButton();
		createReturnWizard.createPhysicalReturn(quantity, skuCode);
	}

	/**
	 * Completes the order.
	 */
	@And("^I complete the order shipment")
	public void completeOrderShipment() {
		selectOrderEditorTab("Details");
		orderEditor.clickReleaseShipmentButton();
		activityToolbar.clickShippingReceivingButton();
		completeShipmentDialog = shippingReceivingActionToolbar.clickCompleteShipmentButton();
		completeShipmentDialog.completeShipment(CortexMacrosTestBase.PURCHASE_NUMBER + "-1");
	}

	/**
	 * Verify order status.
	 *
	 * @param orderStatus the order status
	 */
	@And("^the order status should be (.+)$")
	public void verifyOrderStatus(final String orderStatus) {
		orderEditor.verifyOrderStatus(orderStatus);
	}

	/**
	 * Verify shipment status.
	 *
	 * @param shipmentStatus the shipment status.
	 */
	@And("^the shipment status should be (.+)$")
	public void verifyShipmentStatus(final String shipmentStatus) {
		orderEditor.verifyShipmentStatus(shipmentStatus);
	}

	/**
	 * Select order editor tab.
	 *
	 * @param tabName the tab name.
	 */
	@When("^I select (.+) tab in the Order Editor$")
	public void selectOrderEditorTab(final String tabName) {
		orderEditor.clickTab(tabName);
	}

	/**
	 * Verify Transaction Type.
	 *
	 * @param transactionType the transaction type.
	 */
	@Then("^I should see transaction type (.+) in the Payment History$")
	public void verifyTransactionType(final String transactionType) {
		orderEditor.verifyTransactionType(transactionType);
	}

	/**
	 * Clear search results window.
	 *
	 * @param tabName the tab name.
	 */
	@When("^I close the window (.+)")
	public void clearSearchResults(final String tabName) {
		orderSearchResultPane.close(tabName);
	}

	/**
	 * Clear input fields.
	 */
	@When("^I clear the input fields$")
	public void clearInputFields() {
		customerService.clearInputFields();
	}

	/**
	 * Searches order and opens order editor.
	 */
	@When("^I search and open order editor for the latest order$")
	public void searchAndOpenOrderEditor() {
		String orderNumber = CortexMacrosTestBase.PURCHASE_NUMBER;
		LOGGER.info("ordernumber.... " + orderNumber);
		customerService.enterOrderNumber(orderNumber);
		orderSearchResultPane = customerService.clickOrderSearch();
		orderEditor = orderSearchResultPane.selectOrderAndOpenOrderEditor(orderNumber, ORDER_NUMBER_COLUMNNAME);
	}

	/**
	 * Searches order by number.
	 */
	@When("^I search the latest order by number$")
	public void searchOrderByNumber() {
		String orderNumber = CortexMacrosTestBase.PURCHASE_NUMBER;
		LOGGER.info("searching for ordernumber.... " + orderNumber);
		customerService.enterOrderNumber(orderNumber);
		orderSearchResultPane = customerService.clickOrderSearch();
	}

	/**
	 * Creates refund.
	 *
	 * @param refundMap refund item values
	 */
	@When("^I create a refund with following values$")
	public void createRefund(final Map<String, String> refundMap) {
		shippingReceivingActionToolbar.clickReloadActiveEditor();
		createRefundWizard = orderEditor.clickCreateRefundButton();
		createRefundWizard.createRefund(refundMap);
	}


	/**
	 * Creates exchange.
	 *
	 * @param exchangeMap exchange item values
	 */
	@When("^I create a exchange with following values$")
	public void createExchange(final Map<String, String> exchangeMap) {
		shippingReceivingActionToolbar.clickReloadActiveEditor();
		createExchangeWizard = orderEditor.clickCreateExchangeButton();
		createExchangeWizard.createExchange(exchangeMap);
		shippingReceivingActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify sku code.
	 *
	 * @param skuCode the sku code.
	 */
	@Then("^I should see the returned sku code (.+)$")
	public void verifyReturnedSkuCode(final String skuCode) {
		orderEditor.verifyReturnSkuCode(skuCode);
	}

	/**
	 * Verifies exchange order number is present.
	 */
	@Then("^I should see exchange order number$")
	public void verifyExchangeOrderNumberIsPresent() {
		exchangeOrderNumber = orderEditor.verifyExchangeOrderNumberIsPresent();
		LOGGER.info("exchange order number: " + exchangeOrderNumber);
	}

	/**
	 * Opens exchange order editor.
	 */
	@When("^I open the exchange order editor$")
	public void openExchangeOrderEditor() {
		orderEditor.clickOpenExchangeOrderButton();
	}

	/**
	 * Verifies original and exchange order number.
	 */
	@When("^I should see the original order\\# as External Order\\# and exchange order\\# as Order\\#$")
	public void verifyOrderNumbers() {
		orderEditor.verifyOriginalAndExchangeOrderNumbers(CortexMacrosTestBase.PURCHASE_NUMBER, exchangeOrderNumber);
	}

	/**
	 * Verifies sku present in the list.
	 *
	 * @param skuCodeList list of order skus
	 */
	@When("^I should see the following skus? in item list$")
	public void verifySkuCodePresentInList(final List<String> skuCodeList) {
		for (String skuCode : skuCodeList) {
			orderEditor.verifyAndSelectOrderSkuCode(skuCode);
		}
	}

	/**
	 * Verifies sku code is not in the list.
	 *
	 * @param skuCodeList list of order skus
	 */
	@When("^I should not see the following skus? in item list$")
	public void verifySkuCodeIsNotPresentInList(final List<String> skuCodeList) {
		for (String skuCode : skuCodeList) {
			orderEditor.verifySkuCodeIsNotInList(skuCode);
		}
	}

	/**
	 * Creates a new shipment.
	 *
	 * @param skuCode            the sku code
	 * @param newShipmentInfoMap new shipment values
	 */
	@And("^I create a new shipment for sku (.+) with following values$")
	public void createNewShipment(final String skuCode, final Map<String, String> newShipmentInfoMap) {
		orderEditor.verifyAndSelectOrderSkuCode(skuCode);
		moveItemDialog = orderEditor.clickMoveItemButton();
		moveItemDialog.moveItem(newShipmentInfoMap.get("Address"), newShipmentInfoMap.get("Shipment Method"));
		paymentAuthorizationWizard = customerServiceActionToolbar.clickSaveAllButton();
		paymentAuthorizationWizard.completePaymentAuthorization(newShipmentInfoMap.get("Payment Source"));
	}


	/**
	 * Adds an item to the shipment.
	 *
	 * @param skuCode        the sku code
	 * @param addItemInfoMap item values
	 */
	@When("^I add sku (.+) to the shipment with following values$")
	public void addItemToShipment(final String skuCode, final Map<String, String> addItemInfoMap) {
		selectASkuDialog = orderEditor.clickAddItemButton();
		selectASkuDialog.selectSkuAndPriceList(skuCode, addItemInfoMap.get("Price List Name"));
		paymentAuthorizationWizard = customerServiceActionToolbar.clickSaveAllButton();
		paymentAuthorizationWizard.completePaymentAuthorization(addItemInfoMap.get("Payment Source"));
		customerServiceActionToolbar.clickReloadActiveEditor();
	}


	/**
	 * Removes an item from shipment.
	 *
	 * @param skuCode the sku code
	 */
	@And("^I remove sku (.+) from the shipment$")
	public void addItemToShipment(final String skuCode) {
		orderEditor.verifyAndSelectOrderSkuCode(skuCode);
		orderEditor.clickRemoveItemButton();
		customerServiceActionToolbar.clickSaveAllButton();
		customerServiceActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verifies number of shipments.
	 *
	 * @param numberOfShipments number of shipments
	 */
	@And("^I should see (\\d+) shipments?$")
	public void verifyNumberOfShipments(final int numberOfShipments) {
		for (int i = 0; i < numberOfShipments; i++) {
			String shipmentNumber = CortexMacrosTestBase.PURCHASE_NUMBER + "-" + (i + 1);
			LOGGER.info("verifying shipment #: " + shipmentNumber);
			orderEditor.verifyShipmentNumber(shipmentNumber);
		}
	}


}

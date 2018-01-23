package com.elasticpath.selenium.navigations;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.resultspane.CustomerSearchResultsPane;
import com.elasticpath.selenium.resultspane.OrderSearchResultPane;

/**
 * Customer Service Page.
 */
public class CustomerService extends AbstractNavigation {

	private static final String ACTIVE_LEFT_PANE = "div[pane-location='left-pane-inner'] div[active-editor='true'] ";
	//TODO to be implemented when ready
	private static final String APPEARANCE_ID_CSS = "div[appearance-id='ctab-item']";
	private static final String CUSTOMERS_TAB_CSS = APPEARANCE_ID_CSS + "[widget-id='Customers']";
	private static final String EMAIL_USERID_INPUT_CSS = ACTIVE_LEFT_PANE + "div[widget-id='Email / User ID'] > input";
	private static final String ORDER_NUMBER_INPUT_CSS = ACTIVE_LEFT_PANE + "div[widget-id='Order Number'] > input";
	private static final String SEARCH_BUTTON_CSS = "div[pane-location='left-pane-inner'] div[widget-id='Search'][seeable='true']";
	private static final String CLEAR_BUTTON_CSS
			= "div[pane-location='left-pane-inner'] "
			+ "div[automation-id='com.elasticpath.cmclient.fulfillment.FulfillmentMessages.SearchView_ClearButton'][seeable='true']";
	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CustomerService(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if order number field exists.
	 */
	public void verifyOrderNumberFieldExist() {
		getWaitDriver().waitForElementToBeInteractable(ORDER_NUMBER_INPUT_CSS);
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(ORDER_NUMBER_INPUT_CSS));
	}

	/**
	 * Enters order number.
	 *
	 * @param orderNum String
	 */
	public void enterOrderNumber(final String orderNum) {
		getWaitDriver().waitForElementToBeInteractable(ORDER_NUMBER_INPUT_CSS);
		clearAndType(getDriver().findElement(By.cssSelector(ORDER_NUMBER_INPUT_CSS)), orderNum);
	}

	/**
	 * Clicks on search for order.
	 *
	 * @return OrderSearchResultPane
	 */
	public OrderSearchResultPane clickOrderSearch() {
		getWaitDriver().waitForElementToBeInteractable(SEARCH_BUTTON_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SEARCH_BUTTON_CSS)).click();
		return new OrderSearchResultPane(getDriver());
	}

	/**
	 * Clicks Customer tab.
	 */
	public void clickCustomersTab() {
		getWaitDriver().waitForElementToBeInteractable(CUSTOMERS_TAB_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CUSTOMERS_TAB_CSS)).click();
	}

	/**
	 * Enters Email User ID.
	 *
	 * @param emailUserID String
	 */
	public void enterEmailUserID(final String emailUserID) {
		getWaitDriver().waitForElementToBeInteractable(EMAIL_USERID_INPUT_CSS);
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(EMAIL_USERID_INPUT_CSS)), emailUserID);
	}

	/**
	 * Clicks Search for customer.
	 *
	 * @return CustomerSearchResultsPane
	 */
	public CustomerSearchResultsPane clickCustomerSearch() {
		getWaitDriver().waitForElementToBeInteractable(SEARCH_BUTTON_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SEARCH_BUTTON_CSS)).click();
		return new CustomerSearchResultsPane(getDriver());
	}

	/**
	 * Clear the input fields.
	 */
	public void clearInputFields() {
		getWaitDriver().waitForElementToBeInteractable(CLEAR_BUTTON_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CLEAR_BUTTON_CSS)).click();
		}
}

package com.elasticpath.cucumber.definitions;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;
import com.elasticpath.selenium.navigations.CustomerService;
import com.elasticpath.selenium.resultspane.CustomerSearchResultsPane;

/**
 * Customer step definition.
 */
public class CustomerDefinition {
	private final CustomerService customerService;
	private CustomerSearchResultsPane customerSearchResultsPane;

	/**
	 * Constructor.
	 */
	public CustomerDefinition() {
		customerService = new CustomerService(SeleniumDriverSetup.getDriver());
	}

	/**
	 * Click customer tab.
	 */
	@When("^I select Customers tab")
	public void clicksCustomersTab() {
		customerService.clickCustomersTab();
	}

	/**
	 * Search for customer.
	 *
	 * @param customerEmailID the customer email id.
	 */
	@When("^I search for customer with email ID (.+)$")
	public void searchCustomer(final String customerEmailID) {
		enterCustomerEmailID(customerEmailID);
		clickCustomerSearch();
	}

	/**
	 * Verify Customer exists.
	 *
	 * @param expectedCustomerID the expected customer Id.
	 */
	@Then("^I should see customer with email ID (.+) in result list$")
	public void verifyCustomerExists(final String expectedCustomerID) {
		customerSearchResultsPane.verifyCustomerExists(expectedCustomerID);
	}

	private void enterCustomerEmailID(final String customerEmailID) {
		customerService.enterEmailUserID(customerEmailID);
	}

	private void clickCustomerSearch() {
		customerSearchResultsPane = customerService.clickCustomerSearch();
	}
}

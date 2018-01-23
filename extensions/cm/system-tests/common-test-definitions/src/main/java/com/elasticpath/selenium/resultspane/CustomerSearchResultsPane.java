package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Customer Search Results Pane.
 */
public class CustomerSearchResultsPane extends AbstractPageObject {

	private static final String CUSTOMER_SEARCH_RESULT_PARENT_CSS = "div[widget-id='Customer Search Result Table'][widget-type='Table'] ";
	private static final String CUSTOMER_SEARCH_RESULT_LIST_CSS = CUSTOMER_SEARCH_RESULT_PARENT_CSS + "div[parent-widget-id='Customer Search Result"
			+ " "
			+ "Table'] div[column-id='%s']";
	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CustomerSearchResultsPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies Customer exists.
	 *
	 * @param expectedEmailID the expected email id.
	 */
	public void verifyCustomerExists(final String expectedEmailID) {
		getWaitDriver().waitForPageLoad();
		assertThat(selectItemInCenterPane(CUSTOMER_SEARCH_RESULT_PARENT_CSS,
				CUSTOMER_SEARCH_RESULT_LIST_CSS, expectedEmailID, "Email Address"))
				.as("Unable to find customer email - " + expectedEmailID)
				.isTrue();
	}
}

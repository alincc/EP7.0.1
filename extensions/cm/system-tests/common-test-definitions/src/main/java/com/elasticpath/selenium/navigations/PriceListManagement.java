package com.elasticpath.selenium.navigations;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.resultspane.PriceListAssignmentsResultPane;
import com.elasticpath.selenium.resultspane.PriceListsResultPane;

/**
 * Price List Management.
 */
public class PriceListManagement extends AbstractNavigation {

	private static final String LEFT_PANE_INNER_PARENT_CSS = "div[pane-location='left-pane-inner'] div[seeable='true'] ";
	private static final String PRICE_LIST_ASSIGNMENTS_TAB_CSS = "div[widget-id='Price List Assignments'][widget-type='CTabItem']";
	private static final String PRICE_LIST_TAB_CSS = "div[widget-id='Price Lists'][widget-type='CTabItem']";
	private static final String PRICE_LIST_ASSIGNMENTS_SEARCH_BUTTON_CSS = LEFT_PANE_INNER_PARENT_CSS + "div[widget-id='Search'][seeable='true']";
	private static final String PRICE_LIST_NAME_INPUT_CSS = "div[widget-id='Price List Name'] > input";
	private static final String PRICE_LIST_SEARCH_BUTTON_CSS = LEFT_PANE_INNER_PARENT_CSS + "div[widget-id='Search'][seeable='true']";
	private static final String CATALOG_COMBO_CSS = LEFT_PANE_INNER_PARENT_CSS + "div[widget-id='Catalog'][widget-type='CCombo']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceListManagement(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Price List.
	 */
	public void clickPriceListTab() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(PRICE_LIST_TAB_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks Price List Assignments.
	 */
	public void clickPriceListAssignmentsTab() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(PRICE_LIST_ASSIGNMENTS_TAB_CSS)).click();
	}

	/**
	 * Enters Price List name for search.
	 *
	 * @param priceListName String
	 */
	public void enterPriceListName(final String priceListName) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRICE_LIST_NAME_INPUT_CSS)), priceListName);
	}

	/**
	 * Clicks Search for Price List Assignment.
	 *
	 * @return PriceListAssignmentsResultPane
	 */
	public PriceListAssignmentsResultPane clickPriceListAssignmentSearch() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(PRICE_LIST_ASSIGNMENTS_SEARCH_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new PriceListAssignmentsResultPane(getDriver());
	}

	/**
	 * Clicks Search for Price Lists.
	 *
	 * @return PriceListsResultPane
	 */
	public PriceListsResultPane clickPriceListsSearch() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(PRICE_LIST_SEARCH_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new PriceListsResultPane(getDriver());
	}

	/**
	 * Selects catalog from combobox.
	 *
	 * @param catalogName String
	 */
	public void selectCatalogFromComboBox(final String catalogName) {
		WebElement comboBox = getWaitDriver().waitForElementToBeClickable(By.cssSelector(CATALOG_COMBO_CSS));
		comboBox.click();
		getWaitDriver().waitForPageLoad();
		assertThat(selectComboBoxItem(CATALOG_COMBO_CSS, catalogName))
				.as("Unable to find catalog - " + catalogName)
				.isTrue();
		getSelectedElement().click();
	}
}
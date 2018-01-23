package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.editor.PriceListEditor;

/**
 * Price List Results Pane.
 */
public class PriceListsResultPane extends AbstractPageObject {

	private static final String DELETE_PRICE_LIST_CSS = "div[widget-id='Delete Price List']";
	private static final String PRICE_LIST_PARENT_CSS = "div[widget-id='Price List Search Result'][widget-type='Table'] ";
	private static final String PRICE_LIST_COLUMN_CSS = PRICE_LIST_PARENT_CSS + "div[parent-widget-id='Price List Search Result']"
			+ " div[column-id='%s']";
	private static final String PRICE_LIST_COLUMNNAME = "Price List";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceListsResultPane(final WebDriver driver) {
		super(driver);
	}


	/**
	 * Verifies given price list exists.
	 *
	 * @param expectedPriceList the expected price list.
	 */
	public void verifyPriceListExists(final String expectedPriceList) {
		assertThat(selectItemInCenterPane(PRICE_LIST_PARENT_CSS, PRICE_LIST_COLUMN_CSS, expectedPriceList, PRICE_LIST_COLUMNNAME))
				.as("Expected Price List does not exist - " + expectedPriceList)
				.isTrue();
	}

	/**
	 * Verifies if Price List is deleted.
	 *
	 * @param expectedPriceListName the expected price list name.
	 */
	public void verifyPriceListDeleted(final String expectedPriceListName) {
		assertThat(selectItemInCenterPane(PRICE_LIST_PARENT_CSS, PRICE_LIST_COLUMN_CSS, expectedPriceListName, PRICE_LIST_COLUMNNAME))
				.as("Delete failed, Price List is still in the list - " + expectedPriceListName)
				.isFalse();
	}

	/**
	 * Deletes given price list.
	 *
	 * @param priceListName the price list name.
	 */
	public void deletePriceList(final String priceListName) {
		assertThat(selectItemInCenterPane(PRICE_LIST_PARENT_CSS, PRICE_LIST_COLUMN_CSS, priceListName, PRICE_LIST_COLUMNNAME))
				.as("Unable to delete Price List, it does not exist - " + priceListName)
				.isTrue();
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(DELETE_PRICE_LIST_CSS)).click();
	}

	/**
	 * Opens Price List editor.
	 *
	 * @param priceListName The price list name.
	 * @return the price list editor.
	 */
	public PriceListEditor openPriceListEditor(final String priceListName) {
		verifyPriceListExists(priceListName);
		doubleClick(getSelectedElement());
		return new PriceListEditor(getDriver());
	}

	/**
	 * Opens Selected price list editor.
	 *
	 * @return the price list editor.
	 */
	public PriceListEditor openSelectedPriceListEditor() {
		doubleClick(getSelectedElement());
		return new PriceListEditor(getDriver());
	}

}

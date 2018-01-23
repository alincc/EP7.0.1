package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.PriceEditorDialog;

/**
 * Price List Details pane.
 */
public class PriceListEditor extends AbstractPageObject {

	private static final String PRICE_LIST_EDITOR_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String PRICE_LIST_NAME_INPUT_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Price List'] > input";
	private static final String PRICE_LIST_DESCRIPTION_INPUT_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Description'] > input";
	private static final String CURRENCY_CODE_INPUT_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Currency'] > input";
	private static final String PRICES_TAB_CSS = "div[pane-location='editor-pane'] div[widget-id='Prices']";
	private static final String PRICE_LIST_SUMMARY_TAB_CSS = "div[pane-location='editor-pane'] div[widget-id='Price List Summary']";
	private static final String PRICE_LIST_EDITOR_CLOSE_ICON_CSS = "div[widget-id='%s'][appearance-id='ctab-item'][active-tab='true'] > "
			+ "div[style*='.gif']";
	private static final String ADD_PRICE_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Add Price...']";
	private static final String DELETE_PRICE_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Delete Price...']";
	private static final String EDIT_PRICE_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Edit Price...']";
	private static final String OPEN_ITEM_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Open Item...']";
	private static final String BASE_AMOUNT_PARENT_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Base Amount'][widget-type='Table'] ";
	private static final String BASE_AMOUNT_COLUMN_CSS = BASE_AMOUNT_PARENT_CSS + "div[parent-widget-id='Base "
			+ "Amount'][widget-type='table_row'] > div[column-id='%s']";
	//	private static final String BASE_AMOUNT_PRODUCT_NAME_LIST_CSS = BASE_AMOUNT_PARENT_CSS + "div[parent-widget-id='Base " +
//			"Amount'][widget-type='table_row'] > div[column-id='%s']";
	private static final String PRICE_LIST_ROW_CSS = BASE_AMOUNT_PARENT_CSS + "div[parent-widget-id='Base Amount'][row-id='%s']";
	private static final String PRICE_LIST_COLUMN_CSS = "div[column-id='%s']";
	private static final String SEARCH_BUTTON_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id='Search']";
	private static final String SEARCH_TEXT_BOX_LIST_CSS = PRICE_LIST_EDITOR_PARENT_CSS + "div[widget-id=''][widget-type='Text'] > input";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceListEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Enters price list name.
	 *
	 * @param priceListName the price list name.
	 */
	public void enterPriceListName(final String priceListName) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRICE_LIST_NAME_INPUT_CSS)), priceListName);
	}

	/**
	 * Enters price list description.
	 *
	 * @param priceListDescription the price list description.
	 */
	public void enterPriceListDescription(final String priceListDescription) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRICE_LIST_DESCRIPTION_INPUT_CSS)), priceListDescription);
	}

	/**
	 * Enters price list currency.
	 *
	 * @param priceListCurrency the price list currency.
	 */
	public void enterPriceListCurrency(final String priceListCurrency) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(CURRENCY_CODE_INPUT_CSS)), priceListCurrency);
	}

	/**
	 * Enters Code.
	 *
	 * @param code the code to search for.
	 */
	public void enterCodeToSearch(final String code) {
		clearAndType(getDriver().findElements(By.cssSelector(SEARCH_TEXT_BOX_LIST_CSS)).get(0), code);
	}

	/**
	 * Clears price list name.
	 */
	public void clearPriceListName() {
		clearField(getDriver().findElement(By.cssSelector(PRICE_LIST_NAME_INPUT_CSS)));
	}

	/**
	 * Clears price list description.
	 */
	public void clearPriceListDescription() {
		clearField(getDriver().findElement(By.cssSelector(PRICE_LIST_DESCRIPTION_INPUT_CSS)));
	}

	/**
	 * Clears price list currency.
	 */
	public void clearPriceListCurrency() {
		clearField(getDriver().findElement(By.cssSelector(CURRENCY_CODE_INPUT_CSS)));
	}

	/**
	 * Clears all fields.
	 */
	public void clearAll() {
		clearPriceListName();
		clearPriceListDescription();
		clearPriceListCurrency();
	}

	/**
	 * Selects Prices tab.
	 */
	public void selectPricesTab() {
		getDriver().findElement(By.cssSelector(PRICES_TAB_CSS)).click();
	}

	/**
	 * Selects Price List Summary tab.
	 */
	public void selectPriceListSummaryTab() {
		getDriver().findElement(By.cssSelector(PRICE_LIST_SUMMARY_TAB_CSS)).click();
	}

	/**
	 * Close Price List Editor.
	 *
	 * @param priceListName String
	 */
	public void closePriceListEditor(final String priceListName) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(PRICE_LIST_EDITOR_CLOSE_ICON_CSS, priceListName))).click();
	}

	/**
	 * Clicks Add Price button.
	 *
	 * @return the product editor dialog.
	 */
	public PriceEditorDialog clickAddPriceButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(ADD_PRICE_BUTTON_CSS)).click();
		return new PriceEditorDialog(getDriver());
	}

	/**
	 * Clicks Delete Price button.
	 */
	public void clickDeletePriceButton() {
		getDriver().findElement(By.cssSelector(DELETE_PRICE_BUTTON_CSS)).click();
		new ConfirmDialog(getDriver()).clickOKButton();
	}

	/**
	 * Clicks Edit Price button.
	 *
	 * @return the product editor dialog.
	 */
	public PriceEditorDialog clickEditPriceButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(EDIT_PRICE_BUTTON_CSS)).click();
		return new PriceEditorDialog(getDriver());
	}

	/**
	 * Clicks Open Item button.
	 *
	 * @return the product editor.
	 */
	public ProductEditor clickOpenItemButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(OPEN_ITEM_BUTTON_CSS)).click();
		return new ProductEditor(getDriver());
	}

	/**
	 * Clicks Search button.
	 */
	public void clickSearchButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SEARCH_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Verify product code is present in price list.
	 *
	 * @param productCode the product code.
	 */
	public void verifyProductCodeIsPresentInPriceList(final String productCode) {
		assertThat(selectItemInEditorPane(BASE_AMOUNT_PARENT_CSS, BASE_AMOUNT_COLUMN_CSS, productCode, "Product Code"))
				.as("Unable to find product code - " + productCode)
				.isTrue();
	}

	/**
	 * Verify sku code is present in price list.
	 *
	 * @param skuCode the sku code.
	 */
	public void verifySkuCodeIsPresentInPriceList(final String skuCode) {
		assertThat(selectItemInDialog(BASE_AMOUNT_PARENT_CSS, BASE_AMOUNT_COLUMN_CSS, skuCode, "SKU Code"))
				.as("Unable to find product code - " + skuCode)
				.isTrue();
	}

	/**
	 * Verify product name is present in price list.
	 *
	 * @param productName the product name.
	 */
	public void verifyProductNameIsPresentInPriceList(final String productName) {
		assertThat(selectItemInEditorPane(BASE_AMOUNT_PARENT_CSS, BASE_AMOUNT_COLUMN_CSS, productName, "Product Name"))
				.as("Unable to find product name - " + productName)
				.isTrue();
	}

	/**
	 * Select product row by product code.
	 *
	 * @param productCode the product code.
	 */
	public void selectPriceRowByProductCode(final String productCode) {
		assertThat(selectItemInEditorPane(BASE_AMOUNT_PARENT_CSS, BASE_AMOUNT_COLUMN_CSS, productCode, "Product Code"))
				.as("Unable to find product code - " + productCode)
				.isTrue();
	}

	/**
	 * Select price row by product name.
	 *
	 * @param productName the product name.
	 */
	public void selectPriceRowByProductName(final String productName) {
		assertThat(selectItemInEditorPane(BASE_AMOUNT_PARENT_CSS, BASE_AMOUNT_COLUMN_CSS, productName, "Product Name"))
				.as("Unable to find product name - " + productName)
				.isTrue();
	}

	/**
	 * Verify product code is not present in price list.
	 *
	 * @param productCode the product code.
	 */
	public void verifyProductCodeIsNotPresentInPriceList(final String productCode) {
		assertThat(selectItemInEditorPane(BASE_AMOUNT_PARENT_CSS, BASE_AMOUNT_COLUMN_CSS, productCode, "Product Code"))
				.as("Product code should not be in price list - " + productCode)
				.isFalse();
	}

	/**
	 * Verify list price in price list.
	 *
	 * @param productName the produce name.
	 * @param listPrice   the list price.
	 */
	public void verifyListPriceInPriceList(final String productName, final String listPrice) {
		verifyProductPrice(productName, listPrice);
	}

	/**
	 * Verify sale price in price list.
	 *
	 * @param productName the product name.
	 * @param salePrice   the sale price.
	 */
	public void verifySalePriceInPriceList(final String productName, final String salePrice) {
		verifyProductPrice(productName, salePrice);
	}

	/**
	 * Verify product price.
	 *
	 * @param productName the produce name.
	 * @param price       the price.
	 */
	private void verifyProductPrice(final String productName, final String price) {
		WebElement row = getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(PRICE_LIST_ROW_CSS, productName)));
		assertThat(row.findElement(By.cssSelector(String.format(PRICE_LIST_COLUMN_CSS, price))).getText())
				.as("Price validation failed")
				.isEqualTo(price);
	}
}

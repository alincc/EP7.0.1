package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

/**
 * Base Price Editor Dialog.
 */
public class PriceEditorDialog extends AbstractDialog {

	private static final String PRICE_EDITOR_PARENT_CSS = "div[widget-id='Price Editor'][widget-type='Shell'] ";
	private static final String LIST_PRICE_INPUT_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='List Price'] > input";
	private static final String SALE_PRICE_INPUT_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='Sale Price'] > input";
	private static final String SELECT_PRODUCT_IMAGE_LINK_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-type='ImageHyperlink'] > div[style*='.png']";
	private static final String OK_BUTTON_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='OK']";
	private static final String TYPE_SELECTOR_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='Type']";
	private static final String DIALOG_ERROR_VALIDATION = "div[widget-id='Code'] + div > img";
	private static final String QUANTITY_INPUT_CSS = PRICE_EDITOR_PARENT_CSS + "div[widget-id='Quantity']";
	private static final String INCREASE_QUANTITY_BUTTON_CSS = QUANTITY_INPUT_CSS + " div[appearance-id='spinner-button-up']";
	private static final int SLEEP_TIME = 500;

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PriceEditorDialog(final WebDriver driver) {
		super(driver);
	}


	/**
	 * Inputs List Price.
	 *
	 * @param listPrice String
	 */
	public void enterListPrice(final String listPrice) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(LIST_PRICE_INPUT_CSS)), listPrice);
	}

	/**
	 * Inputs Sale Price.
	 *
	 * @param salePrice String
	 */
	public void enterSalePrice(final String salePrice) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(SALE_PRICE_INPUT_CSS)), salePrice);
	}

	/**
	 * Clicks OK button.
	 */
	public void clickOKButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(OK_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Clicks Select Product image link.
	 *
	 * @return SelectAProductDialog
	 */
	public SelectAProductDialog clickSelectProductImageLink() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SELECT_PRODUCT_IMAGE_LINK_CSS)).click();
		return new SelectAProductDialog(getDriver());
	}

	/**
	 * Change type to sku.
	 */
	public void changeTypeToSku() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(TYPE_SELECTOR_CSS)).click();
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(TYPE_SELECTOR_CSS)).sendKeys("s");
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(TYPE_SELECTOR_CSS)).sendKeys(Keys.RETURN);
	}

	/**
	 * Click select sku image link.
	 *
	 * @return SelectASkuDialog.
	 */
	public SelectASkuDialog clickSelectSkuImageLink() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SELECT_PRODUCT_IMAGE_LINK_CSS)).click();
		return new SelectASkuDialog(getDriver());
	}

	/**
	 * Verify validation error.
	 *
	 * @param errorMsg the error message.
	 */
	public void verifyValidationErrorIsPresent(final String errorMsg) {
		try {
			getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRICE_EDITOR_PARENT_CSS + DIALOG_ERROR_VALIDATION)).click();
			sleep(SLEEP_TIME);
			assertThat(getDriver().getPageSource().contains(errorMsg))
					.as("unable to find error message '" + errorMsg + "'")
					.isTrue();
		} catch (ElementNotVisibleException e) {
			assertThat(false)
					.as("Unable to find error message " + errorMsg)
					.isTrue();
		}
	}

	/**
	 * Enter the quantity.
	 *
	 * @param quantity the quantity
	 */
	public void enterQuantity(final String quantity) {
		for (int i = 1; i < Integer.parseInt(quantity); i++) {
			getWaitDriver().waitForElementToBeClickable(By.cssSelector(INCREASE_QUANTITY_BUTTON_CSS)).click();
		}
	}
}

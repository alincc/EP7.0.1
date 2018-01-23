package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Create Cart Promotion Wizard.
 */
public class CreateCartPromotionWizard extends AbstractWizard {

	private static final String CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS
			= "div[automation-id='com.elasticpath.cmclient.store.promotions.PromotionsMessages.CreatePromotionsWizard_Title'] ";
	private static final String STORE_COMBO_CSS = CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id='Store'][widget-type='CCombo']";

	private static final String NAME_INPUT_CSS = CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id='Promotion Name'] > input";
	private static final String DISPLAY_NAME_INPUT_CSS = CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS
			+ "div[widget-id=''][widget-type='Text'] > input";
	private static final String CONDITION_ICON_CSS
			= CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id='of these conditions are true'][widget-type='ImageHyperlink']";
	private static final String CONDITION_MENU_ITEM_XPATH = "//div[contains(text(), '%s')]";
	private static final String DISCOUNT_ICON_CSS
			= CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id='They get'][widget-type='ImageHyperlink']";
	private static final String DISCOUNT_VALUE_INPUT_CSS = CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS + "div[widget-id*='Get'] > input";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateCartPromotionWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Selects store in combo box.
	 *
	 * @param storeName the store name.
	 */
	public void selectStore(final String storeName) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(STORE_COMBO_CSS));
		assertThat(selectComboBoxItem(STORE_COMBO_CSS, storeName))
				.as("Unable to find store - " + storeName)
				.isTrue();
	}

	/**
	 * Inputs promotion name.
	 *
	 * @param promotionName the promotion name.
	 */
	public void enterPromotionName(final String promotionName) {
		getDriver().findElement(By.cssSelector(NAME_INPUT_CSS)).click();
		clearAndType(getDriver().findElement(By.cssSelector(NAME_INPUT_CSS)), promotionName);
	}

	/**
	 * Inputs promotion display name.
	 *
	 * @param displayName the display name.
	 */
	public void enterPromotionDisplayName(final String displayName) {
		clearAndType(getDriver().findElement(By.cssSelector(DISPLAY_NAME_INPUT_CSS)), displayName);
	}

	/**
	 * Opens condition menu.
	 */
	public void openConditionMenu() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CONDITION_ICON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Selects condition's menu item.
	 *
	 * @param conditionMenuItem the condition menu item.
	 */
	public void selectConditionMenuItem(final String conditionMenuItem) {
		getWaitDriver().waitForElementToBeClickable(By.xpath(String.format(CONDITION_MENU_ITEM_XPATH, conditionMenuItem))).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Opens discount menu and selects menu item.
	 *
	 * @param menuItem the menu item.
	 */
	public void openDiscountMenuAndSelectMenuItem(final String menuItem) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(DISCOUNT_ICON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		scrollDownWithDownArrowKey(getDriver().findElement(By.cssSelector(CREATE_SHOPPING_CART_PROMOTION_PARENT_CSS)), 1);
		getWaitDriver().waitForElementToBeClickable(By.xpath(String.format("//div[text()= '%s']", menuItem))).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Selects discount sub-menu item.
	 *
	 * @param subMenuItem the subMenu item.
	 */
	public void selectDiscountSubMenuItem(final String subMenuItem) {
		getWaitDriver().waitForElementToBeClickable(By.xpath(String.format("//div[text()= '%s']", subMenuItem))).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Inputs discount value.
	 *
	 * @param discountValue the discount value.
	 */
	public void enterDiscountValue(final String discountValue) {
		clearAndType(getDriver().findElement(By.cssSelector(DISCOUNT_VALUE_INPUT_CSS)), discountValue);
	}

}
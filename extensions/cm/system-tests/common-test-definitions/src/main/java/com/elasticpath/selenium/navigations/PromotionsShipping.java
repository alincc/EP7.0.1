package com.elasticpath.selenium.navigations;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.resultspane.PromotionSearchResultPane;
import com.elasticpath.selenium.resultspane.ShippingServiceLevelSearchResultPane;

/**
 * Promotions Shipping.
 */
public class PromotionsShipping extends AbstractNavigation {

	//TODO use proper ID
	private static final String LEFT_PANE_INNER_PARENT_CSS = "div[pane-location='left-pane-inner'] ";
	private static final String PROMOTION_NAME_INPUT_CSS = LEFT_PANE_INNER_PARENT_CSS + "div[widget-id='Promotion Name'] > input";
	private static final String SEARCH_BUTTON_CSS = LEFT_PANE_INNER_PARENT_CSS + "div[widget-id='Search'][seeable='true']";
	private static final String SHIPPING_SERVICE_LEVEL_TAB_CSS = "div[widget-id='Shipping'][appearance-id='ctab-item'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PromotionsShipping(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks on promotion search button.
	 *
	 * @return PromotionSearchResultPane
	 */
	public PromotionSearchResultPane clickPromotionSearchButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SEARCH_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new PromotionSearchResultPane(getDriver());
	}

	/**
	 * Inputs promotion name.
	 *
	 * @param promotionName the promotion name.
	 */
	public void enterPromotionName(final String promotionName) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(PROMOTION_NAME_INPUT_CSS)).click();
		clearAndType(getDriver().findElement(By.cssSelector(PROMOTION_NAME_INPUT_CSS)), promotionName);
	}

	/**
	 * Clicks on shipping service level search button.
	 *
	 * @return ShippingServiceLevelSearchResultPane
	 */
	public ShippingServiceLevelSearchResultPane clickShippingServiceSearchButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SEARCH_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new ShippingServiceLevelSearchResultPane(getDriver());
	}

	/**
	 * Clicks on shipping service level tab.
	 */
	public void clickShippingServiceLevelTab() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SHIPPING_SERVICE_LEVEL_TAB_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}
}

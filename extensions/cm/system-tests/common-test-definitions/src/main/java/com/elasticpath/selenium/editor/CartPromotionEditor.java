package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Cart Promotion Details pane.
 */
public class CartPromotionEditor extends AbstractPageObject {

	private static final String STATE_INPUT_CSS = "div[widget-id='State'] > input";
	private static final String ENABLE_ICON_CSS = "div[widget-type='Button']  > div[style*='1882de9d.png']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CartPromotionEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks to un-check Enable in Store box.
	 */
	public void disableCartPromotion() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(ENABLE_ICON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Verifies promotion state.
	 *
	 * @param state the state.
	 */
	public void verifyPromoState(final String state) {
		assertThat(getDriver().findElement(By.cssSelector(STATE_INPUT_CSS)).getAttribute("value"))
				.as("Cart promotion state validation failed")
				.isEqualTo(state);
	}
}

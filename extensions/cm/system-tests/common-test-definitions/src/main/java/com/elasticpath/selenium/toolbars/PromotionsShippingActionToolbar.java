package com.elasticpath.selenium.toolbars;

import com.elasticpath.selenium.wizards.CreateCartPromotionWizard;
import com.elasticpath.selenium.wizards.CreateCatalogPromotionWizard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Promotions Shipping Toolbar.
 */
public class PromotionsShippingActionToolbar extends AbstractToolbar {

	private static final String APPEARANCE_ID_CSS = "div[appearance-id='toolbar-button']";

	private static final String CREATE_CATALOG_PROMOTION_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Create Catalog Promotion']"; //TODO to be changed
	private static final String CREATE_CART_PROMOTION_BUTTON_CSS = APPEARANCE_ID_CSS
			+ "[widget-id='Create Shopping Cart Promotion']"; //TODO to be changed

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PromotionsShippingActionToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Create Catalog Promotion icon.
	 *
	 * @return CreateCatalogPromotionWizard
	 */
	public CreateCatalogPromotionWizard clickCreateCatalogPromotionButton() {
		getWaitDriver().waitForElementToBeInteractable(CREATE_CATALOG_PROMOTION_BUTTON_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_CATALOG_PROMOTION_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new CreateCatalogPromotionWizard(getDriver());
	}

	/**
	 * Clicks Create Cart Promotion icon.
	 *
	 * @return CreateCartPromotionWizard
	 */
	public CreateCartPromotionWizard clickCreateCartPromotionButton() {
		getWaitDriver().waitForElementToBeInteractable(CREATE_CART_PROMOTION_BUTTON_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_CART_PROMOTION_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new CreateCartPromotionWizard(getDriver());
	}

	/**
	 * Verifies Create Catalog Promotion button is present.
	 */
	public void verifyCreateCatalogPromotionButtonIsPresent() {
		getWaitDriver().waitForPageLoad();
		getWaitDriver().waitForElementToBeInteractable(CREATE_CATALOG_PROMOTION_BUTTON_CSS);
		assertThat(isElementPresent(By.cssSelector(CREATE_CATALOG_PROMOTION_BUTTON_CSS)))
				.as("Unable to find create catalog promotion button")
				.isTrue();
	}

}

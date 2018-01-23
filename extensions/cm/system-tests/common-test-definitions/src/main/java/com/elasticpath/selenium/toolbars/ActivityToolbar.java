package com.elasticpath.selenium.toolbars;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.UserMenuDialog;
import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.navigations.ChangeSet;
import com.elasticpath.selenium.navigations.Configuration;
import com.elasticpath.selenium.navigations.CustomerService;
import com.elasticpath.selenium.navigations.PriceListManagement;
import com.elasticpath.selenium.navigations.PromotionsShipping;
import com.elasticpath.selenium.navigations.ShippingReceiving;

/**
 * Top Navigation class.
 */
public class ActivityToolbar extends AbstractPageObject {

	private static final String TOOLBAR_APPEARANCE_ID_CSS = "div[appearance-id='toolbar-button']";
	private static final String TOOLBAR_CATALOG_MANAGEMENT_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Catalog Management']";
	private static final String TOOLBAR_PRICE_LIST_MANAGER_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Price List Manager']";
	private static final String TOOLBAR_PROMOTIONS_SHIPPING_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Promotions/Shipping']";
	private static final String TOOLBAR_CUSTOMER_SERVICE_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Customer Service']";
	private static final String TOOLBAR_SHIPPING_RECEIVING_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Shipping/Receiving']";
	private static final String TOOLBAR_CONFIGURATION_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Configuration']";
	private static final String TOOLBAR_CHANGE_SET_BUTTON_CSS
			= TOOLBAR_APPEARANCE_ID_CSS + "[widget-id*='Change Set']";
	private static final String SUB_MENU_CHANGE_SET_ENABLED_CSS
			= "div[widget-id='com.elasticpath.cmclient.changeset.openSwitchChangeSet'][widget-type='MenuItem'] div[style*='opacity: 1']";
	private static final String USER_MENU_BUTTON_CSS = "div[widget-id='User Menu']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ActivityToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Wait for the page to be interactable. After it has loaded, and after the Busy indicator is gone.
	 */
	public void waitForPage() {
		getWaitDriver().waitForPageLoad();
		getWaitDriver().waitForElementToBeInteractable("[widget-id='User Menu']");

	}

	/**
	 * Clicks on Configuration button.
	 *
	 * @return Configuration
	 */
	public Configuration clickConfigurationButton() {
		clickButton(TOOLBAR_CONFIGURATION_BUTTON_CSS);
		return new Configuration(getDriver());
	}

	/**
	 * Clicks on Customer Service button.
	 *
	 * @return CustomerService
	 */
	public CustomerService clickCustomerServiceButton() {
		clickButton(TOOLBAR_CUSTOMER_SERVICE_BUTTON_CSS);
		return new CustomerService(getDriver());
	}


	/**
	 * Clicks on Catalog Management button.
	 *
	 * @return CatalogManagement
	 */
	public CatalogManagement clickCatalogManagementButton() {
//		clickButton(TOOLBAR_CATALOG_MANAGEMENT_BUTTON_LOCATOR);
		clickButton(TOOLBAR_CATALOG_MANAGEMENT_BUTTON_CSS);
		return new CatalogManagement(getDriver());
	}

	/**
	 * Clicks on Price List Manager button.
	 *
	 * @return PriceListManagement
	 */
	public PriceListManagement clickPriceListManagementButton() {
		clickButton(TOOLBAR_PRICE_LIST_MANAGER_BUTTON_CSS);
		return new PriceListManagement(getDriver());
	}

	/**
	 * Clicks on Promotions Shipping button.
	 *
	 * @return StoreMarketing
	 */
	public PromotionsShipping clickPromotionsShippingButton() {
		clickButton(TOOLBAR_PROMOTIONS_SHIPPING_BUTTON_CSS);
		return new PromotionsShipping(getDriver());
	}

	/**
	 * Clicks on Shipping/Receiving button.
	 *
	 * @return ShippingReceiving
	 */
	public ShippingReceiving clickShippingReceivingButton() {
		clickButton(TOOLBAR_SHIPPING_RECEIVING_BUTTON_CSS);
		return new ShippingReceiving(getDriver());
	}

	/**
	 * Clicks on Change Set button.
	 *
	 * @return ChangeSet
	 */
	public ChangeSet clickChangeSetButton() {
		clickButton(TOOLBAR_CHANGE_SET_BUTTON_CSS);
		return new ChangeSet(getDriver());
	}

	/**
	 * Verifies Catalog Management button is not present.
	 */
	public void verifyCatalogManagementButtonIsNotPresent() {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_CATALOG_MANAGEMENT_BUTTON_CSS)))
				.as("Catalog Management access is still enabled for restricted user role.")
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Verifies Price List Manager button is not present.
	 */
	public void verifyPriceListManagerButtonIsNotPresent() {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_PRICE_LIST_MANAGER_BUTTON_CSS)))
				.as("Price List Manager access is still enabled for restricted user role.")
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Verifies Configuration button is not present.
	 */
	public void verifyConfigurationButtonIsNotPresent() {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_CONFIGURATION_BUTTON_CSS)))
				.as("Configuration access is still enabled for restricted user role.")
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Verifies Promotions Shipping button is not present.
	 */
	public void verifyPromotionsShippingButtonIsNotPresent() {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_PROMOTIONS_SHIPPING_BUTTON_CSS)))
				.as("Promotions and Shipping access is still enabled for restricted user role.")
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Verifies Shipping Receiving button is not present.
	 */
	public void verifyShippingReceivingButtonIsNotPresent() {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_SHIPPING_RECEIVING_BUTTON_CSS)))
				.as("Shipping Receiving access is still enabled for restricted user role.")
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Verifies Customer Service button is not present.
	 */
	public void verifyCustomerServiceButtonIsNotPresent() {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(isElementPresent(By.cssSelector(TOOLBAR_CUSTOMER_SERVICE_BUTTON_CSS)))
				.as("Customer Service access is still enabled for restricted user role.")
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Verifies if change set is enabled.
	 *
	 * @return boolean
	 */
	public boolean isChangeSetEnabled() {
		getWaitDriver().waitForPageLoad();
		getWaitDriver().adjustWaitInterval(1);
		boolean isEnabled = isElementPresent(By.cssSelector(SUB_MENU_CHANGE_SET_ENABLED_CSS));
		getWaitDriver().adjustWaitBackToDefault();
		return isEnabled;
	}

	/**
	 * Clicks on User Menu button.
	 *
	 * @return UserMenuDialog
	 */
	public UserMenuDialog clickUserMenu() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(USER_MENU_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new UserMenuDialog(getDriver());
	}

	/**
	 * Clicks toolbar button.
	 *
	 * @param cssSelector CSS selector
	 */
	public void clickButton(final String cssSelector) {
		getWaitDriver().waitForElementToBeInteractable(cssSelector);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(cssSelector)).click();
		getDriver().findElement(By.cssSelector("body")).sendKeys(Keys.ESCAPE);
		getWaitDriver().waitForPageLoad();
	}

}

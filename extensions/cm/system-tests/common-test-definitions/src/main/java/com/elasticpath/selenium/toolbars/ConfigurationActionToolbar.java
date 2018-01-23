package com.elasticpath.selenium.toolbars;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.navigations.UserSearch;
import com.elasticpath.selenium.resultspane.UserRolesResultPane;
import com.elasticpath.selenium.resultspane.WarehousesPane;

/**
 * Configuration Toolbar.
 */
public class ConfigurationActionToolbar extends AbstractToolbar {

	private static final String USER_ROLES_LINK_CSS = "div[widget-id='User Roles'][widget-type='ImageHyperlink'][seeable='true']";
	private static final String WAREHOUSE_LINK_CSS = "div[widget-id='Warehouses'][widget-type='ImageHyperlink'][seeable='true']";
	private static final String USERS_LINK_CSS = "div[widget-id='Users'][widget-type='ImageHyperlink'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ConfigurationActionToolbar(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks on User Roles link.
	 *
	 * @return UserRolesResultPane
	 */
	public UserRolesResultPane clickUserRoles() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(USER_ROLES_LINK_CSS)).click();
		return new UserRolesResultPane(getDriver());
	}

	/**
	 * Clicks on Warehouse link.
	 *
	 * @return WarehousePane
	 */
	public WarehousesPane clickWarehouses() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(WAREHOUSE_LINK_CSS)).click();
		return new WarehousesPane(getDriver());
	}

	/**
	 * Verifies User Roles link is present.
	 */
	public void verifyUserRolesLinkIsPresent() {
		getWaitDriver().waitForPageLoad();
		assertThat(isElementPresent(By.cssSelector(USER_ROLES_LINK_CSS)))
				.as("Unable to find User Roles link")
				.isTrue();
	}

	/**
	 * Clicks on Users link.
	 *
	 * @return UserSearch
	 */
	public UserSearch clickUsers() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(USERS_LINK_CSS)).click();
		return new UserSearch(getDriver());
	}

}

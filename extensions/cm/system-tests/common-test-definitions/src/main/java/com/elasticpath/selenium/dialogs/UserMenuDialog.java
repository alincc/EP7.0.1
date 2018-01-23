package com.elasticpath.selenium.dialogs;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * User menu dialog.
 */
public class UserMenuDialog extends AbstractDialog {

	private static final String LOGOUT_CSS = "div[appearance-id='menu'] div[seeable='true'][widget-id='Logout']";
	private static final String CHANGE_PAGINATION_CSS = "div[appearance-id='menu'] div[seeable='true'][widget-id='Change Pagination Settings']";
	private static final String CHANGE_PASSWORD_CSS = "div[appearance-id='menu'] div[seeable='true'][widget-id='Change Password']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public UserMenuDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * @return SignInDialog
	 */
	public SignInDialog clickLogout() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(LOGOUT_CSS)).click();
		return new SignInDialog(getDriver());
	}

	/**
	 *
	 */
	public void clickChangePagination() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CHANGE_PAGINATION_CSS)).click();
	}

	/**
	 * @return ChangePasswordDialog
	 */
	public ChangePasswordDialog clickChangePassword() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CHANGE_PASSWORD_CSS)).click();
		return new ChangePasswordDialog(getDriver());
	}
}
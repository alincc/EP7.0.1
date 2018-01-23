package com.elasticpath.selenium.dialogs;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.CM;

/**
 * Sign In Page.
 */
public class SignInDialog extends AbstractDialog {

	private static final String USERNAME_INPUT_CSS = "div[widget-id='User ID'] input";
	private static final String PASSWORD_INPUT_CSS = "div[widget-id='Password'] input";
	private static final String SIGN_IN_BUTTON_CSS = "div[widget-id='Sign In']";
	private static final String ERROR_MSG_XPATH = "//div[text()= 'Authentication failed. Check your user ID and password, then try again"
			+ ".'][contains(@style, 'overflow: hidden')]";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public SignInDialog(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Enters username value.
	 *
	 * @param username the username.
	 */
	public void enterUsername(final String username) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(USERNAME_INPUT_CSS)), username);
	}

	/**
	 * Enters password value.
	 *
	 * @param password the password.
	 */
	public void enterPassword(final String password) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PASSWORD_INPUT_CSS)), password);
	}

	/**
	 * Clicks on Sign In.
	 *
	 * @return the CM.
	 */
	public CM clickSignIn() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SIGN_IN_BUTTON_CSS)).click();
		return new CM(getDriver());
	}

	/**
	 * Verifies signin failed.
	 */
	public void verifySignInFailed() {
		getWaitDriver().waitForPageLoad();
		assertThat(isElementPresent(By.xpath(ERROR_MSG_XPATH)))
				.as("Expected sign in error message is not present.")
				.isTrue();
	}
}

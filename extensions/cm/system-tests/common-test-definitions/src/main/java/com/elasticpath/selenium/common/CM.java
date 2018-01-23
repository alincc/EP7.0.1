package com.elasticpath.selenium.common;

import com.elasticpath.selenium.dialogs.SignInDialog;

import org.openqa.selenium.WebDriver;

/**
 * CM Object. Blank page before each page loads.
 */
public class CM extends AbstractPageObject {
	/**
	 * constructor.
	 *
	 * @param driver WebDriver.
	 */
	public CM(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Launch CM.
	 *
	 * @return SignInDialog.
	 */
	public SignInDialog openCM() {
		getDriver().get(getSiteURL());
		return new SignInDialog(getDriver());
	}

}

package com.elasticpath.selenium.navigations;

import com.elasticpath.selenium.common.AbstractPageObject;

import org.openqa.selenium.WebDriver;

/**
 * Abstract Navigation for common navigation actions.
 */
public abstract class AbstractNavigation extends AbstractPageObject {

	/**
	 * constructor.
	 *
	 * @param driver WebDriver which drives this webpage.
	 */
	public AbstractNavigation(final WebDriver driver) {
		super(driver);
	}

}

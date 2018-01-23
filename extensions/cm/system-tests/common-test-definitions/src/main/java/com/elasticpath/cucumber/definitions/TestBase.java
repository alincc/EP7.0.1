package com.elasticpath.cucumber.definitions;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;

/**
 * Test Base class contains some methods for Before and After tests to be run for each test.
 */
public class TestBase {

	/**
	 * Provides scenario result.
	 *
	 * @param scenario Scenario
	 */
//	The order indicate this will be execute later if the other @After order has higher value.
	@After(order = 1)
	public void tearDown(final Scenario scenario) {
		AbstractPageObject.setIsMaximized(false);
		if (scenario.isFailed()) {
			final byte[] screenshot = ((TakesScreenshot) SeleniumDriverSetup.getDriver()).getScreenshotAs(OutputType.BYTES);
			scenario.embed(screenshot, "image/png");
		}
		SeleniumDriverSetup.quitDriver();
	}
}

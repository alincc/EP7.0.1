package com.elasticpath.selenium.util;

/**
 * Extended WaitDriver class to use FluentWait.
 */

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import com.elasticpath.selenium.framework.pages.WaitDriver;
import com.elasticpath.selenium.framework.util.PropertyManager;

/**
 * WebDriverWait helper method class.
 */
public class FluentWaitDriver extends WaitDriver {

	private static final int POLLING_INTERVAL = 500;

	private static final int DEFAULT_TIMEOUT_INTERVAL = 60;

	private final FluentWait<WebDriver> wait;

	private final JavascriptExecutor jsDriver;

	private static final Logger LOGGER = Logger.getLogger(FluentWaitDriver.class);

	/**
	 * Constructor.
	 *
	 * @param driver The WebDriver
	 */
	public FluentWaitDriver(final WebDriver driver) {
		super(driver);
		int timeout = getWaitTimeout();
		wait = new FluentWait<WebDriver>(driver)
				.withTimeout(timeout, TimeUnit.SECONDS)
				.pollingEvery(POLLING_INTERVAL, TimeUnit.MILLISECONDS)
				.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);

		//set up the js driver.
		driver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS);
		jsDriver = (JavascriptExecutor) driver;
	}

	private int getWaitTimeout() {
		try {
			return Integer.parseInt(PropertyManager.getInstance().getProperty("selenium.waitdriver.timeout"));
		} catch (NumberFormatException e) {
			return DEFAULT_TIMEOUT_INTERVAL;
		}
	}

	/**
	 * Waits for an element that is expected to be visible multiple times and returns it, such as a row or list of buttons.
	 *
	 * @param findBy Search method to search for the element
	 * @return The list of elements that are displayed
	 */
	public List<WebElement> waitForElementsListVisible(final By findBy) {
		waitForPageLoad();
		return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(findBy));
	}

	/**
	 * Waits for element list that is expected to be visible multiple times and returns it, such as a row or list of buttons.
	 *
	 * @param elements Search method to search for the element
	 * @return The list of elements that are displayed
	 */
	public List<WebElement> waitForElementsListVisible(final List<WebElement> elements) {
		waitForPageLoad();
		return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
	}

	/**
	 * Waits for one or more elements that is expected to be present once or multiple times and returns them, such as a row or list of buttons.
	 *
	 * @param findBy Search method to search for the element
	 * @return The list of elements that are present
	 */
	public List<WebElement> waitForElementsListPresent(final By findBy) {
		waitForPageLoad();
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(findBy));
	}

	/**
	 * Waits until an element is clickable.
	 *
	 * @param findBy the FindBy condition
	 * @return the webelement that is clickable
	 */
	public WebElement waitForElementToBeClickable(final By findBy) {
		waitForPageLoad();
		return wait.until(ExpectedConditions.elementToBeClickable(findBy));
	}

	/**
	 * Wait until an element is clickable.
	 *
	 * @param element Webelement
	 * @return WebElement
	 */
	public WebElement waitForElementToBeClickable(final WebElement element) {
		waitForPageLoad();
		return wait.until(ExpectedConditions.elementToBeClickable(element));
	}

	/**
	 * Waits until an element is present.
	 *
	 * @param findBy the FindBy condition
	 * @return the webelement that is visible
	 */
	public WebElement waitForElementToBePresent(final By findBy) {
		waitForPageLoad();
		return wait.until(ExpectedConditions.presenceOfElementLocated(findBy));
	}

	/**
	 * Waits until an element is visible.
	 *
	 * @param findBy the FindBy condition
	 * @return the webelement that is visible
	 */
	public WebElement waitForElementToBeVisible(final By findBy) {
		waitForPageLoad();
		return wait.until(ExpectedConditions.visibilityOfElementLocated(findBy));
	}

	/**
	 * Waits until an element is invisible.
	 *
	 * @param findBy the FindBy condition
	 * @return the webelement if the element is invisible
	 */
	public Boolean waitForElementToBeInvisible(final By findBy) {
		waitForPageLoad();
		return wait.until(ExpectedConditions.refreshed(ExpectedConditions.invisibilityOfElementLocated(findBy)));
	}

	/**
	 * Waits until an element is interactable (IE, it is the top element in the DOM for its given position).
	 * This is useful to determine whether an element is covered by a busy indicator, or dialog box,
	 * where it is still visible, but will not receive a click event.
	 * <p>
	 * Example Usage: waitForElementToBeInteractable("[widget-id='User Menu']");
	 *
	 * @param elementSelector The element's CSS selector.
	 */
	public void waitForElementToBeInteractable(final String elementSelector) {
		wait.until(webDriver -> (Boolean) jsDriver.executeScript(" return EPTest.isElementInteractable(\"" + elementSelector + "\");"));
	}

	/**
	 * Wait for DOM loading to be complete before any action.
	 */
	public void waitForPageLoad() {

		for (int i = 0; i < DEFAULT_TIMEOUT_INTERVAL; i++) {
			try {
				Thread.sleep(POLLING_INTERVAL);
			} catch (InterruptedException e) {
				LOGGER.debug(e);
			}
			if (("complete").equals(jsDriver.executeScript("return document.readyState").toString())) {
				break;
			}
		}
	}

	/**
	 * Waits for text in input.
	 *
	 * @param webElement   the webelement
	 * @param expectedText the expected text
	 */
	public void waitForTextInInput(final WebElement webElement, final String expectedText) {

		for (int i = 0; i < DEFAULT_TIMEOUT_INTERVAL; i++) {
			try {
				Thread.sleep(POLLING_INTERVAL);
			} catch (InterruptedException e) {
				LOGGER.debug(e);
			}

			if (webElement.getAttribute("value").contains(expectedText)) {
				break;
			}
		}
	}

}

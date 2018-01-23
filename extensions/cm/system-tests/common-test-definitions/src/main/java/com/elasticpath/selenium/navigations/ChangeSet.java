package com.elasticpath.selenium.navigations;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.resultspane.ChangeSetSearchResultPane;

/**
 * Change Set.
 */
public class ChangeSet extends AbstractNavigation {

	private static final String LEFT_PANE_INNER_CSS = "div[pane-location='left-pane-inner'][seeable='true'] ";
	private static final String SEARCH_BUTTON_CSS
			= LEFT_PANE_INNER_CSS + "div[automation-id='com.elasticpath.cmclient.changeset.ChangeSetMessages.ChangeSetSearchView_SearchButton']";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ChangeSet(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks Search button.
	 *
	 * @return ChangeSetSearchResultPane
	 */
	public ChangeSetSearchResultPane clickSearchButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(SEARCH_BUTTON_CSS)).click();
		return new ChangeSetSearchResultPane(getDriver());
	}

}

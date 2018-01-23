package com.elasticpath.selenium.common;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.elasticpath.selenium.framework.pages.AbstractPage;
import com.elasticpath.selenium.util.FluentWaitDriver;

/**
 * Page super class. All page objects in this test framework extends the page super class.
 */
@SuppressWarnings({"PMD.GodClass"})
public abstract class AbstractPageObject extends AbstractPage {

	private static final Logger LOGGER = Logger.getLogger(AbstractPageObject.class);
	/**
	 * The Tab CSS.
	 */
	protected static final String TAB_CSS = "div[widget-id='%s'][widget-type='CTabItem']";
	private final String siteURL;
	private final FluentWaitDriver fluentWaitDriver;
	private WebElement selectedElement;
	/**
	 * Checks if window is maximized.
	 */
	private static boolean maximized;
	private static final String CENTER_PANE_FIRST_BUTTON_CSS = "div[widget-id='First Page'][appearance-id='toolbar-button'][seeable='true']";
	private static final String CENTER_PANE_NEXT_BUTTON_CSS = "div[widget-id='Next Page'][appearance-id='toolbar-button'][seeable='true']";
	private static final String CATALOG_TREE_PARENT_CSS = "div[pane-location='left-pane-inner'] div[widget-id='Catalog Browse "
			+ "Tree'][widget-type='Tree'] ";
	private static final String CATALOG_TREE_ITEM_CSS = CATALOG_TREE_PARENT_CSS + "div[row-id='%1$s'] div[column-id='%1$s']";
	private static final String CLOSE_PANE_ICON_CSS = "div[widget-id='%s'][active-tab='true'] > div[style*='close.gif']";
	private static final int TIME_TO_SLEEP = 1000;
	private static final int MAX_WAIT_TIME = 5;

	/**
	 * Is browser maximized.
	 *
	 * @return the browser maximized state.
	 */
	public boolean isMaximized() {
		return maximized;
	}

	/**
	 * Sets browser maximized state.
	 *
	 * @param isMaximized the value.
	 */
	public static void setIsMaximized(final boolean isMaximized) {
		AbstractPageObject.maximized = isMaximized;
	}

	/**
	 * constructor.
	 *
	 * @param driver WebDriver which drives this webpage.
	 */
	public AbstractPageObject(final WebDriver driver) {
		super(driver);

		if (!maximized) {
			driver.manage().window().maximize();
			setIsMaximized(true);
			LOGGER.debug("maximizing window");
		}


		fluentWaitDriver = new FluentWaitDriver(driver);
		super.setWaitDriver(fluentWaitDriver);

		siteURL = getPropertyManager().getProperty("selenium.session.baseurl");
		getWaitDriver().waitForPageLoad();
	}

	@Override
	public void afterInit() {
		// Do nothing
	}

	/**
	 * Get site url.
	 *
	 * @return the url.
	 */
	public String getSiteURL() {
		return siteURL;
	}

	@Override
	public FluentWaitDriver getWaitDriver() {
		return fluentWaitDriver;
	}

	@Override
	public void clearAndType(final WebElement element, final String text) {
		getWaitDriver().waitForPageLoad();
		element.clear();

		if (text != null && !text.isEmpty()) {
			element.sendKeys(text);
		}

		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Sleep for a number of milliseconds.
	 *
	 * @param mills number of milliseconds.
	 */
	public void sleep(final long mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Sroll up with arrow key.
	 *
	 * @param element        The element.
	 * @param numberOfUpKeys Number of keys.
	 */
	public void scrollUpWithUpArrowKey(final WebElement element, final int numberOfUpKeys) {
		for (int i = 0; i < numberOfUpKeys; i++) {
			element.sendKeys(Keys.ARROW_UP);
		}
	}

	/**
	 * Scroll down the table.
	 *
	 * @param element          Webelement.
	 * @param numberOfDownKeys the number of keys.
	 */
	protected void scrollDownWithDownArrowKey(final WebElement element, final int numberOfDownKeys) {
		for (int i = 0; i < numberOfDownKeys; i++) {
			element.sendKeys(Keys.ARROW_DOWN);
		}
	}

	/**
	 * Close the Pane.
	 *
	 * @param textId The text id if the close pane icon.
	 */
	public void closePane(final String textId) {
		getDriver().findElement(By.cssSelector(String.format(CLOSE_PANE_ICON_CSS, textId))).click();
	}

	/**
	 * Checks if button is enabled.
	 *
	 * @param buttonCss button css
	 * @return boolean
	 */
	public boolean isButtonEnabled(final String buttonCss) {
		return (boolean) ((JavascriptExecutor) getDriver()).executeScript(String.format("return EPTest.isButtonEnabled(\"%s\");", buttonCss));
	}

	private boolean scrollToTableListItem(final String parentCss, final String value, final String columnName) {
		return (boolean) ((JavascriptExecutor) getDriver())
				.executeScript(String.format("return EPTest.scrollToTableItemWithText(\"%s\",\"%s\",\"%s\");", parentCss, value, columnName));
	}

	private void scrollToComboItem(final String comboParentCss, final String value) {
		((JavascriptExecutor) getDriver()).executeScript(String.format("EPTest.scrollToComboItemWithText(\"%s\",\"%s\");", comboParentCss, value));
	}

	/**
	 * Scrolls widget into view.
	 *
	 * @param cssString The css string.
	 */
	protected void scrollWidgetIntoView(final String cssString) {
		((JavascriptExecutor) getDriver()).executeScript(String.format("EPTest.scrollWidgetIntoView(\"%s\");", cssString));
	}

	/**
	 * Selects item in center pane with pagination.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column number.
	 * @return true if value exists.
	 */
	public boolean selectItemInCenterPane(final String tableParentCss, final String tableColumnCss, final String value, final String columnName) {
		getWaitDriver().waitForPageLoad();
		boolean valueExists = false;
		boolean isNextButtonEnabled = true;
		getWaitDriver().waitForElementToBeInteractable(CENTER_PANE_FIRST_BUTTON_CSS);
		WebElement firstButton = getDriver().findElement(By.cssSelector(CENTER_PANE_FIRST_BUTTON_CSS));


		if (isButtonEnabled(CENTER_PANE_FIRST_BUTTON_CSS)) {
			firstButton.click();
			getWaitDriver().waitForPageLoad();
		}

		while (isNextButtonEnabled) {
			sleep(TIME_TO_SLEEP);
			if (scrollToTableListItem(tableParentCss, value, columnName)) {

				getWaitDriver().adjustWaitInterval(1);
				if (isElementPresent(By.cssSelector(String.format(tableColumnCss, value)))) {
					WebElement element = getDriver().findElement(By.cssSelector(String.format(tableColumnCss, value)));
					if (value.equals(element.getText())) {
						valueExists = true;
						element.click();
						this.selectedElement = element;
						break;
					}
				}
			} else {
				isNextButtonEnabled = isButtonEnabled(CENTER_PANE_NEXT_BUTTON_CSS);
				if (isNextButtonEnabled) {
					getDriver().findElement(By.cssSelector(CENTER_PANE_NEXT_BUTTON_CSS)).click();
					getWaitDriver().waitForPageLoad();
				} else {
					break;
				}
				getWaitDriver().adjustWaitBackToDefault();

			}
			getWaitDriver().waitForPageLoad();
		}


		return valueExists;
	}

	/**
	 * Selects item in center pane without pagination .
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column number.
	 * @return true if selected item.
	 */
	public boolean selectItemInCenterPaneWithoutPagination(final String tableParentCss, final String tableColumnCss, final String value,
														   final String columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, true);
	}

	/**
	 * Selects item in editor pane without scrollbar.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column name.
	 * @return true if selected item.
	 */
	public boolean selectItemInEditorPane(final String tableParentCss, final String tableColumnCss, final String value, final String columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, false);
	}

	/**
	 * Selects item in editor pane without scrollbar.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @return true if selected item.
	 */
	public boolean selectItemInEditorPaneWithScrollBar(final String tableParentCss, final String tableColumnCss, final String value) {
		return selectItem(tableParentCss, tableColumnCss, value, "Name", true);
	}

	/**
	 * Selects item in editor pane without scrollbar.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the coulumn name.
	 * @return true if selected item.
	 */
	public boolean selectItemInEditorPaneWithScrollBar(final String tableParentCss, final String tableColumnCss, final String value, final String
			columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, true);
	}

	/**
	 * Selects item in dialog with.
	 *
	 * @param tableParentCss the table parent css.
	 * @param tableColumnCss the table column css.
	 * @param value          the value.
	 * @param columnName     the column number.
	 * @return true if selected item.
	 */
	public boolean selectItemInDialog(final String tableParentCss, final String tableColumnCss, final String value, final String columnName) {
		return selectItem(tableParentCss, tableColumnCss, value, columnName, true);
	}

	/**
	 * Selects item in dialog and pane.
	 *
	 * @param tableParentCss     the table parent css.
	 * @param tableColumnCss     the table column css.
	 * @param value              the value.
	 * @param columnName         the column name.
	 * @param isScrollBarPresent is scrollbar present.
	 * @return true if selected item.
	 */
	private boolean selectItem(final String tableParentCss, final String tableColumnCss, final String value,
							   final String columnName, final boolean isScrollBarPresent) {
		String cleanedValue = value.replace("'", "\\'");
		boolean valueExists = false;
		if (isScrollBarPresent) {
			scrollToTableListItem(tableParentCss, cleanedValue, columnName);
		}
		if (isElementPresent(By.cssSelector(String.format(tableColumnCss, cleanedValue)))) {
			WebElement element = getDriver().findElement(By.cssSelector(String.format(tableColumnCss, cleanedValue)));
			if (cleanedValue.equals(element.getText().replace("'", "\\'"))) {
				valueExists = true;
				element.click();
				this.selectedElement = element;
			}
		}
		getWaitDriver().waitForPageLoad();
		return valueExists;
	}

	/**
	 * Selects item from combo box.
	 *
	 * @param comboBoxParent the combo box parent css.
	 * @param value          the value.
	 * @return true if selected.
	 */
	public boolean selectComboBoxItem(final String comboBoxParent, final String value) {
		scrollToComboItem(comboBoxParent, value);
		WebElement element = getDriver().findElement(By.cssSelector(comboBoxParent + " input"));
		if (value.equals(element.getAttribute("value").trim())) {
			this.selectedElement = element;
			return true;
		}
		getWaitDriver().waitForPageLoad();
		return false;
	}

	/**
	 * Selects item from catalog tree.
	 *
	 * @param catalogTreeItem the catalog tree item.
	 * @return true if selected.
	 */
	public boolean selectCatalogTreeItem(final String catalogTreeItem) {
		boolean itemExists = false;
		if (isElementPresent(By.cssSelector(String.format(CATALOG_TREE_ITEM_CSS, catalogTreeItem)))) {
			WebElement treeItem = getDriver().findElement(By.cssSelector(String.format(CATALOG_TREE_ITEM_CSS, catalogTreeItem)));
			if (catalogTreeItem.equals(treeItem.getText())) {
				this.selectedElement = treeItem;
				treeItem.click();
				itemExists = true;
			}
		}
		getWaitDriver().waitForPageLoad();
		return itemExists;
	}

	/**
	 * Mouse double click.
	 *
	 * @param element the WebElement.
	 */
	public void doubleClick(final WebElement element) {
		element.click();
		Actions actions = new Actions(getDriver());
		actions.doubleClick(element).build().perform();
	}

	/**
	 * Mouse right click.
	 */
	public void rightClick() {
		Actions actions = new Actions(getDriver());
		actions.contextClick().build().perform();
	}

	/**
	 * Get selected element.
	 *
	 * @return the web element.
	 */
	public WebElement getSelectedElement() {
		return this.selectedElement;
	}

	/**
	 * Click on Combo Box.
	 *
	 * @param by the element selector
	 * @return The element that has been clicked.
	 */
	@SuppressWarnings({"PMD.ShortVariable"})
	public WebElement clickOnComboBox(final By by) {
		WebElement comboBox = getWaitDriver().waitForElementToBeClickable(by);
		comboBox.click();
		getWaitDriver().waitForPageLoad();
		return comboBox;
	}

	/**
	 * Clear field.
	 *
	 * @param element the field to clear.
	 */
	public void clearField(final WebElement element) {
		element.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE));
	}

	/**
	 * Waits till element is not visible.
	 *
	 * @param by the element selector
	 */
	@SuppressWarnings({"PMD.ShortVariable"})
	public void waitTillElementDisappears(final By by) {
		int i = 0;
		getWaitDriver().adjustWaitInterval(1);
		boolean isElementPresent = isElementPresent(by);
		while (isElementPresent && i < MAX_WAIT_TIME) {
			try {
				Thread.sleep(TIME_TO_SLEEP);
				i++;
				isElementPresent = isElementPresent(by);
				LOGGER.info(i + ": waiting for element to disappear");
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage());
			}
		}
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Clicks on tab to select it.
	 *
	 * @param tabName the tab name.
	 */
	public void selectTab(final String tabName) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(TAB_CSS, tabName))).click();
		getWaitDriver().waitForPageLoad();
	}


}

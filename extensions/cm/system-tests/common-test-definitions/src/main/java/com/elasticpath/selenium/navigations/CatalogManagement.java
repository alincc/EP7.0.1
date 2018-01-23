package com.elasticpath.selenium.navigations;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.editor.CatalogEditor;
import com.elasticpath.selenium.editor.CategoryEditor;
import com.elasticpath.selenium.resultspane.CatalogProductListingPane;
import com.elasticpath.selenium.resultspane.CatalogSearchResultPane;
import com.elasticpath.selenium.wizards.CreateBundleWizard;
import com.elasticpath.selenium.wizards.CreateCategoryWizard;
import com.elasticpath.selenium.wizards.CreateProductWizard;

/**
 * Catalog Management Page.
 */
public class CatalogManagement extends AbstractNavigation {

	private static final String LEFT_PANE_INNER_PARENT_CSS = "div[pane-location='left-pane-inner'] ";
	private static final String CATALOG_BROWSE_TREE_PARENT_CSS
			= LEFT_PANE_INNER_PARENT_CSS + "div[widget-id='Catalog Browse Tree'][widget-type='Tree'] ";
	private static final String CATALOG_BROWSE_TREE_ITEM_CSS = CATALOG_BROWSE_TREE_PARENT_CSS + "div[widget-id='%s'][widget-type='TreeItem']";
	private static final String CATALOG_EXPAND_ICON_CSS = CATALOG_BROWSE_TREE_ITEM_CSS + " div[expand-icon='']";
	private static final String CREATE_PRODUCT_BUTTON_CSS = "div[widget-id='Create Product'][widget-type='ToolItem']";
	private static final String CREATE_BUNDLE_BUTTON_CSS = "div[widget-id='Create Bundle'][widget-type='ToolItem']";
	// removed [seeable='true']
	private static final String PRODUCT_NAME_SEARCH_INPUT_CSS = LEFT_PANE_INNER_PARENT_CSS + "div[widget-id='Product Name'] > input";
	private static final String DELETE_CSS = "div[widget-id='Delete'][seeable='true']";
	private static final String RIGHT_CLICK_DELETE_CSS = "div[appearance-id='menu'] div[widget-id='Delete'][seeable='true']";
	private static final String OPEN_CATALOG_CATEGORY_EDITOR_ICON_CSS
			= "div[widget-id='Catalog Browse ToolBar'][widget-type='ToolBar'] div[widget-id='Open...']";
	private static final String CATALOG_BROWSE_TOOLBAR = "div[widget-id='Catalog Browse ToolBar'][widget-type='ToolBar'] ";
	private static final String CATALOG_SEARCH_TAB_CSS = "div[widget-id*='Search'][appearance-id='ctab-item'][seeable='true']";
	private static final String PRODUCT_SKU_SEARCH_BUTTON_CSS = LEFT_PANE_INNER_PARENT_CSS + "div[widget-id='Search'][seeable='true']";
	private static final String CREATE_CATEGORY_BUTTON_CSS = CATALOG_BROWSE_TOOLBAR + "div[widget-id='Create Category'][seeable='true']";
	private static final String OPEN_CATALOG_CATEGORY_BUTTON_CSS
			= "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.CatalogBrowseView_Action_OpenCatalogCategory']"
			+ "[widget-type='ToolItem']";
	private static final int SLEEP_TIME_IN_MILLI = 1000;


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CatalogManagement(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Clicks on Catalog Search tab.
	 */
	public void clickCatalogSearchTab() {
		getWaitDriver().waitForElementToBeInteractable(CATALOG_SEARCH_TAB_CSS);
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CATALOG_SEARCH_TAB_CSS)).click();
	}

	/**
	 * Enters product name for search.
	 *
	 * @param productName the product name.
	 */
	public void enterProductName(final String productName) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRODUCT_NAME_SEARCH_INPUT_CSS)), productName);
	}

	/**
	 * Clicks on catalog search.
	 *
	 * @return CatalogSearchResultPane the search result pane.
	 */
	public CatalogSearchResultPane clickCatalogSearch() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(PRODUCT_SKU_SEARCH_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new CatalogSearchResultPane(getDriver());
	}

	/**
	 * Expand the catalog.
	 *
	 * @param catalogName the catalog name.
	 */
	public void expandCatalog(final String catalogName) {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(String.format(CATALOG_EXPAND_ICON_CSS, catalogName))).click();
		sleep(SLEEP_TIME_IN_MILLI);
		getWaitDriver().waitForPageLoad();
	}

	/**
	 * Verify catalog exists.
	 *
	 * @param catalogName the catalog name.
	 */
	public void verifyCatalogExists(final String catalogName) {
		assertThat(selectCatalogTreeItem(catalogName))
				.as("Unable to find catalog - " + catalogName)
				.isTrue();
	}

	/**
	 * Verify virtual catalog exits.
	 *
	 * @param catalogName the catalog name.
	 */
	public void verifyVirtualCatalogExists(final String catalogName) {
		assertThat(selectCatalogTreeItem(catalogName))
				.as("Unable to find virtual catalog - " + catalogName)
				.isTrue();
	}

	/**
	 * Select catalog.
	 *
	 * @param catalogName the catalog name.
	 */
	public void selectCatalog(final String catalogName) {
		assertThat(selectCatalogTreeItem(catalogName))
				.as("Unable to find catalog - " + catalogName)
				.isTrue();
	}

	/**
	 * Select category in catalog.
	 *
	 * @param catalogName  the catalog name.
	 * @param categoryName the category name.
	 */
	public void selectCategoryInCatalog(final String catalogName, final String categoryName) {
		expandCatalog(catalogName);
		assertThat(selectCatalogTreeItem(categoryName))
				.as("Unable to find category - " + categoryName)
				.isTrue();
	}

	/**
	 * Select category.
	 *
	 * @param categoryName the category name.
	 */
	public void selectCategory(final String categoryName) {
		assertThat(selectCatalogTreeItem(categoryName))
				.as("Unable to find category - " + categoryName)
				.isTrue();
	}

	/**
	 * Clicks Create Product icon.
	 *
	 * @return CreateProductWizard the wizard.
	 */
	public CreateProductWizard clickCreateProductButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_PRODUCT_BUTTON_CSS)).click();
		return new CreateProductWizard(getDriver());
	}

	/**
	 * Clicks Create Bundle icon.
	 *
	 * @return CreateBundleWizard the wizard.
	 */
	public CreateBundleWizard clickCreateBundleButton() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_BUNDLE_BUTTON_CSS)).click();
		return new CreateBundleWizard(getDriver());
	}

	/**
	 * Right click and select 'Delete'.
	 *
	 * @return The confirm dialog
	 */
	public ConfirmDialog rightClickDelete() {
		getWaitDriver().waitForPageLoad();
		rightClick();
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(RIGHT_CLICK_DELETE_CSS)).click();
		return new ConfirmDialog(getDriver());
	}

	/**
	 * Right click and select 'Delete' will display unable to delete error dialog.
	 */
	public void rightClickDeleteDisplaysError() {
		getWaitDriver().waitForPageLoad();
		rightClick();
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(RIGHT_CLICK_DELETE_CSS)).click();
	}

	/**
	 * Verifies Catalog is not in the list.
	 *
	 * @param catalogName the catalog name.
	 */
	public void verifyCatalogIsDeleted(final String catalogName) {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(selectCatalogTreeItem(catalogName))
				.as("Delete failed, catalog is still in the list - " + catalogName)
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Clicks Create Category icon.
	 *
	 * @return the category wizard.
	 */
	public CreateCategoryWizard clickCreateCategoryIcon() {
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_CATEGORY_BUTTON_CSS)).click();
		return new CreateCategoryWizard(getDriver());
	}

	/**
	 * Right click and select create category menu item.
	 *
	 * @return the category wizard.
	 */
	public CreateCategoryWizard rightClickAndSelectCreateCategory() {
		rightClick();
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(CREATE_CATEGORY_BUTTON_CSS)).click();
		return new CreateCategoryWizard(getDriver());
	}

	/**
	 * Double click category.
	 *
	 * @param categoryName the category name.
	 * @return the pane.
	 */
	public CatalogProductListingPane doubleClickCategory(final String categoryName) {
		getWaitDriver().waitForElementToBeInteractable(String.format(CATALOG_BROWSE_TREE_ITEM_CSS, categoryName));
		doubleClick(getDriver().findElement(By.cssSelector(String.format(CATALOG_BROWSE_TREE_ITEM_CSS, categoryName))));
		getWaitDriver().waitForPageLoad();
		return new CatalogProductListingPane(getDriver());
	}

	/**
	 * Verify if category exists.
	 *
	 * @param categoryName the category name.
	 */
	public void verifyCategoryExists(final String categoryName) {
		assertThat(selectCatalogTreeItem(categoryName))
				.as("Unable to find category -  " + categoryName)
				.isTrue();
	}

	/**
	 * Clicks Catalog/Category Delete icon.
	 *
	 * @return the confirm dialog.
	 */
	public ConfirmDialog clickDeleteCategoryIcon() {
		rightClick();
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(DELETE_CSS)).click();
		getWaitDriver().waitForPageLoad();
		return new ConfirmDialog(getDriver());
	}

	/**
	 * Verifies Category is deleted.
	 *
	 * @param categoryName the category name.
	 */
	public void verifyCategoryIsNotInList(final String categoryName) {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(selectCatalogTreeItem(categoryName))
				.as("Delete failed, Category is still present - " + categoryName)
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Clicks Open Category Editor icon.
	 *
	 * @return the category editor.
	 */
	public CategoryEditor clickOpenCategoryIcon() {
		getWaitDriver().waitForElementToBeVisible(By.cssSelector(OPEN_CATALOG_CATEGORY_EDITOR_ICON_CSS)).click();
		return new CategoryEditor(getDriver());
	}

	/**
	 * Clicks Open Catalog/Category button.
	 *
	 * @return The catalog editor
	 */
	public CatalogEditor clickOpenCatalogCategoryButton() {
		getWaitDriver().waitForPageLoad();
		getWaitDriver().waitForElementToBeClickable(By.cssSelector(OPEN_CATALOG_CATEGORY_BUTTON_CSS)).click();
		return new CatalogEditor(getDriver());
	}

}

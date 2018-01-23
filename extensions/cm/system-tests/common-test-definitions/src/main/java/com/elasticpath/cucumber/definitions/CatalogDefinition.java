package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.AddAttributeDialog;
import com.elasticpath.selenium.dialogs.AddCategoryTypeDialog;
import com.elasticpath.selenium.dialogs.AddItemDialog;
import com.elasticpath.selenium.dialogs.BasePriceEditorDialog;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateCatalogDialog;
import com.elasticpath.selenium.dialogs.CreateVirtualCatalogDialog;
import com.elasticpath.selenium.dialogs.EditAttributeDialog;
import com.elasticpath.selenium.dialogs.EditCategoryTypeDialog;
import com.elasticpath.selenium.dialogs.EditGlobalAttributesDialog;
import com.elasticpath.selenium.dialogs.SelectAProductDialog;
import com.elasticpath.selenium.domainobjects.Category;
import com.elasticpath.selenium.domainobjects.Product;
import com.elasticpath.selenium.editor.CatalogEditor;
import com.elasticpath.selenium.editor.CategoryEditor;
import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;
import com.elasticpath.selenium.navigations.CatalogManagement;
import com.elasticpath.selenium.resultspane.CatalogProductListingPane;
import com.elasticpath.selenium.resultspane.CatalogSearchResultPane;
import com.elasticpath.selenium.toolbars.ActivityToolbar;
import com.elasticpath.selenium.toolbars.CatalogManagementActionToolbar;
import com.elasticpath.selenium.wizards.CreateBundleWizard;
import com.elasticpath.selenium.wizards.CreateCategoryWizard;
import com.elasticpath.selenium.wizards.CreateProductWizard;

/**
 * Catalog Search step definitions.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyMethods", "PMD.TooManyFields"})
public class CatalogDefinition {
	private static final int UUID_END_INDEX = 5;
	private static final int SLEEP_HALFSECOND_IN_MILLIS = 500;
	private final CatalogManagement catalogManagement;
	private final ActivityToolbar activityToolbar;
	private CatalogSearchResultPane catalogSearchResultPane;
	private CatalogProductListingPane catalogProductListingPane;
	private CreateProductWizard createProductWizard;
	private CreateBundleWizard createBundleWizard;
	private BasePriceEditorDialog basePriceEditorDialog;
	private final CatalogManagementActionToolbar catalogManagementActionToolbar;
	private CreateCatalogDialog createCatalogDialog;
	private CreateVirtualCatalogDialog createVirtualCatalogDialog;
	private CreateCategoryWizard createCategoryWizard;
	private CategoryEditor categoryEditor;
	private EditGlobalAttributesDialog editGlobalAttributesDialog;
	private AddAttributeDialog addAttributeDialog;
	private EditAttributeDialog editAttributeDialog;
	private AddItemDialog addItemDialog;
	private SelectAProductDialog selectAProductDialog;
	private CatalogEditor catalogEditor;
	private AddCategoryTypeDialog addCategoryTypeDialog;
	private EditCategoryTypeDialog editCategoryTypeDialog;
	private String productName = "";
	private String catalogName = "";
	private static String virtualCatalogName = "";
	private String categoryName = "";
	private String globalAttributeName = "";
	private String catalogAttributeName = "";
	private String categoryTypeName = "";


	/**
	 * Constructor.
	 */
	public CatalogDefinition() {
		catalogManagement = new CatalogManagement(SeleniumDriverSetup.getDriver());
		catalogManagementActionToolbar = new CatalogManagementActionToolbar(SeleniumDriverSetup.getDriver());
		activityToolbar = new ActivityToolbar(SeleniumDriverSetup.getDriver());
	}

	/**
	 * Search for product name.
	 *
	 * @param productName product name.
	 */
	@When("^I search for product name (.*)$")
	public void searchForProductName(final String productName) {
		searchForProductByName(productName);
	}

	/**
	 * Delete Newly Created product.
	 */
	@When("^I delete the newly created (?:product|bundle)$")
	public void deleteNewlyCreatedProduct() {
		int index = 0;
		searchForProductByName(this.productName);

		while (!catalogSearchResultPane.isProductInList(this.productName) && index < UUID_END_INDEX) {
			catalogSearchResultPane.sleep(SLEEP_HALFSECOND_IN_MILLIS);
			searchNewlyCreatedProductByName(this.productName);
			index++;
		}

		catalogSearchResultPane.getWaitDriver().adjustWaitInterval(1);
		catalogSearchResultPane.verifyProductNameExists(this.productName);
		catalogSearchResultPane.getWaitDriver().adjustWaitBackToDefault();
		catalogSearchResultPane.clickDeleteProductButton();
		new ConfirmDialog(SeleniumDriverSetup.getDriver()).clickOKButton();
	}

	/**
	 * Verify Product name.
	 *
	 * @param expectedProductName expected product name.
	 */
	@Then("^Product name (.*) should appear in result$")
	public void verifyProductSearchResult(final String expectedProductName) {
		catalogSearchResultPane.verifyProductNameExists(expectedProductName);
	}

	/**
	 * Expand Catalog.
	 *
	 * @param catalogName the catalog Name.
	 */
	@When("^I expand (.+) catalog$")
	public void expandCatalog(final String catalogName) {
		catalogManagement.expandCatalog(catalogName);
	}

	/**
	 * Double click category.
	 *
	 * @param categoryName the category name.
	 */
	@When("^I open category (.+) to view products list$")
	public void doubleClickCategory(final String categoryName) {
		catalogProductListingPane = catalogManagement.doubleClickCategory(categoryName);
	}

	/**
	 * Open Category Editor.
	 *
	 * @param categoryName the category name.
	 */
	@When("^I edit category (.+) in editor$")
	public void openCategoryEditor(final String categoryName) {
		catalogManagement.selectCategory(categoryName);
		categoryEditor = catalogManagement.clickOpenCategoryIcon();
	}

	/**
	 * Opoen new category editor.
	 */
	@When("^I open newly created category in editor$")
	public void openNewCategoryEditor() {
		catalogManagement.selectCategory(this.categoryName);
		categoryEditor = catalogManagement.clickOpenCategoryIcon();
	}

	/**
	 * Verify product in product listing.
	 *
	 * @param productNameList the product name list.
	 */
	@Then("^Product Listing should contain following products$")
	public void verifyProductInProductListing(final List<String> productNameList) {
		for (String productName : productNameList) {
			catalogProductListingPane.verifyProductNameExists(productName);
		}
	}

	/**
	 * Verify product is deleted.
	 */
	@And("^I verify (?:product|bundle) is deleted$")
	public void verifyProductIsDeleted() {
		searchNewlyCreatedProductByName(this.productName);
		catalogSearchResultPane.verifyProductIsDeleted(this.productName);
	}

	/**
	 * Create catalog with language.
	 *
	 * @param catalogName the catalog name.
	 * @param language    the language.
	 */
	@And("^I create new catalog (.*) with langauge (.+)$")
	public void createCatalogWithLanguage(final String catalogName, final String language) {
		createCatalogDialog = catalogManagementActionToolbar.clickCreateCatalogButton();
		String catalogCode = UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		createCatalogDialog.enterCatalogCode(catalogCode);
		this.catalogName = catalogName + "-" + catalogCode;
		createCatalogDialog.enterCatalogName(this.catalogName);
		createCatalogDialog.selectAvailableLanguage(language);
		createCatalogDialog.clickMoveRightButton();
		createCatalogDialog.verifySelectedLanguage(language);
		createCatalogDialog.selectDefaultLanguage(language);
		createCatalogDialog.clickSaveButton();
	}

	/**
	 * Verify catalog exists.
	 */
	@Then("^newly created catalog is in the list$")
	public void verifyCatalogExists() {
		catalogManagement.verifyCatalogExists(this.catalogName);
	}

	/**
	 * Create virtual catalog with language.
	 *
	 * @param virtualCatName the name.
	 * @param language            the language.
	 */
	@And("^I create new virtual catalog (.*) with langauge (.+)$")
	public void createVirtualCatalogWithLanguage(final String virtualCatName, final String language) {
		createVirtualCatalogDialog = catalogManagementActionToolbar.clickCreateVirtualCatalogButton();
		String virtualCatalogCode = UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		createVirtualCatalogDialog.enterCatalogCode(virtualCatalogCode);
		virtualCatalogName = virtualCatName + "-" + virtualCatalogCode;
		createVirtualCatalogDialog.enterCatalogName(virtualCatalogName);
		createVirtualCatalogDialog.selectDefaultLanguage(language);
		createVirtualCatalogDialog.clickSaveButton();
	}

	/**
	 * Verify virtual catalog exists.
	 */
	@Then("^newly created virtual catalog is in the list$")
	public void verifyVirtualCatalogExists() {
		catalogManagement.verifyVirtualCatalogExists(virtualCatalogName);
	}

	/**
	 * select new catalog.
	 */
	@And("^I select newly created catalog in the list$")
	public void selectNewCatalog() {
		catalogManagement.selectCatalog(this.catalogName);
	}

	/**
	 * Select new virtual catalog.
	 */
	@And("^I select newly created virtual catalog in the list$")
	public void selectNewVirtualCatalog() {
		catalogManagement.selectCatalog(virtualCatalogName);
	}

	/**
	 * select existing catalog.
	 *
	 * @param catalogName the catalog name.
	 */
	@And("^I select catalog (.+) in the list$")
	public void selectExistingCatalog(final String catalogName) {
		catalogManagement.selectCatalog(catalogName);
	}

	/**
	 * Delete catalog.
	 */
	@And("^I can delete newly created catalog$")
	public void deleteCatalog() {
		catalogManagement.rightClickDelete().clickOK();
	}

	/**
	 * Delete virtual catalog.
	 */
	@And("^I can delete newly created virtual catalog$")
	public void deleteVirtualCatalog() {
		catalogManagement.rightClickDelete().clickOK();
	}

	/**
	 * Verify newly created catalog is deleted.
	 */
	@And("^I verify newly created catalog is deleted$")
	public void verifyNewlyCreatedCatalogIsDeleted() {
		catalogManagement.verifyCatalogIsDeleted(this.catalogName);
	}

	/**
	 * Verify newly created virtual catalog is deleted.
	 */
	@And("^I verify newly created virtual catalog is deleted$")
	public void verifyNewlyCreatedVirtualCatalogIsDeleted() {
		catalogManagement.verifyCatalogIsDeleted(virtualCatalogName);
	}

	private void clickNextButtonCreateCategory() {
		createCategoryWizard.clickNextInDialog();
	}

	/**
	 * Select category.
	 *
	 * @param catalogName  the catalog name.
	 * @param categoryName the category name.
	 */
	@And("^I select (.+) category in (.+) catalog$")
	public void selectCategory(final String catalogName, final String categoryName) {
		catalogManagement.selectCategoryInCatalog(catalogName, categoryName);
	}

	/**
	 * Verify Category exists.
	 */
	@And("^I verify newly created category exists$")
	public void verifyCategoryExists() {
		catalogManagement.verifyCategoryExists(this.categoryName);
	}

	/**
	 * Select new category.
	 */
	@And("^I select newly created category$")
	public void selectNewCategory() {
		catalogManagement.selectCategory(this.categoryName);
	}

	/**
	 * Delete new category.
	 */
	@And("^I delete newly created category")
	public void deleteNewCategory() {
//		Need to select category again before right click delete.
		selectNewCategory();
		catalogManagement.clickDeleteCategoryIcon();
		new ConfirmDialog(SeleniumDriverSetup.getDriver()).clickOKButton();
	}

	/**
	 * Verify newly created category is deleted.
	 */
	@And("^newly created category is deleted$")
	public void verifyNewlyCreatedCategoryIsDeleted() {
		catalogManagement.verifyCategoryIsNotInList(this.categoryName);
	}

	/**
	 * Select editor tab.
	 *
	 * @param tabName the tab name.
	 */
	@And("^I select editor's (.+) tab$")
	public void selectEditorTab(final String tabName) {
		categoryEditor.selectTab(tabName);
	}

	/**
	 * Verify category attribute.
	 *
	 * @param attributeValueList the attribute value list.
	 */
	@And("^it should have following category attributes?$")
	public void verifyCategoryAttribute(final List<String> attributeValueList) {
		for (String attributeValue : attributeValueList) {
			categoryEditor.verifyAttributeValue(attributeValue);
		}
	}

	/**
	 * Create new category for existing catelog.
	 *
	 * @param catalog          the catelog.
	 * @param categoryInfoList the category info list.
	 */
	@When("^I create new category for (.+) with following data")
	public void createNewCategoryforExistingCatalog(final String catalog, final List<Category> categoryInfoList) {
		for (Category category : categoryInfoList) {
			selectExistingCatalog(catalog);
			createCategoryWizard = catalogManagement.clickCreateCategoryIcon();
			String categoryCode = UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
			createCategoryWizard.enterCategoryCode(categoryCode);
			this.categoryName = category.getCategoryName() + " - " + categoryCode;
			createCategoryWizard.enterCategoryName(this.categoryName);
			createCategoryWizard.selectCategoryType(category.getCategoryType());
			createCategoryWizard.enterCurrentEnableDateTime();
			if (category.getStoreVisible().equalsIgnoreCase("true")) {
				createCategoryWizard.checkStoreVisibleBox();
			}
			clickNextButtonCreateCategory();
			createCategoryWizard.enterAttributeLongText(category.getAttrLongTextValue(), category.getAttrLongTextName());
			createCategoryWizard.enterAttributeDecimalValue(category.getAttrDecimalValue(), category.getAttrDecimalName());
			createCategoryWizard.enterAttributeShortText(category.getAttrShortTextValue(), category.getAttrShortTextName());
			createCategoryWizard.clickFinish();
		}
	}

	/**
	 * Create new product for existing category.
	 *
	 * @param productInfoList the product info list.
	 */
	@When("^I create new product with following attributes$")
	public void createNewProductForExistingCategory(final List<Product> productInfoList) {
		Product product = productInfoList.get(0);
		catalogManagement.expandCatalog(product.getCatalog());
		catalogManagement.doubleClickCategory(product.getCategory());
		createProductWizard = catalogManagement.clickCreateProductButton();
		String prodCode = UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		createProductWizard.enterProductCode(prodCode);
		this.productName = product.getProductName() + "-" + prodCode;
		createProductWizard.enterProductName(this.productName);
		createProductWizard.selectProductType(product.getProductType());
		createProductWizard.selectTaxCode(product.getTaxCode());
		createProductWizard.selectBrand(product.getBrand());

		if (product.getStoreVisible().equalsIgnoreCase("true")) {
			createProductWizard.checkStoreVisibleBox();
		} else {
			assertThat(product.getStoreVisible())
					.as("Store visible value is invalid - " + product.getStoreVisible())
					.isEqualToIgnoringCase("false");
		}

		createProductWizard.selectAvailabilityRule(product.getAvailability());
		createProductWizard.clickNextInDialog();
		createProductWizard.enterAttributeShortTextMultiValue(product.getAttrShortTextMultiValue(), product.getAttrShortTextMulti());
		createProductWizard.enterAttributeIntegerValue(product.getAttrIntegerValue(), product.getAttrInteger());
		createProductWizard.enterAttributeDecimalValue(product.getAttrDecimalValue(), product.getAttrDecimal());
		createProductWizard.clickNextInDialog();
		createProductWizard.enterSkuCode("sku" + prodCode);
		createProductWizard.selectShippableType(product.getShippableType());
		createProductWizard.clickNextInDialog();
		createProductWizard.selectPriceList(product.getPriceList());
		basePriceEditorDialog = createProductWizard.clickAddBasePriceButton();
		basePriceEditorDialog.enterListPrice(product.getListPrice());
		basePriceEditorDialog.clickOKButton();
		createProductWizard.clickFinish();
	}

	/**
	 * Create enw global attribute.
	 *
	 * @param globalAttributeName the global attribute name.
	 * @param usage               the usage.
	 * @param type                the type.
	 * @param required            required flag.
	 */
	@When("^I create a new global attribute with name (.+) for (.+) of type (.+) with required (.+)$")
	public void createNewGlobalAttribute(final String globalAttributeName, final String usage, final String type, final boolean required) {
		this.globalAttributeName = globalAttributeName + "_" + UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		editGlobalAttributesDialog = catalogManagementActionToolbar.clickEditGlobalAttributesButton();
		addAttributeDialog = editGlobalAttributesDialog.clickAddAttributeButton();
		addAttributeDialog.enterAttributeKey("GA_" + UUID.randomUUID().toString().substring(0, UUID_END_INDEX));
		addAttributeDialog.enterAttributeName(this.globalAttributeName);
		addAttributeDialog.selectAttributeUsage(usage);
		addAttributeDialog.selectAttributeType(type);

		if (required) {
			addAttributeDialog.clickCheckBox("Required Attribute");
		}

		addAttributeDialog.clickAddButton();
		editGlobalAttributesDialog.clickSaveButton();
	}

	/**
	 * verify newly created global attribute exists.
	 */
	@Then("^newly created global attribute is in the list$")
	public void verifyNewlyCreatedGlobalAttributeExists() {
		editGlobalAttributesDialog = catalogManagementActionToolbar.clickEditGlobalAttributesButton();
		editGlobalAttributesDialog.verifyGlobalAttributeValue(this.globalAttributeName);
	}

	/**
	 * Select new global attribute.
	 */
	@And("^I select newly created global attribute in the list$")
	public void selectNewGlobalAttribute() {
		editGlobalAttributesDialog.selectGlobalAttributeRow(this.globalAttributeName);
	}

	/**
	 * Delete new global attribute.
	 */
	@And("^I delete newly created global attribute$")
	public void deleteNewGlobalAttribute() {
		editGlobalAttributesDialog.deleteGlobalAttribute();
		editGlobalAttributesDialog.clickSaveButton();
	}

	/**
	 * Verify newly created global attribute is deleted.
	 */
	@And("^I verify newly created global attribute is deleted$")
	public void verifyNewlyCreatedGlobalAttributeIsDeleted() {
		editGlobalAttributesDialog = catalogManagementActionToolbar.clickEditGlobalAttributesButton();
		editGlobalAttributesDialog.verifyGlobalAttributeValueIsNotInList(this.globalAttributeName);
	}

	private void searchForProductByName(final String productName) {
		catalogManagement.clickCatalogSearchTab();
		catalogManagement.enterProductName(productName);
		catalogSearchResultPane = catalogManagement.clickCatalogSearch();
	}

	private void searchNewlyCreatedProductByName(final String productName) {
		catalogManagement.enterProductName(productName);
		catalogSearchResultPane = catalogManagement.clickCatalogSearch();
	}

	/**
	 * Create new bundle for existing category.
	 *
	 * @param productInfoList the product info list.
	 */
	@When("^I create new bundle with following attributes$")
	public void createNewBundleForExistingCategory(final List<Product> productInfoList) {
		Product product = productInfoList.get(0);

		catalogManagement.expandCatalog(product.getCatalog());
		catalogManagement.doubleClickCategory(product.getCategory());
		createBundleWizard = catalogManagement.clickCreateBundleButton();
		String productCode = UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		createBundleWizard.enterProductCode(productCode);
		this.productName = product.getProductName() + "-" + productCode;
		createBundleWizard.enterProductName(this.productName);
		createBundleWizard.selectBundlePricing(product.getBundlePricing());
		createBundleWizard.selectProductType(product.getProductType());
		createBundleWizard.selectBrand(product.getBrand());

		if (product.getStoreVisible().equalsIgnoreCase("true")) {
			createBundleWizard.checkStoreVisibleBox();
		} else {
			assertThat(product.getStoreVisible())
					.as("Store visible value is invalid - " + product.getStoreVisible())
					.isEqualToIgnoringCase("false");
		}

		createBundleWizard.clickNextInDialog();

		for (String bundleProductCode : product.getBundleProductCodeList()) {
			addItemDialog = createBundleWizard.clickAddItemButton();
			selectAProductDialog = addItemDialog.clickSelectProductImageLink();
			selectAProductDialog.enterProductCode(bundleProductCode);
			selectAProductDialog.clickSearchButton();
			selectAProductDialog.selectProductByCode(bundleProductCode);
			selectAProductDialog.clickOKButton();
			addItemDialog.clickOKButton();
		}

		createBundleWizard.clickNextInDialog();
		createBundleWizard.enterAttributeShortTextMultiValue(product.getAttrShortTextMultiValue(), product.getAttrShortTextMulti());
		createBundleWizard.enterAttributeIntegerValue(product.getAttrIntegerValue(), product.getAttrInteger());
		createBundleWizard.enterAttributeDecimalValue(product.getAttrDecimalValue(), product.getAttrDecimal());
		createBundleWizard.clickNextInDialog();
		createBundleWizard.enterSkuCode(UUID.randomUUID().toString().substring(0, UUID_END_INDEX));
		createBundleWizard.clickNextInDialog();
		createBundleWizard.clickFinish();
	}

	/**
	 * Verify create catalog button is present.
	 */
	@And("^I can view Create Catalog button")
	public void verifyCreateCatalogButtonIsPresent() {
		catalogManagementActionToolbar.verifyCreateCatalogButtonIsPresent();
	}

	/**
	 * Verify create catalog button is present.
	 */
	@And("^I open the newly created catalog editor")
	public void openNewCatalogEditor() {
		selectNewCatalog();
		catalogEditor = catalogManagement.clickOpenCatalogCategoryButton();
	}

	/**
	 * Select order editor tab.
	 *
	 * @param tabName the tab name.
	 */
	@When("^I select (.+) tab in the Catalog Editor$")
	public void selectCatalogEditorTab(final String tabName) {
		catalogEditor.selectTab(tabName);
	}

	/**
	 * Deletes new catalog.
	 */
	@After("@cleanupCatalog")
	public void cleanupCatalog() {
		selectNewCatalog();
		deleteCatalog();
	}

	/**
	 * Verify catalog attribute.
	 *
	 * @param attributeValueList the attribute value list.
	 */
	@And("^it should have following catalog attributes?$")
	public void verifyCatalogAttribute(final List<String> attributeValueList) {
		for (String attributeValue : attributeValueList) {
			catalogEditor.verifyCatalogAttributeValue(attributeValue);
		}
	}

	/**
	 * verify newly created catalog attribute exists.
	 */
	@Then("^(?:newly created|updated) catalog attribute is in the list$")
	public void verifyNewlyCreatedCatalogAttributeExists() {
		catalogEditor.verifyCatalogAttributeValue(this.catalogAttributeName);
	}

	/**
	 * Create new catalog attribute.
	 *
	 * @param catlogAttributeName the catalog attribute name.
	 * @param usage               the usage.
	 * @param type                the type.
	 * @param required            required flag.
	 */
	@When("^I create a new catalog attribute with name (.+) for (.+) of type (.+) with required (.+)$")
	public void createNewCatalogAttribute(final String catlogAttributeName, final String usage, final String type, final boolean required) {
		this.catalogAttributeName = catlogAttributeName + "_" + UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		addAttributeDialog = catalogEditor.clickAddAttributeButton();

		addAttributeDialog.enterAttributeKey("CA_" + UUID.randomUUID().toString().substring(0, UUID_END_INDEX));
		addAttributeDialog.enterAttributeName(this.catalogAttributeName);
		addAttributeDialog.selectAttributeUsage(usage);
		addAttributeDialog.selectAttributeType(type);

		if (required) {
			addAttributeDialog.clickCheckBox("Required Attribute");
		}

		addAttributeDialog.clickAddButton();
		catalogManagementActionToolbar.saveAll();
	}

	/**
	 * Delete new catalog.
	 */
	@When("^I close the editor and try to delete the newly created catalog$")
	public void deleteNewCatalogError() {
		catalogManagement.closePane(this.catalogName);
		selectNewCatalog();
		catalogManagement.rightClickDelete();
	}

	/**
	 * Delete new catalog.
	 */
	@When("^I close the editor and delete the newly created catalog$")
	public void deleteNewCatalog() {
		catalogManagement.closePane(this.catalogName);
		selectNewCatalog();
		catalogManagement.rightClickDelete().clickOK();
	}

	/**
	 * Delete new catalog attribute.
	 */
	@When("^I delete the newly created catalog attribute$")
	public void deleteNewCatalogAttribute() {
		catalogEditor.selectTab("Attributes");
		catalogEditor.selectCatalogAttributeValue(this.catalogAttributeName);
		catalogEditor.clickRemoveAttributeButton();
		new ConfirmDialog(SeleniumDriverSetup.getDriver()).clickOKButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify newly created catalog attribute is deleted.
	 */
	@Then("^I verify newly created catalog attribute is deleted$")
	public void verifyNewAttributeDelete() {
		catalogEditor.verifyCatalogAttributeDelete(this.catalogAttributeName);
	}


	/**
	 * Edit catalog attribute name.
	 */
	@When("^I edit the catalog attribute name$")
	public void editCatalogAttributeName() {
		catalogEditor.selectCatalogAttributeValue(this.catalogAttributeName);
		this.catalogAttributeName = "Edit Prod Desc" + "_" + UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		editAttributeDialog = catalogEditor.clickEditAttributeButton();
		editAttributeDialog.enterAttributeName(this.catalogAttributeName);
		editAttributeDialog.clickOKButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Create category type.
	 *
	 * @param categoryTypeName the category type name
	 * @param attributeList    the list of attributes
	 */
	@When("^I create a new category type (.*) with following attributes?$")
	public void createCatalogWithLanguage(final String categoryTypeName, final List<String> attributeList) {
		addCategoryTypeDialog = catalogEditor.clickAddCategoryTypeButton();
		this.categoryTypeName = categoryTypeName + UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		addCategoryTypeDialog.enterCategoryTypeName(this.categoryTypeName);
		for (String attribute : attributeList) {
			addCategoryTypeDialog.selectAvailableAttribute(attribute);
			addCategoryTypeDialog.clickMoveRightButton();
			addCategoryTypeDialog.verifyAssignedAttribute(attribute);
		}
		addCategoryTypeDialog.clickAddButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * verify newly created catalog attribute exists.
	 */
	@Then("^(?:newly created|updated) category type is in the list$")
	public void verifyNewlyCreatedCategoryTypeExists() {
		catalogEditor.verifyCategoryType(this.categoryTypeName);
	}

	/**
	 * Edit category type name.
	 */
	@When("^I edit the category type name$")
	public void editCategoryTypeName() {
		catalogEditor.selectCategoryType(this.categoryTypeName);
		this.catalogAttributeName = "Edit Cat Type" + "_" + UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		editCategoryTypeDialog = catalogEditor.clickEditCategoryTypeButton();
		editCategoryTypeDialog.enterCategoryTypeName(this.categoryTypeName);
		editCategoryTypeDialog.clickAddButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Delete new category type.
	 */
	@When("^I delete the newly created category type$")
	public void deleteNewCategoryType() {
		catalogEditor.selectTab("CategoryTypes");
		catalogEditor.selectCategoryType(this.categoryTypeName);
		catalogEditor.clickRemoveCategoryTypeButton();
		new ConfirmDialog(SeleniumDriverSetup.getDriver()).clickOKButton();
		catalogManagementActionToolbar.saveAll();
		catalogManagementActionToolbar.clickReloadActiveEditor();
	}

	/**
	 * Verify newly created category type is deleted.
	 */
	@Then("^I verify newly created category type is deleted$")
	public void verifyNewCategoryTypeDelete() {
		catalogEditor.verifyCategoryTypeDelete(this.categoryTypeName);
	}

	/**
	 * Deletes newly created virtual catalog.
	 */
	@After("@deleteNewVirtualCatalog")
	public void deleteNewVirtualCatalog() {
		activityToolbar.clickCatalogManagementButton();
		selectNewVirtualCatalog();
		deleteVirtualCatalog();
	}

	/**
	 * Returns virtual catalog name.
	 *
	 * @return virtualCatalogName
	 */
	public static String getVirtualCatalogName() {
		return virtualCatalogName;
	}

}

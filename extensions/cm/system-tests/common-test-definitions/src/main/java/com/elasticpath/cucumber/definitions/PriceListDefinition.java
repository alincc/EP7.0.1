package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.PriceEditorDialog;
import com.elasticpath.selenium.dialogs.SelectAProductDialog;
import com.elasticpath.selenium.dialogs.SelectASkuDialog;
import com.elasticpath.selenium.editor.PriceListEditor;
import com.elasticpath.selenium.editor.ProductEditor;
import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;
import com.elasticpath.selenium.navigations.PriceListManagement;
import com.elasticpath.selenium.resultspane.PriceListAssignmentsResultPane;
import com.elasticpath.selenium.resultspane.PriceListsResultPane;
import com.elasticpath.selenium.toolbars.PriceListActionToolbar;
import com.elasticpath.selenium.wizards.CreatePriceListAssignmentWizard;


/**
 * Price List definition steps.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class PriceListDefinition {
	private static final int UID_END_INDEX = 5;
	private final PriceListManagement priceListManagement;
	private final PriceListActionToolbar priceListActionToolbar;
	private CreatePriceListAssignmentWizard createPriceListAssignmentWizard;
	private PriceListAssignmentsResultPane priceListAssignmentsResultPane;
	private PriceListsResultPane priceListsResultPane;
	private PriceListEditor priceListEditor;
	private PriceEditorDialog priceEditorDialog;
	private SelectAProductDialog selectAProductDialog;
	private SelectASkuDialog selectASkuDialog;
	private ProductEditor productEditor;
	private String uniquePriceListAssignmentName = "";
	private String uniquePriceListName = "";
	private static final String PRICE_LIST_DESC = "test price list";
	private static final String CURRENCY = "CAD";

	/**
	 * Constructor.
	 */
	public PriceListDefinition() {
		priceListActionToolbar = new PriceListActionToolbar(SeleniumDriverSetup.getDriver());
		priceListManagement = new PriceListManagement(SeleniumDriverSetup.getDriver());
	}

	/**
	 * Select price list tab.
	 */
	@When("^I select Price List tab$")
	public void selectPriceListTab() {
		priceListManagement.clickPriceListTab();
	}

	/**
	 * Select price list assignments tab.
	 */
	@When("^I select Price List Assignments tab$")
	public void selectPriceListAssignmentsTab() {
		clickPriceListAssignmentsTab();
	}

	/**
	 * Click new price list button.
	 */
	@When("^I click Create Price List button$")
	public void clickNewPriceListButton() {
		priceListEditor = priceListActionToolbar.clickCreatePriceList();
	}

	/**
	 * Create Price list assignment (UNUSED).
	 *
	 * @param priceList the price list.
	 * @param catalog   the catalog to assign it to.
	 */
	@When("^I create Price List Assignment with existing price list (.+) for catalog (.+)$")
	public void createPriceListAssignment(final String priceList, final String catalog) {
		clickCreatePriceListAssignment();
		enterPriceListAssignmentName();
		clickNextButton();
		selectPriceListName(priceList);
		clickNextButton();
		selectCatalogNameForPriceListAssignment(catalog);
		clickFinishButton();
	}

	/**
	 * Create Price list assignment for new price list.
	 *
	 * @param catalog the catalog to assign it to.
	 */
	@When("^I create Price List Assignment with newly created price list for catalog (.+)$")
	public void createPriceListAssignmentForNewPriceList(final String catalog) {
		clickCreatePriceListAssignment();
		enterPriceListAssignmentName();
		clickNextButton();
		selectPriceListName(uniquePriceListName);
		clickNextButton();
		selectCatalogNameForPriceListAssignment(catalog);
		clickFinishButton();
	}

	/**
	 * Set up price list assignment.
	 *
	 * @param catalog the catalog.
	 */
	@Given("^I have a Price List Assignment for catalog (.+)$")
	public void setupPriceListAssignment(final String catalog) {
		createNewPriceList(PRICE_LIST_DESC, CURRENCY);
		createPriceListAssignmentForNewPriceList(catalog);
	}

	/**
	 * Search Created price list assignments.
	 *
	 * @param priceListName price list name to search for.
	 */
	@When("^I search for Price List Name (.*)$")
	public void searchCreatedPriceListAssignment(final String priceListName) {
		clickPriceListAssignmentsTab();
		priceListManagement.enterPriceListName(priceListName);
		priceListAssignmentsResultPane = priceListManagement.clickPriceListAssignmentSearch();
	}

	/**
	 * Verify newly created price list assignment exists.
	 */
	@Then("^I should see newly created Price List Assignment in search result$")
	public void verifyNewCreatedPriceListAssignmentExists() {
		searchNewCreatedPriceListAssignment();
		priceListAssignmentsResultPane.verifyPriceListAssignmentExists(uniquePriceListAssignmentName);
	}

	/**
	 * Delete newly created price list assignment.
	 */
	@When("^I delete newly created price list assignment$")
	public void deleteNewCreatedPriceListAssignment() {
		searchNewCreatedPriceListAssignment();
		priceListAssignmentsResultPane.deletePriceListAssignment(uniquePriceListAssignmentName);
		new ConfirmDialog(SeleniumDriverSetup.getDriver()).clickOKButton();
	}

	/**
	 * Verify price list assignment is deleted.
	 */
	@Then("^the deleted price list assignment no longer exists$")
	public void verifyPriceListAssignmentDeleted() {
		searchCreatedPriceListAssignment(uniquePriceListName);
		priceListAssignmentsResultPane.verifyPriceListAssignmentDeleted(uniquePriceListAssignmentName);
	}

	/**
	 * Click search for price lists.
	 */
	@When("^I search for price list")
	public void clickSearchForPriceLists() {
		priceListsResultPane = priceListManagement.clickPriceListsSearch();
	}

	/**
	 * Verify Price lists.
	 *
	 * @param expectedPriceList the expected price list.
	 */
	@Then("^I should see price list (.*) in the result$")
	public void verifyPriceLists(final String expectedPriceList) {
		priceListsResultPane.verifyPriceListExists(expectedPriceList);
	}

	/**
	 * Verify newly created price lists.
	 */
	@Then("^I should see the newly created price list$")
	public void verifyNewCreatedPriceList() {
		clickSearchForPriceLists();
		verifyPriceLists(uniquePriceListName);
	}

	/**
	 * Create new price list.
	 *
	 * @param description the new price list description.
	 * @param currency    the CURRENCY.
	 */
	@When("^I create a new price list with description (.+) and currency (.+)$")
	public void createNewPriceList(final String description, final String currency) {
//		Appending letter Z in order to test the scroll and pagination.
		uniquePriceListName = "Z" + UUID.randomUUID().toString().substring(0, UID_END_INDEX);
		priceListEditor = priceListActionToolbar.clickCreatePriceList();
		priceListEditor.enterPriceListName(uniquePriceListName);
		priceListEditor.enterPriceListDescription(description);
		priceListEditor.enterPriceListCurrency(currency);
		priceListActionToolbar.saveAll();
		//TODO
		priceListEditor.closePriceListEditor(this.uniquePriceListName);
//		priceListEditor.closePriceListEditor("New Price List");
	}

	/**
	 * Create new price list.
	 *
	 * @param currency the CURRENCY.
	 */
	@When("^I create new price list with currency (.+) and without description$")
	public void createNewPriceList(final String currency) {
//		Appending letter Z in order to test the scroll and pagination.
		uniquePriceListName = "Z" + UUID.randomUUID().toString().substring(0, UID_END_INDEX);
		priceListEditor = priceListActionToolbar.clickCreatePriceList();
		priceListEditor.enterPriceListName(uniquePriceListName);
		priceListEditor.enterPriceListCurrency(currency);
		priceListActionToolbar.saveAll();
		//TODO
		priceListEditor.closePriceListEditor(this.uniquePriceListName);
//		priceListEditor.closePriceListEditor("New Price List");
	}

	/**
	 * Set up price list.
	 */
	@Given("^I have a new Price List$")
	public void setupPriceList() {
		createNewPriceList(PRICE_LIST_DESC, CURRENCY);
		verifyNewCreatedPriceList();
	}

	/**
	 * Delete new price list.
	 */
	@Then("^I delete the newly created price list")
	public void deleteNewPriceList() {
		clickSearchForPriceLists();
		priceListsResultPane.deletePriceList(uniquePriceListName);
		new ConfirmDialog(SeleniumDriverSetup.getDriver()).clickOKButton();
	}

	/**
	 * Open the pricelist assignment.
	 */
	@Then("^I open the pricelist assignment$")
	public void openPriceListAssigment() {
		searchNewCreatedPriceListAssignment();
		priceListAssignmentsResultPane.openPriceListAssignment(uniquePriceListAssignmentName);
	}

	/**
	 * Edit pricelist assignment description.
	 *
	 * @param descriptionText the description text.
	 */
	@Then("^I edit the pricelist assignment description to \"(.+)\"")
	public void editPriceListAssignmentDescription(final String descriptionText) {
		searchNewCreatedPriceListAssignment();
		priceListAssignmentsResultPane.openPriceListAssignment(uniquePriceListAssignmentName);
		createPriceListAssignmentWizard.enterPriceListAssignmentDescription(descriptionText);
		createPriceListAssignmentWizard.clickFinish();
	}

	/**
	 * Verify the pricelist assignment description.
	 *
	 * @param descriptionText the description text.
	 */
	@Then("^the pricelist assignment description is \"(.+)\"")
	public void confirmEditPriceListAssignmentDescription(final String descriptionText) {
		assertTrue(createPriceListAssignmentWizard.getPriceListAssignmentDescription().equals(descriptionText));
		assertThat(createPriceListAssignmentWizard.getPriceListAssignmentDescription().equals(descriptionText))
				.as("Pricelist description should be " + descriptionText)
				.isTrue();
		assertThat(createPriceListAssignmentWizard.getPriceListAssignmentDescription())
				.as("Pricelist description should be " + descriptionText)
				.isEqualTo(descriptionText);
		createPriceListAssignmentWizard.clickFinish();
	}

	/**
	 * Verify price list is deleted.
	 */
	@Then("^The deleted price list no longer exists$")
	public void verifyPriceListDeleted() {
		clickSearchForPriceLists();
		priceListsResultPane.verifyPriceListDeleted(uniquePriceListName);
	}

	/**
	 * Search Price List Assignements for catalog.
	 *
	 * @param catalog the catalog.
	 */
	@When("^I search Price List Assignments for catalog (.+)$")
	public void searchPLAforCatalog(final String catalog) {
		selectCatalog(catalog);
		clickPLASearchButton();
	}

	/**
	 * Verify Price List Assignments search results.
	 *
	 * @param plaList the price list assignment list.
	 */
	@Then("^Search result should contain following Price List Assignments?$")
	public void verifyPLASearchResults(final List<String> plaList) {
		for (String pla : plaList) {
			priceListAssignmentsResultPane.verifyPLASearchResults(pla);
		}
	}

	/**
	 * Open New price list editor.
	 */
	@And("^I open the newly created price list editor$")
	public void openNewPriceListEditor() {
		priceListEditor = priceListsResultPane.openPriceListEditor(this.uniquePriceListName);
	}

	/**
	 * Open price list editor.
	 *
	 * @param priceListName the price list name.
	 */
	@And("^I open price list (.+) in editor$")
	public void openPriceListEditor(final String priceListName) {
		priceListEditor = priceListsResultPane.openPriceListEditor(priceListName);
	}

	/**
	 * Add Product price list.
	 *
	 * @param listPrice   The list price.
	 * @param productName the product name.
	 */
	@And("^I add a list price (.+) for product (.+)$")
	public void addProductPrice(final String listPrice, final String productName) {
		addProductPrices(listPrice, "", productName, "1");
		clickOKAndSaveAll();
	}

	/**
	 * Add a new pricelist.
	 *
	 * @param listPrice   The list price.
	 * @param salePrice   The sale price.
	 * @param quantity    The quantity.
	 * @param productName The product.
	 */
	@And("^I (?:can add|add) list price (.+) and sale price (.+) for quantity (.+) for product (.+)$")
	public void addProductTierPrice(final String listPrice, final String salePrice, final String quantity, final String productName) {
		addProductPrices(listPrice, salePrice, productName, quantity);
		clickOKAndSaveAll();
	}

	/**
	 * Attempt to add a new pricelist.
	 *
	 * @param listPrice   The list price.
	 * @param salePrice   The sale price.
	 * @param quantity    The quantity.
	 * @param productName The product.
	 */
	@And("^I attempt to add list price (.+) and sale price (.+) for quantity (.+) for product (.+)$")
	public void attemptToAddProductTierPriceFromDialog(final String listPrice, final String salePrice, final String quantity, final String
			productName) {
		addProductPrices(listPrice, salePrice, productName, quantity);
	}

	/**
	 * Attempt to add a product list price.
	 *
	 * @param listPrice   the list price.
	 * @param productName the product name.
	 */
	@And("^I attempt to add a list price (.+) for product (.+)$")
	public void attemptToAddProductPrice(final String listPrice, final String productName) {
		addProductPrices(listPrice, "", productName, "1");
	}

	/**
	 * Add Sku price list.
	 *
	 * @param listPrice the list price.
	 * @param skuCode   the sku code.
	 */
	@And("^I add a list price (.+) for sku code (.+)$")
	public void addSkuPrice(final String listPrice, final String skuCode) {
		addSkuPrices(listPrice, "", skuCode);
		clickOKAndSaveAll();
	}

	/**
	 * Attempt to add a list price to a sku.
	 *
	 * @param listPrice the list price.
	 * @param skuCode   the sku price.
	 */
	@And("^I attempt to add a list price (.+) for sku code (.+)$")
	public void attemptToAddSkuPrice(final String listPrice, final String skuCode) {
		addSkuPrices(listPrice, "", skuCode);
	}

	/**
	 * Add product list and sale price.
	 *
	 * @param salePrice   The sale price.
	 * @param listPrice   the list price.
	 * @param productName the product name.
	 */
	@And("^I add a sale price (.+) and a list price (.+) for the product (.+)$")
	public void addProductListAndSalePrice(final String salePrice, final String listPrice, final String productName) {
		addProductPrices(listPrice, salePrice, productName, "1");
		clickOKAndSaveAll();
	}

	/**
	 * Verify Product Price is present.
	 *
	 * @param productCodeList the product code list
	 */
	@Then("^the price list should have prices? for the following product codes?$")
	public void verifyProductPriceIsPresent(final List<String> productCodeList) {
		for (String productCode : productCodeList) {
			priceListEditor.verifyProductCodeIsPresentInPriceList(productCode);
		}
	}

	/**
	 * Verify Product Price is present.
	 *
	 * @param skuCodeList the sku code list.
	 */
	@Then("^the price list should have prices? for the following sku codes?$")
	public void verifySkuPriceIsPresent(final List<String> skuCodeList) {
		for (String skuCode : skuCodeList) {
			priceListEditor.verifyProductCodeIsPresentInPriceList(skuCode);
		}
	}

	/**
	 * Verify Product list and sale price.
	 *
	 * @param listPrice   the list price.
	 * @param salePrice   the sale price.
	 * @param productName the product name.
	 */
	@Then("^the price list should have a list price (.+) and a sale price (.+) for the product (.+)$")
	public void verifyProductListAndSalePrice(final String listPrice, final String salePrice, final String productName) {
		priceListEditor.verifyListPriceInPriceList(productName, listPrice);
		priceListEditor.verifySalePriceInPriceList(productName, salePrice);
	}

	/**
	 * Verify product price is not present.
	 *
	 * @param productCode the product code.
	 */
	@Then("^product code (.+) should not be in price list editor$")
	public void verifyProductPriceIsNotPresent(final String productCode) {
		priceListEditor.verifyProductCodeIsNotPresentInPriceList(productCode);
	}


	/**
	 * Delete price.
	 *
	 * @param productCode the product code.
	 */
	@When("^I delete price for product code (.+)$")
	public void deletePrice(final String productCode) {
		priceListEditor.selectPriceRowByProductCode(productCode);
		priceListEditor.clickDeletePriceButton();
		priceListActionToolbar.saveAll();
	}

	/**
	 * Edit price.
	 *
	 * @param productName the product name.
	 * @param listPrice   the list price.
	 * @param salePrice   the sale price.
	 */
	@When("^I edit price for product (.+) as list price (.+) and sale price (.+)$")
	public void editPrice(final String productName, final String listPrice, final String salePrice) {
		priceListEditor.selectPriceRowByProductName(productName);
		priceEditorDialog = priceListEditor.clickEditPriceButton();
		priceEditorDialog.enterListPrice(listPrice);
		priceEditorDialog.enterSalePrice(salePrice);
		priceEditorDialog.clickOKButton();
		priceListActionToolbar.saveAll();
	}

	/**
	 * open product editor for the given product code.
	 *
	 * @param productCode the code.
	 */
	@When("^I open the product editor for product code (.+)$")
	public void openItem(final String productCode) {
		priceListEditor.selectPriceRowByProductCode(productCode);
		productEditor = priceListEditor.clickOpenItemButton();
	}

	/**
	 * Verify product name.
	 *
	 * @param productName the product name.
	 */
	@Then("^the product name should be (.+)$")
	public void verifyProductName(final String productName) {
		productEditor.verifyProductName(productName);
	}

	/**
	 * Enter price list summary values.
	 *
	 * @param priceListSummaryMap The summary value map.
	 */
	@And("^I enter following price list summary values? and save it$")
	public void enterPirceListSummaryValues(final Map<String, String> priceListSummaryMap) {
		priceListEditor.enterPriceListName(priceListSummaryMap.get("Price List"));
		priceListEditor.enterPriceListDescription(priceListSummaryMap.get("Description"));
		priceListEditor.enterPriceListCurrency(priceListSummaryMap.get("Currency"));
		priceListActionToolbar.saveAll();
	}

	/**
	 * Search price for code.
	 *
	 * @param code the code.
	 */
	@When("^I search price for code (.+)$")
	public void searchPrice(final String code) {
		priceListEditor.enterCodeToSearch(code);
		priceListEditor.clickSearchButton();
	}

	/**
	 * Verify error message.
	 *
	 * @param errMsgList the list of messages.
	 */
	@Then("^I should see following validation alert?$")
	public void verifyErrorAlert(final List<String> errMsgList) {
		for (String errMsg : errMsgList) {
			if (errMsg.length() > 0) {
				priceEditorDialog.verifyValidationErrorIsPresent(errMsg);
			}
		}
		priceEditorDialog.clickCancel();
	}

	/**
	 * Clean up price list assignment.
	 */
	@After("@cleanupPriceListAssignment")
	public void cleanUpPriceListAssignment() {
		deleteNewCreatedPriceListAssignment();
		verifyPriceListAssignmentDeleted();
		cleanUpPriceList();
	}

	/**
	 * Clean up price list.
	 */
	@After("@cleanupPriceList")
	public void cleanUpPriceList() {
		clickPriceListTab();
		deleteNewPriceList();
		verifyPriceListDeleted();
	}

	private void searchNewCreatedPriceListAssignment() {
		clickPriceListAssignmentsTab();
		priceListManagement.enterPriceListName(uniquePriceListName);
		priceListAssignmentsResultPane = priceListManagement.clickPriceListAssignmentSearch();
	}

	private void clickCreatePriceListAssignment() {
		createPriceListAssignmentWizard = priceListActionToolbar.clickCreatePriceListAssignment();
	}

	private void enterPriceListAssignmentName() {
		int endIndex = UID_END_INDEX;
		uniquePriceListAssignmentName = "PLA" + UUID.randomUUID().toString().substring(0, endIndex);
		createPriceListAssignmentWizard.enterPriceListAssignmentName(uniquePriceListAssignmentName);
	}

	private void clickNextButton() {
		createPriceListAssignmentWizard.clickNextInDialog();
	}

	private void clickFinishButton() {
		createPriceListAssignmentWizard.clickFinish();
	}

	private void selectPriceListName(final String priceListName) {
		createPriceListAssignmentWizard.selectPriceList(priceListName);
	}

	private void selectCatalogNameForPriceListAssignment(final String catalogName) {
		createPriceListAssignmentWizard.selectCatalogName(catalogName);
	}

	private void clickPriceListAssignmentsTab() {
		priceListManagement.clickPriceListAssignmentsTab();
	}

	private void clickPriceListTab() {
		priceListManagement.clickPriceListTab();
	}

	private void selectCatalog(final String catalogName) {
		priceListManagement.selectCatalogFromComboBox(catalogName);
	}

	private void clickPLASearchButton() {
		priceListAssignmentsResultPane = priceListManagement.clickPriceListAssignmentSearch();
	}

	/**
	 * Verify create price list button is present.
	 */
	@And("^I can view Create Price List button")
	public void verifyCreatePriceListButtonIsPresent() {
		priceListActionToolbar.verifyCreatePriceListButtonIsPresent();
	}

	/**
	 * Adds product prices.
	 *
	 * @param listPrice   The list price.
	 * @param salePrice   the sale price.
	 * @param quantity    The quantity.
	 * @param productName The product name.
	 */
	public void addProductPrices(final String listPrice, final String salePrice, final String productName, final String quantity) {
		priceEditorDialog = priceListEditor.clickAddPriceButton();
		selectAProductDialog = priceEditorDialog.clickSelectProductImageLink();
		selectAProductDialog.enterProductName(productName);
		selectAProductDialog.clickSearchButton();
		selectAProductDialog.selectProductByName(productName);
		selectAProductDialog.clickOKButton();
		priceEditorDialog.enterQuantity(quantity);
		priceEditorDialog.enterListPrice(listPrice);
		if (!"".equals(salePrice)) {
			priceEditorDialog.enterSalePrice(salePrice);
		}
	}

	/**
	 * Click On on editor dialog and save all.
	 */
	public void clickOKAndSaveAll() {
		priceEditorDialog.clickOKButton();
		priceListActionToolbar.saveAll();
		priceListEditor.closePriceListEditor(this.uniquePriceListName);
		priceListsResultPane.openSelectedPriceListEditor();
	}

	/**
	 * Adds sku prices.
	 *
	 * @param listPrice the list price.
	 * @param salePrice the sale price.
	 * @param skuCode   the sku code.
	 */
	public void addSkuPrices(final String listPrice, final String salePrice, final String skuCode) {
		priceEditorDialog = priceListEditor.clickAddPriceButton();
		priceEditorDialog.changeTypeToSku();
		selectASkuDialog = priceEditorDialog.clickSelectSkuImageLink();
		selectASkuDialog.enterSkuCode(skuCode);
		selectASkuDialog.clickSearchButton();
		selectASkuDialog.selectSkuCodeInSearchResult(skuCode);
		selectASkuDialog.clickOKButton();
		priceEditorDialog.enterListPrice(listPrice);
		if (!"".equals(salePrice)) {
			priceEditorDialog.enterSalePrice(salePrice);
		}
	}

}
package com.elasticpath.cucumber.definitions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateShippingServiceLevelDialog;
import com.elasticpath.selenium.dialogs.EditShippingServiceLevelDialog;
import com.elasticpath.selenium.editor.CartPromotionEditor;
import com.elasticpath.selenium.editor.CatalogPromotionEditor;
import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;
import com.elasticpath.selenium.navigations.PromotionsShipping;
import com.elasticpath.selenium.resultspane.PromotionSearchResultPane;
import com.elasticpath.selenium.resultspane.ShippingServiceLevelSearchResultPane;
import com.elasticpath.selenium.toolbars.PromotionsShippingActionToolbar;
import com.elasticpath.selenium.wizards.CreateCartPromotionWizard;
import com.elasticpath.selenium.wizards.CreateCatalogPromotionWizard;

/**
 * Promotions Shipping step definitions.
 */
public class PromotionsShippingDefinition {

	private static final String DASH = " - ";
	private static final int UUID_END_INDEX = 5;
	private static final int SLEEP_TIME = 500;
	private final PromotionsShipping promotionsShipping;
	private final PromotionsShippingActionToolbar promotionsShippingActionToolbar;
	private PromotionSearchResultPane promotionSearchResultPane;
	private CreateCatalogPromotionWizard createCatalogPromotionWizard;
	private CreateCartPromotionWizard createCartPromotionWizard;
	private CatalogPromotionEditor catalogPromotionEditor;
	private CartPromotionEditor cartPromotionEditor;
	private ShippingServiceLevelSearchResultPane shippingServiceLevelSearchResultPane;
	private CreateShippingServiceLevelDialog createShippingServiceLevelDialog;
	private EditShippingServiceLevelDialog editShippingServiceLevelDialog;
	private String catalogPromoName = "";
	private String cartPromoName = "";
	private String shippingServiceLevelCode = "";

	/**
	 * Constructor.
	 */
	public PromotionsShippingDefinition() {
		promotionsShipping = new PromotionsShipping(SeleniumDriverSetup.getDriver());
		promotionsShippingActionToolbar = new PromotionsShippingActionToolbar(SeleniumDriverSetup.getDriver());
	}

	/**
	 * Click Promotion Search Button.
	 */
	@When("^I click Search button in Promotion tab$")
	public void clickPromotionSearchButton() {
		promotionSearchResultPane = promotionsShipping.clickPromotionSearchButton();
	}

	/**
	 * Verify Promotion Search Results.
	 *
	 * @param promotionList The promotion list.
	 */
	@Then("^Promotion Search Results should contain following promotions?$")
	public void verifyPromotionSearchResult(final List<String> promotionList) {
		for (String promotionName : promotionList) {
			promotionSearchResultPane.verifyPromotionExists(promotionName);
		}
	}

	/**
	 * Create catalog promotion.
	 *
	 * @param catalogPromoMap the catalog promotion map.
	 */
	@When("^I create catalog promotion with following values$")
	public void createCatalogPromotion(final Map<String, String> catalogPromoMap) {
		String uuidString = UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		this.catalogPromoName = catalogPromoMap.get("name") + DASH + uuidString;

		createCatalogPromotionWizard = promotionsShippingActionToolbar.clickCreateCatalogPromotionButton();
		createCatalogPromotionWizard.selectCatalog(catalogPromoMap.get("catalog"));
		createCatalogPromotionWizard.enterPromotionName(this.catalogPromoName);
		//TODO will enable when we get the id on element
		createCatalogPromotionWizard.enterPromotionDisplayName(catalogPromoMap.get("display name") + DASH + uuidString);
		createCatalogPromotionWizard.enterEnableDateTime("Mar 17, 2017 2:39 PM");
		createCatalogPromotionWizard.clickNextInDialog();
		createCatalogPromotionWizard.openConditionMenu();
		createCatalogPromotionWizard.selectConditionMenuItem(catalogPromoMap.get("condition menu item"));
		createCatalogPromotionWizard.openDiscountMenu();
		createCatalogPromotionWizard.selectDiscountMenuItem(catalogPromoMap.get("discount menu item"));
		createCatalogPromotionWizard.enterDiscountValue(catalogPromoMap.get("discount value"));
		createCatalogPromotionWizard.clickFinish();
	}

	/**
	 * Verify New catalog promotion.
	 */
	@And("^I verify newly created catalog promotion exists$")
	public void verifyNewCatalogPromotion() {
		isPromotionInList(this.catalogPromoName);
	}

	/**
	 * Disable new catalog promotion.
	 */
	@And("^I disable newly created category promotion$")
	public void disableNewCatalogPromotion() {
		catalogPromotionEditor = promotionSearchResultPane.openCatalogPromotionEditor();
		catalogPromotionEditor.enterCurrentExpirationDateTime();
		promotionsShippingActionToolbar.saveAll();
	}

	/**
	 * Verify Catalog promotion state.
	 *
	 * @param state the state.
	 */
	@And("^catalog promotion state should be (.+)$")
	public void verifyCatalogPromotionState(final String state) {
		catalogPromotionEditor.verifyPromoState(state);
	}

	/**
	 * Create cart promotion.
	 *
	 * @param cartPromoMap the cart promotion map.
	 */
	@When("^I create cart promotion with following values$")
	public void createCartPromotion(final Map<String, String> cartPromoMap) {
		String uuidString = UUID.randomUUID().toString().substring(0, UUID_END_INDEX);
		this.cartPromoName = cartPromoMap.get("name") + DASH + uuidString;

		createCartPromotionWizard = promotionsShippingActionToolbar.clickCreateCartPromotionButton();
		createCartPromotionWizard.selectStore(cartPromoMap.get("store"));
		createCartPromotionWizard.enterPromotionName(this.cartPromoName);
		//TODO will enable when we get the id on element
		createCartPromotionWizard.enterPromotionDisplayName(cartPromoMap.get("display name") + DASH + uuidString);
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.clickNextInDialog();
		createCartPromotionWizard.openConditionMenu();
		createCartPromotionWizard.selectConditionMenuItem(cartPromoMap.get("condition menu item"));
		createCartPromotionWizard.openDiscountMenuAndSelectMenuItem(cartPromoMap.get("discount menu item"));
		createCartPromotionWizard.selectDiscountSubMenuItem(cartPromoMap.get("discount sub menu item"));
		createCartPromotionWizard.enterDiscountValue(cartPromoMap.get("discount value"));
		createCartPromotionWizard.clickFinish();
	}

	/**
	 * Verify new cart promotion.
	 */
	@And("^I verify newly created cart promotion exists$")
	public void verifyNewCartPromotion() {
		isPromotionInList(this.cartPromoName);
	}

	/**
	 * Disable new cart promotion.
	 */
	@And("^I disable newly created cart promotion$")
	public void disableNewCartPromotion() {
		cartPromotionEditor = promotionSearchResultPane.openCartPromotionEditor();
		cartPromotionEditor.disableCartPromotion();
		promotionsShippingActionToolbar.saveAll();
	}

	/**
	 * Verify cart promotion state.
	 *
	 * @param state the state.
	 */
	@And("^cart promotion state should be (.+)$")
	public void verifyCartPromotionState(final String state) {
		cartPromotionEditor.verifyPromoState(state);
	}

	/**
	 * Click shipping service level tab.
	 */
	@When("^I click Shipping Service Levels tab$")
	public void clickShippingServiceLevelTab() {
		promotionsShipping.clickShippingServiceLevelTab();
	}

	/**
	 * Click shipping service level search button.
	 */
	@When("^I click Search button in Shipping Service Levels tab$")
	public void clickShippingServiceLevelSearchButton() {
		promotionsShipping.clickShippingServiceLevelTab();
		shippingServiceLevelSearchResultPane = promotionsShipping.clickShippingServiceSearchButton();
	}

	/**
	 * Verify shipping service level search result.
	 *
	 * @param serviceLevelCodeList the code list.
	 */
	@Then("^Shipping Service Level Search Results should contain following service level codes?$")
	public void verifyShippingServiceLevelSearchResult(final List<String> serviceLevelCodeList) {
		for (String serviceLevelCode : serviceLevelCodeList) {
			shippingServiceLevelSearchResultPane.verifyShippingServiceLevelExists(serviceLevelCode);
		}
	}

	/**
	 * Verify shipping service level name sarch results.
	 *
	 * @param serviceLevelNameList the name list.
	 */
	@Then("^Shipping Service Level Search Results should contain following service level names?$")
	public void verifyShippingServiceLevelNameSearchResult(final List<String> serviceLevelNameList) {
		for (String serviceLevelName : serviceLevelNameList) {
			shippingServiceLevelSearchResultPane.verifyShippingServiceLevelExistsByName(serviceLevelName);
		}
	}

	/**
	 * Create shipping service level.
	 *
	 * @param shippingServiceLevelMap the shipping service levels.
	 */
	@When("^I create shipping service level with following values$")
	public void createShippingServiceLevel(final Map<String, String> shippingServiceLevelMap) {
		this.shippingServiceLevelCode = "SSL-" + UUID.randomUUID().toString().substring(0, UUID_END_INDEX);

		promotionsShipping.clickShippingServiceLevelTab();
		shippingServiceLevelSearchResultPane = new ShippingServiceLevelSearchResultPane(SeleniumDriverSetup.getDriver());
		shippingServiceLevelSearchResultPane.clickCreateServiceLevelResultsTab();
		createShippingServiceLevelDialog = shippingServiceLevelSearchResultPane.clickCreateServiceLevelButton();
		createShippingServiceLevelDialog.selectStore(shippingServiceLevelMap.get("store"));
		createShippingServiceLevelDialog.selectShippingRegion(shippingServiceLevelMap.get("shipping region"));
		createShippingServiceLevelDialog.selectCarrier(shippingServiceLevelMap.get("carrier"));
		createShippingServiceLevelDialog.enterUniqueCode(this.shippingServiceLevelCode);
		createShippingServiceLevelDialog.enterName(shippingServiceLevelMap.get("name") + DASH + this.shippingServiceLevelCode);
		createShippingServiceLevelDialog.enterPropertyValue(shippingServiceLevelMap.get("property value"));
		createShippingServiceLevelDialog.clickSaveButton();
	}

	/**
	 * Edit shipping service level.
	 *
	 * @param shippingServiceLevelName the service level name to edit.
	 */
	@When("^I edit the shipping service level name to (.+)$")
	public void editShippingServiceLevel(final String shippingServiceLevelName) {
		editShippingServiceLevelDialog.enterName(shippingServiceLevelName);
		editShippingServiceLevelDialog.clickSaveButton();
	}

	/**
	 * Verify new shipping service level.
	 */
	@And("^I verify newly created shipping service level exists$")
	public void verifyNewShippingServiceLevel() {
		isShippingServiceLevelInList(this.shippingServiceLevelCode);
	}

	/**
	 * Delete new shipping service level.
	 */
	@When("^I delete the newly created shipping service level$")
	public void deleteNewShippingServiceLevel() {
		isShippingServiceLevelInList(this.shippingServiceLevelCode);
		shippingServiceLevelSearchResultPane.clickDeleteServiceLevelButton();
		new ConfirmDialog(SeleniumDriverSetup.getDriver()).clickOKButton();
	}

	/**
	 * Verify shipping service level is deleted.
	 */
	@And("^I verify shipping service level is deleted$")
	public void verifyShippingServiceLevelIsDeleted() {
		shippingServiceLevelSearchResultPane = promotionsShipping.clickShippingServiceSearchButton();
		shippingServiceLevelSearchResultPane.verifyShippingServiceLevelIsDeleted(this.shippingServiceLevelCode);
	}

	/**
	 * Open shipping service level editor.
	 *
	 * @param serviceLevelName the shipping service level name.
	 */
	@Then("^I open the shipping service level editor for (.+)$")
	public void openShippingServiceLevelEditor(final String serviceLevelName) {
		shippingServiceLevelSearchResultPane.selectServiceLevelByCode(serviceLevelName);
		editShippingServiceLevelDialog = shippingServiceLevelSearchResultPane.clickOpenServiceLevelResultsTab();
	}

	/**
	 * Open newly created shipping service level editor.
	 */
	@Then("^I open the newly created shipping service level$")
	public void openNewlyCreatedShippingServiceLevelEditor() {
		shippingServiceLevelSearchResultPane.selectServiceLevelByCode(this.shippingServiceLevelCode);
		editShippingServiceLevelDialog = shippingServiceLevelSearchResultPane.clickOpenServiceLevelResultsTab();
	}

	/**
	 * Is shipping service level in list.
	 *
	 * @param shippingServiceLevelCode the shipping service level code.
	 */
	public void isShippingServiceLevelInList(final String shippingServiceLevelCode) {
		shippingServiceLevelSearchResultPane = promotionsShipping.clickShippingServiceSearchButton();
		boolean isServiceLevelInList = shippingServiceLevelSearchResultPane.isShippingServiceLevelInList(shippingServiceLevelCode);

		int index = 0;
		while (!isServiceLevelInList && index < UUID_END_INDEX) {
			shippingServiceLevelSearchResultPane.sleep(SLEEP_TIME);
			promotionsShipping.clickShippingServiceSearchButton();
			isServiceLevelInList = shippingServiceLevelSearchResultPane.isShippingServiceLevelInList(shippingServiceLevelCode);
			index++;
		}

		assertThat(isServiceLevelInList)
				.as("Shipping Service Level does not exist in search result - " + shippingServiceLevelCode)
				.isTrue();
	}

	/**
	 * Is promotion in list.
	 *
	 * @param promoName the promotion name.
	 */
	public void isPromotionInList(final String promoName) {
		promotionsShipping.enterPromotionName(promoName);
		promotionSearchResultPane = promotionsShipping.clickPromotionSearchButton();
		boolean isPromotionInList = promotionSearchResultPane.isPromotionInList(promoName);

		int index = 0;
		while (!isPromotionInList && index < UUID_END_INDEX) {
			promotionSearchResultPane.sleep(SLEEP_TIME);
			promotionsShipping.clickPromotionSearchButton();
			isPromotionInList = promotionSearchResultPane.isPromotionInList(promoName);
			index++;
		}

		assertThat(isPromotionInList)
				.as("Promotion does not exist in search result - " + promoName)
				.isTrue();
	}

	/**
	 * Verify create catalog promotion button is present.
	 */
	@And("^I can view Create Catalog Promotion button")
	public void verifyCreateCatalogPromotionButtonIsPresent() {
		promotionsShippingActionToolbar.verifyCreateCatalogPromotionButtonIsPresent();
	}

}

package com.elasticpath.selenium.wizards;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.dialogs.AddShortTextAttributeDialog;
import com.elasticpath.selenium.dialogs.BasePriceEditorDialog;
import com.elasticpath.selenium.dialogs.EditDecimalValueAttributeDialog;
import com.elasticpath.selenium.dialogs.EditIntegerValueAttributeDialog;
import com.elasticpath.selenium.dialogs.EditShortTextMultiValueAttributeDialog;

/**
 * Create Product Dialog.
 */
public class CreateProductWizard extends AbstractWizard {

	private static final String CREATE_PRODUCT_PARENT_CSS = "div[widget-id='Create Product'][widget-type='Shell'] ";
	private static final String PRODUCT_CODE_INPUT_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Product Code'] > input";
	private static final String PRODUCT_NAME_INPUT_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Product Name'] > input";
	private static final String PRODUCT_TYPE_COMBO_PARENT_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Product Type'][widget-type='CCombo']";
	private static final String TAX_CODE_COMBO_PARENT_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Tax Code'][widget-type='CCombo']";
	private static final String BRAND_COMBO_PARENT_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Brand'][widget-type='CCombo']";
	private static final String AVAILABILITY_RULE_COMBO_PARENT_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Availability "
			+ "Rule'][widget-type='CCombo']";
	private static final String SKU_CODE_INPUT_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='SKU Code'] > input";
	private static final String PRICE_LIST_COMBO_PARENT_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id=''][widget-type='CCombo']";
	private static final String ADD_BASE_PRICE_BUTTON_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Add Price...']";
	private static final String EDIT_ATTRIBUTE_VALUE_BUTTON_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Edit Attribute Value...']";
	private static final String CLEAR_ATTRIBUTE_VALUE_BUTTON_CSS = CREATE_PRODUCT_PARENT_CSS + "div[widget-id='Clear Attribute Value']";
	private static final String SHIPPABLE_TYPE_SHIPPABLE_BUTTON_CSS = CREATE_PRODUCT_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorSingleSkuOverview_Shippable'][seeable='true']";
	private static final String SHIPPABLE_TYPE_DIGITAL_ASSET_BUTTON_CSS = CREATE_PRODUCT_PARENT_CSS
			+ "div[automation-id='com.elasticpath.cmclient.catalog.CatalogMessages.ProductEditorSingleSkuOverview_DigitalAsset'][seeable='true']";
	private static final String CREATE_PRODUCT_ATTRIBUTE_PARENT_CSS = "div[widget-id='Attributes View'][widget-type='Table'] ";
	private static final String CREATE_PRODUCT_ATTRIBUTE_COLUMN_CSS = CREATE_PRODUCT_ATTRIBUTE_PARENT_CSS + "div[column-id='%s']";
	private static final String ATTRIBUTE_NAME_COLUMNNAME = "Name";


	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public CreateProductWizard(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Inputs product code.
	 *
	 * @param productCode the product code.
	 */
	public void enterProductCode(final String productCode) {
		clearAndType(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRODUCT_CODE_INPUT_CSS)), productCode);
	}

	/**
	 * Inputs sku code.
	 *
	 * @param skuCode the sku code.
	 */
	public void enterSkuCode(final String skuCode) {
		clearAndType(getDriver().findElement(By.cssSelector(SKU_CODE_INPUT_CSS)), skuCode);
	}

	/**
	 * Selects shippable type.
	 *
	 * @param shippableType the shippabe type.
	 */
	public void selectShippableType(final String shippableType) {
		if ("Shippable".equalsIgnoreCase(shippableType)) {
			getDriver().findElement(By.cssSelector(SHIPPABLE_TYPE_SHIPPABLE_BUTTON_CSS)).click();
			getWaitDriver().waitForPageLoad();
		} else if ("Digital Asset".equalsIgnoreCase(shippableType)) {
			getDriver().findElement(By.cssSelector(SHIPPABLE_TYPE_DIGITAL_ASSET_BUTTON_CSS)).click();
			getWaitDriver().waitForPageLoad();
			//TODO - will remove once button is enabled on click
			getDriver().findElement(By.cssSelector(SHIPPABLE_TYPE_DIGITAL_ASSET_BUTTON_CSS)).click();
		} else {
			assertThat("Shippable".equalsIgnoreCase(shippableType) || "Digital Asset".equalsIgnoreCase(shippableType))
					.as("Invalid shippable type entered - " + shippableType)
					.isTrue();
		}
	}

	/**
	 * Inputs product name.
	 *
	 * @param productName the product name.
	 */
	public void enterProductName(final String productName) {
		clearAndType(getDriver().findElement(By.cssSelector(PRODUCT_NAME_INPUT_CSS)), productName);
	}

	/**
	 * Selects a product type in combo box.
	 *
	 * @param productType the product type.
	 */
	public void selectProductType(final String productType) {
		assertThat(selectComboBoxItem(PRODUCT_TYPE_COMBO_PARENT_CSS, productType))
				.as("Unable to find product type - " + productType)
				.isTrue();
	}

	/**
	 * Selects a tax code in combo box.
	 *
	 * @param taxCode the tax code.
	 */
	public void selectTaxCode(final String taxCode) {
		assertThat(selectComboBoxItem(TAX_CODE_COMBO_PARENT_CSS, taxCode))
				.as("Unable to find tax code - " + taxCode)
				.isTrue();
	}

	/**
	 * Selects a brand in combo box.
	 *
	 * @param brand the brand.
	 */
	public void selectBrand(final String brand) {
		assertThat(selectComboBoxItem(BRAND_COMBO_PARENT_CSS, brand))
				.as("Unable to find brand - " + brand)
				.isTrue();
	}

	/**
	 * Selects availability rule in combo box.
	 *
	 * @param availabilityRule the availability rule.
	 */
	public void selectAvailabilityRule(final String availabilityRule) {
		assertThat(selectComboBoxItem(AVAILABILITY_RULE_COMBO_PARENT_CSS, availabilityRule))
				.as("Unable to find availability rule - " + availabilityRule)
				.isTrue();
	}

	/**
	 * Check store visible box.
	 */
	public void checkStoreVisibleBox() {
		getDriver().findElement(By.xpath("//div[contains(text(), 'Store Visible')]/../../following-sibling::div[1]/div")).click();
	}

	/**
	 * Selects price list in combo box.
	 *
	 * @param priceListName the price list name.
	 */
	public void selectPriceList(final String priceListName) {
		assertThat(selectComboBoxItem(PRICE_LIST_COMBO_PARENT_CSS, priceListName))
				.as("Unable to find price list - " + priceListName)
				.isTrue();
	}

	/**
	 * Enter attribute short text multi value.
	 *
	 * @param value         th value.
	 * @param attributeName the attribute name.
	 */
	public void enterAttributeShortTextMultiValue(final String value, final String attributeName) {
		assertThat(selectItemInDialog(CREATE_PRODUCT_ATTRIBUTE_PARENT_CSS, CREATE_PRODUCT_ATTRIBUTE_COLUMN_CSS, attributeName,
				ATTRIBUTE_NAME_COLUMNNAME))
				.as("Unable to find attribute - " + attributeName)
				.isTrue();

		EditShortTextMultiValueAttributeDialog editShortTextMultiValueAttributeDialog = clickEditAttributeButtonShortTextMultiValue();
		AddShortTextAttributeDialog addShortTextAttributeDialog = editShortTextMultiValueAttributeDialog.clickAddValueButton();
		addShortTextAttributeDialog.enterShortTextValue(value);
		addShortTextAttributeDialog.clickOKButton();
		editShortTextMultiValueAttributeDialog.clickOKButton();
	}

	/**
	 * Enter attribute integer value.
	 *
	 * @param value         the value.
	 * @param attributeName the attribute name.
	 */
	public void enterAttributeIntegerValue(final String value, final String attributeName) {
		assertThat(selectItemInDialog(CREATE_PRODUCT_ATTRIBUTE_PARENT_CSS, CREATE_PRODUCT_ATTRIBUTE_COLUMN_CSS, attributeName,
				ATTRIBUTE_NAME_COLUMNNAME))
				.as("Unable to find attribute - " + attributeName)
				.isTrue();

		EditIntegerValueAttributeDialog editIntegerValueAttributeDialog = clickEditAttributeButtonIntegerValue();
		editIntegerValueAttributeDialog.enterIntegerValue(value);
		editIntegerValueAttributeDialog.clickOKButton();
	}

	/**
	 * Enter attribute decimal value.
	 *
	 * @param value         the value.
	 * @param attributeName the attribute name.
	 */
	public void enterAttributeDecimalValue(final String value, final String attributeName) {
		assertThat(selectItemInDialog(CREATE_PRODUCT_ATTRIBUTE_PARENT_CSS, CREATE_PRODUCT_ATTRIBUTE_COLUMN_CSS, attributeName,
				ATTRIBUTE_NAME_COLUMNNAME))
				.as("Unable to find attribute - " + attributeName)
				.isTrue();

		EditDecimalValueAttributeDialog editDecimalValueAttributeDialog = clickEditAttributeButtonDecimalValue();
		editDecimalValueAttributeDialog.enterDecimalValue(value);
		editDecimalValueAttributeDialog.clickOKButton();
	}

	/**
	 * Edit Click edit attribute button short text multi value.
	 *
	 * @return the edit short text multi value attribute dialog.
	 */
	public EditShortTextMultiValueAttributeDialog clickEditAttributeButtonShortTextMultiValue() {
		getDriver().findElement(By.cssSelector(EDIT_ATTRIBUTE_VALUE_BUTTON_CSS)).click();
		return new EditShortTextMultiValueAttributeDialog(getDriver());
	}

	/**
	 * Click edit attribute button for integer value.
	 *
	 * @return the dialog.
	 */
	public EditIntegerValueAttributeDialog clickEditAttributeButtonIntegerValue() {
		getDriver().findElement(By.cssSelector(EDIT_ATTRIBUTE_VALUE_BUTTON_CSS)).click();
		return new EditIntegerValueAttributeDialog(getDriver());
	}

	/**
	 * Click edit attribute button for decimal value.
	 *
	 * @return the dialog.
	 */
	public EditDecimalValueAttributeDialog clickEditAttributeButtonDecimalValue() {
		getDriver().findElement(By.cssSelector(EDIT_ATTRIBUTE_VALUE_BUTTON_CSS)).click();
		return new EditDecimalValueAttributeDialog(getDriver());
	}

	/**
	 * Click clear attribute button.
	 */
	public void clickClearAttributeButton() {
		getDriver().findElement(By.cssSelector(CLEAR_ATTRIBUTE_VALUE_BUTTON_CSS)).click();
	}

	/**
	 * Click add base price button.
	 *
	 * @return the base price editor dialog.
	 */
	public BasePriceEditorDialog clickAddBasePriceButton() {
		getDriver().findElement(By.cssSelector(ADD_BASE_PRICE_BUTTON_CSS)).click();
		return new BasePriceEditorDialog(getDriver());
	}

}

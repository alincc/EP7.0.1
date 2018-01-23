package com.elasticpath.selenium.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;

/**
 * Product Editor.
 */
public class ProductEditor extends AbstractPageObject {

	private static final String PRODUCT_EDITOR_PARENT_CSS = "div[pane-location='editor-pane'] div[active-editor='true'] ";
	private static final String PRODUCT_NAME_INPUT_CSS = PRODUCT_EDITOR_PARENT_CSS + "div[widget-id='Product Name'][widget-type='Text'] > input";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public ProductEditor(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verify product name.
	 *
	 * @param productName the product name.
	 */
	public void verifyProductName(final String productName) {
		assertThat(getWaitDriver().waitForElementToBeVisible(By.cssSelector(PRODUCT_NAME_INPUT_CSS)).getAttribute("value"))
				.as("Product name validation failed")
				.isEqualTo(productName);
	}

}

package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.editor.CartPromotionEditor;
import com.elasticpath.selenium.editor.CatalogPromotionEditor;

/**
 * Promotion search results pane.
 */
public class PromotionSearchResultPane extends AbstractPageObject {
	private static final String PROMOTION_SEARCH_RESULT_PARENT_CSS = "div[widget-id='Promotions Search Results'][widget-type='Table'] ";
	private static final String PROMOTION_SEARCH_RESULT_LIST_CSS = PROMOTION_SEARCH_RESULT_PARENT_CSS + "div[column-id='%s']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public PromotionSearchResultPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if promotion exists.
	 *
	 * @param promotionName String
	 */
	public void verifyPromotionExists(final String promotionName) {
		assertThat(selectItemInCenterPane(PROMOTION_SEARCH_RESULT_PARENT_CSS, PROMOTION_SEARCH_RESULT_LIST_CSS, promotionName, "Promotion Name"))
				.as("Expected Promotion does not exist in search result - " + promotionName)
				.isTrue();
	}

	/**
	 * Opens catalog promotion editor.
	 *
	 * @return the catalog promotion editor.
	 */
	public CatalogPromotionEditor openCatalogPromotionEditor() {
		doubleClick(getSelectedElement());
		return new CatalogPromotionEditor(getDriver());
	}

	/**
	 * Verifies if promotion exists.
	 *
	 * @param expectedPromotionName the expected promotion name.
	 * @return true if is in list.
	 */
	public boolean isPromotionInList(final String expectedPromotionName) {
		getWaitDriver().adjustWaitInterval(1);
		boolean isPromoInList = selectItemInCenterPane(PROMOTION_SEARCH_RESULT_PARENT_CSS, PROMOTION_SEARCH_RESULT_LIST_CSS,
				expectedPromotionName, "Promotion Name");
		getWaitDriver().adjustWaitBackToDefault();
		return isPromoInList;
	}


	/**
	 * Opens cart promotion editor.
	 *
	 * @return Cart promotion editor.
	 */
	public CartPromotionEditor openCartPromotionEditor() {
		doubleClick(getSelectedElement());
		return new CartPromotionEditor(getDriver());
	}

}

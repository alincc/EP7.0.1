/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.shoppingcart;

/**
 * Collection of StructuredErrorMessage message ids for the cart domain.
 * These message ids should be used for localization of error messages on client side.
 */
public final class ShoppingCartMessageIds {
	/**
	 * Product can not be added to the cart.
	 */
	public static final String PRODUCT_NOT_AVAILABLE = "cart.product.not.available";
	/**
	 * Missing well defined product on sku object.
	 */
	public static final String SKU_MISSING_PRODUCT = "cart.sku.missing.product";
	/**
	 * Missing price for specific product.
	 */
	public static final String PRODUCT_MISSING_PRICE = "cart.product.missing.price";
	/**
	 * Product is not purchasable.
	 */
	public static final String PRODUCT_NOT_PURCHASABLE = "cart.product.not.purchasable";

	private ShoppingCartMessageIds() {
		// private constructor to ensure class can't be instantiated
	}
}

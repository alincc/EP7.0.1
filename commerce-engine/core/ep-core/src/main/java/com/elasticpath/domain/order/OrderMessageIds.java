/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.order;

/**
 * Collection of StructuredErrorMessage message ids for the order domain.
 * These message ids should be used for localization of error messages on client side.
 */
public final class OrderMessageIds {
	/**
	 * Shipment can not be complete as the payment operation failed.
	 */
	public static final String PAYMENT_FAILED = "purchase.payment.failed";
	/**
	 * Complete shipment failed error during shipment release phase.
	 */
	public static final String COMPLETE_SHIPMENT_FAILED = "purchase.complete.shipment.failed";
	/**
	 * Purchase failed as customer is not in an active state.
	 */
	public static final String CUSTOMER_NOT_ACTIVE = "purchase.customer.not.active";
	/**
	 * Purchase failed as shipping address is not defined.
	 */
	public static final String SHIPPING_ADDRESS_MISSING = "purchase.missing.shipping.address";
	/**
	 * Purchase failed as shipping service level is not defined.
	 */
	public static final String SHIPPING_SERVICE_LEVEL_MISSING = "purchase.shipping.service.level.missing";
	/**
	 * Purchase failed as shipping service level is invalid.
	 */
	public static final String SHIPPING_SERVICE_LEVEL_INVALID = "purchase.shipping.service.level.invalid";
	/**
	 * Purchase failed as the shopping cart is empty.
	 */
	public static final String CART_IS_EMPTY = "purchase.cart.is.empty";
	/**
	 * Purchase failed as the sku code is unavailable.
	 */
	public static final String SKU_CODE_UNAVAILABLE = "purchase.sku.code.not.available";
	/**
	 * Purchase failed as one product's quantity is of insufficient inventory.
	 */
	public static final String INSUFFICIENT_INVENTORY = "purchase.product.insufficient.inventory";
	/**
	 * Purchase failed as one product's quantity is of insufficient inventory in the given warehouse.
	 */
	public static final String INSUFFICIENT_INVENTORY_WAREHOUSE = "purchase.product.insufficient.inventory.warehouse";
	/**
	 * Purchase failed as one product is not in the required minimum quantity in the cart.
	 */
	public static final String MINIMUM_QUANTITY_REQUIRED = "purchase.product.minimum.quantity.required";

	private OrderMessageIds() {
		// private constructor to ensure class can't be instantiated
	}
}

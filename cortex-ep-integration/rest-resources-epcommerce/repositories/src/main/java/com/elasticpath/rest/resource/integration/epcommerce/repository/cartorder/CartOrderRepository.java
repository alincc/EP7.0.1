/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder;

import java.util.Collection;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for operations with orders.
 */
public interface CartOrderRepository {

	/**
	 * Possible options for fetching cart orders.
	 */
	enum FindCartOrder {
		/** By order guid.*/
		BY_ORDER_GUID,
		/** By cart guid.*/
		BY_CART_GUID,
		/** By shipment id.*/
		BY_SHIPMENT_DETAILS_ID
	}

	/**
	 * Get the order based on order GUID.
	 *
	 * @param storeCode the store code
	 * @param cartOrderGuid the cart order GUID
	 * @return ExecutionResult with the order
	 */
	ExecutionResult<CartOrder> findByGuid(String storeCode, String cartOrderGuid);

	/**
	 * Get the order based on shopping cart GUID.
	 *
	 * @param cartGuid the shopping cart GUID
	 * @return ExecutionResult with the order
	 */
	ExecutionResult<CartOrder> findByCartGuid(String cartGuid);

	/**
	 * Get the order based on shipment details id.
	 *
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details id
	 * @return ExecutionResult with the order
	 */
	ExecutionResult<CartOrder> findByShipmentDetailsId(String storeCode, String shipmentDetailsId);

	/**
	 * Find cart order GUIDS by customer GUID.
	 *
	 * @param storeCode the store code
	 * @param customerGuid the customer GUID
	 * @return ExecutionResult with the list of order guids
	 */
	ExecutionResult<Collection<String>> findCartOrderGuidsByCustomer(String storeCode, String customerGuid);

	/**
	 * Gets the billing address for the given {@link CartOrder}.
	 *
	 * @param cartOrder the cart order
	 * @return ExecutionResult with the billing address
	 */
	ExecutionResult<Address> getBillingAddress(CartOrder cartOrder);

	/**
	 * Gets the shipping address for the given {@link CartOrder}.
	 *
	 * @param cartOrder the cart order
	 * @return ExecutionResult with the shipping address
	 */
	ExecutionResult<Address> getShippingAddress(CartOrder cartOrder);

	/**
	 * Saves/Updates the Cart Order.
	 *
	 * @param cartOrder The cart order to save.
	 * @return ExecutionResult with the updated reference to the cart Order.
	 */
	ExecutionResult<CartOrder> saveCartOrder(CartOrder cartOrder);

	/**
	 * Gets the shopping cart populated with the transient fields given the cart order.
	 *
	 * @param storeCode the store code
	 * @param cartOrderGuid the cart or order guid
	 * @param findBy enum for using correct find method to get a cart order
	 * @return ExecutionResult with the enriched shopping cart
	 */
	ExecutionResult<ShoppingCart> getEnrichedShoppingCart(String storeCode, String cartOrderGuid, FindCartOrder findBy);

	/**
	 * Gets the shopping cart populated with the transient fields given the cart order.
	 *
	 * @param storeCode the store code
	 * @param cartOrder the cart order
	 * @return ExecutionResult with the enriched shopping cart
	 */
	ExecutionResult<ShoppingCart> getEnrichedShoppingCart(String storeCode, CartOrder cartOrder);
	/**
	 * Update the shipping address on the cart order. This may affect the selected shipping service level as well.
	 *
	 * @param shippingAddressGuid shipping address guid.
	 * @param cartOrderGuid cart order guid.
	 * @param storeCode store code
	 * @return true if cart order address guid is updated, false otherwise.
	 */
	ExecutionResult<Boolean> updateShippingAddressOnCartOrder(String shippingAddressGuid, String cartOrderGuid, String storeCode);

	/**
	 * Filter existing coupons on cart order and auto apply new coupons.
	 *
	 * @param cartOrder cart order.
	 * @param store the store
	 * @param customerEmailAddress the customer email address
	 * @return true if cart order is updated, false otherwise.
	 */
	ExecutionResult<Boolean> filterAndAutoApplyCoupons(CartOrder cartOrder, Store store, String customerEmailAddress);

	/**
	 * Get cart order order guid for given shipment.
	 *
	 * @param shipmentDetailsId the shipment id
	 * @return ExecutionResult with cart order guid
	 */
	ExecutionResult<String> getCartOrderGuidByShipmentDetailsId(String shipmentDetailsId);

	/**
	 * Get cart order for given store code, cart order gui and enum.
	 *
	 * @param storeCode the store code
	 * @param guid the cart guid
	 * @param findBy one of (@link FindCartOrder) values
	 * @return ExecutionResult with cart order
	 */
	ExecutionResult<CartOrder> getCartOrder(String storeCode, String guid, FindCartOrder findBy);

	/**
	 * Get shipping options for given store code and shipping address.
	 *
	 * @param storeCode the store code
	 * @param shippingAddress the shipping address
	 * @return collection of shipping options
	 */
	Collection<ShippingServiceLevel> findShippingServiceLevels(String storeCode, Address shippingAddress);

	/**
	 * Get shopping cart guid for given store code and cart order guid.
	 *
	 * @param storeCode the store code
	 * @param cartOrderGuid the cart order guid
	 * @return shopping cart guid
	 */
	String getShoppingCartGuid(String storeCode, String cartOrderGuid);
}

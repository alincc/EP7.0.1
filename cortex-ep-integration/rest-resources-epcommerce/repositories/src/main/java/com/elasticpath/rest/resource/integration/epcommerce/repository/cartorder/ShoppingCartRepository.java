/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder;

import java.util.Collection;
import java.util.Map;


import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for operations with the cart.
 */
public interface ShoppingCartRepository {

	/**
	 * Gets the default shopping cart.
	 *
	 * @return ExecutionResult with the default shopping cart
	 */
	ExecutionResult<ShoppingCart> getDefaultShoppingCart();

	/**
	 * Gets the shopping cart with given GUID.
	 *
	 * @param cartGuid the cart guid
	 * @return ExecutionResult with the shopping cart
	 */
	ExecutionResult<ShoppingCart> getShoppingCart(String cartGuid);

	/**
	 * Check if the shopping cart with the specified guid is valid for the current scope.
	 *
	 * @param cartGuid  shoppingCart id.
	 * @param storeCode storeCode.
	 * @return boolean true if the cart exists.
	 */
	boolean verifyShoppingCartExistsForStore(String cartGuid, String storeCode);

	/**
	 * Since you inevitably run fireRules() and blow away the record
	 * of the applied catalog promotions, this method will re-price
	 * all your items and re-record the catalog promotions for you.
	 *
	 * @param cart The cart.
	 */
	void reApplyCatalogPromotions(ShoppingCart cart);

	/**
	 * Adds an item to the cart.
	 *
	 * @param cart     the shopping cart
	 * @param skuCode  sku code
	 * @param quantity new item count
	 * @param fields   the shopping item fields
	 * @return the new ShoppingItem and the updated ShoppingCart
	 */
	ExecutionResult<ShoppingCart> addItemToCart(ShoppingCart cart, String skuCode, int quantity, Map<String, String> fields);

	/**
	 * Moves an item to the cart from a wishlist.
	 *
	 * @param cart                 the shopping cart
	 * @param wishlistLineItemGuid the line item guid
	 * @param skuCode              sku code
	 * @param quantity             new item count
	 * @param fields               configuration
	 * @return the updated ShoppingCart
	 */
	ExecutionResult<ShoppingCart> moveItemToCart(ShoppingCart cart, String wishlistLineItemGuid, String skuCode, int quantity,
			Map<String, String> fields);

	/**
	 * Moves an item to a wishlist from a cart.
	 *
	 * @param cart             the shopping cart
	 * @param cartLineItemGuid the line item guid
	 * @return the new ShoppingItem
	 */
	ExecutionResult<ShoppingItem> moveItemToWishlist(ShoppingCart cart, String cartLineItemGuid);

	/**
	 * Updates an existing item to the cart.
	 *
	 * @param cart            the shopping cart
	 * @param shoppingItemUid the id of the shopping item that should be updated
	 * @param skuCode         sku code
	 * @param quantity        new item count
	 * @return the updated ShoppingItem and the updated ShoppingCart
	 */
	ExecutionResult<Void> updateCartItem(
			ShoppingCart cart, long shoppingItemUid, String skuCode, int quantity);

	/**
	 * Removes an existing item from the cart.
	 *
	 * @param cart            the shopping cart
	 * @param shoppingItemUid the id of the shopping item that should be removed
	 * @return the updated ShoppingCart
	 */
	ExecutionResult<Void> removeItemFromCart(ShoppingCart cart, long shoppingItemUid);

	/**
	 * Removes all existing items from the cart.
	 *
	 * @param storeCode the store code
	 * @param cartGuid the shopping cart guid
	 *
	 * @return the whether the operation was successful.
	 */
	ExecutionResult<Void> removeAllItemsFromCart(String storeCode, String cartGuid);

	/**
	 * Gets the default shopping cart GUID.
	 *
	 * @param storeCode the store code
	 * @return the default shopping cart GUID
	 */
	ExecutionResult<String> getDefaultShoppingCartGuid(String storeCode);

	/**
	 * Gets the default shopping cart for the customer.
	 *
	 * @param customerGuid  The customer.
	 * @param storeCode The store.
	 * @return the ShoppingCart
	 */
	ExecutionResult<ShoppingCart> getShoppingCart(String customerGuid, String storeCode);

	/**
	 * Find All of the shopping carts for this customer.
	 *
	 * @param customerGuid Customer GUID
	 * @param storeCode    valid store code
	 * @return Collection of cart GUID's
	 */
	ExecutionResult<Collection<String>> findAllCarts(String customerGuid, String storeCode);
}

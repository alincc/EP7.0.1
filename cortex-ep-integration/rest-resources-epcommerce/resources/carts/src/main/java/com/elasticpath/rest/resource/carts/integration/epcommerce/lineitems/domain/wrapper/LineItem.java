/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper;

import java.util.Map;


import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * A DCE line item which holds all information required to construct a {@link com.elasticpath.rest.definition.carts.LineItemEntity}.
 */
public interface LineItem extends ResourceEntity {

	/**
	 * Gets the shopping item.
	 *
	 * @return the shopping item
	 */
	ShoppingItem getShoppingItem();

	/**
	 * Sets the shopping item.
	 *
	 * @param shoppingItem the shopping item
	 * @return this for chaining
	 */
	LineItem setShoppingItem(ShoppingItem shoppingItem);

	/**
	 * Gets the cart id.
	 *
	 * @return the cart id.
	 */
	String getCartId();

	/**
	 * Sets the cart id.
	 *
	 * @param cartId the cart id.
	 * @return this for chaining
	 */
	LineItem setCartId(String cartId);

	/**
	 *
	 * Gets the item id.
	 * @return the item id
	 */
	String getItemId();

	/**
	 * Sets the item id.
	 *
	 * @param itemId the item id.
	 * @return this for chaining
	 */
	LineItem setItemId(String itemId);

	/**
	 * Gets the cart item modifier values.
	 * @return the cart item modifier values
	 */
	Map<CartItemModifierField, String> getCartItemModifierValues();

	/**
	 * Sets the item modifier values.
	 *
	 * @param cartItemModifierValues the cart item modifier values
	 * @return this for chaining
	 */
	LineItem setCartItemModifierValues(Map<CartItemModifierField, String> cartItemModifierValues);
}

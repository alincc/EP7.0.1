/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.representation;

import com.elasticpath.rest.schema.Internal;
import com.elasticpath.rest.schema.Property;
import com.elasticpath.rest.schema.scope.ScopedRepresentation;

/**
 * Line Item Representation.
 */
public interface WishlistLineItemRepresentation extends ScopedRepresentation {

	/**
	 * MIME type for this resource.
	 */
	String TYPE = "elasticpath.wishlists.line-item";

	/**
	 * quantity. *
	 */
	String QUANTITY_PROPERTY = "quantity";
	/**
	 * line item id. *
	 */
	String LINE_ITEM_ID_PROPERTY = "line-item-id";
	/**
	 * item id. *
	 */
	String ITEM_ID_PROPERTY = "item-id";
	/**
	 * wishlist id. *
	 */
	String WISHLIST_ID_PROPERTY = "wishlist-id";

	/**
	 * Gets the quantity.
	 *
	 * @return the quantity as an int.
	 */
	@Property(name = QUANTITY_PROPERTY)
	Integer getQuantity();

	/**
	 * Gets the line item ID.
	 *
	 * @return the line item id.
	 */
	@Internal
	@Property(name = LINE_ITEM_ID_PROPERTY)
	String getLineItemId();

	/**
	 * Gets the item Id.
	 *
	 * @return the item Id.
	 */
	@Internal
	@Property(name = ITEM_ID_PROPERTY)
	String getItemId();

	/**
	 * Gets the wishlist id.
	 *
	 * @return the wishlist id.
	 */
	@Internal
	@Property(name = WISHLIST_ID_PROPERTY)
	String getWishlistId();
}

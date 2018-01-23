/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.representation;

import com.elasticpath.rest.schema.Internal;
import com.elasticpath.rest.schema.Property;
import com.elasticpath.rest.schema.scope.ScopedRepresentation;

/**
 * Wishlist representation interface.
 */
public interface WishlistRepresentation extends ScopedRepresentation {

	/**
	 * The type of this representation.
	 */
	String TYPE = "elasticpath.wishlists.wishlist";

	/**
	 * total-quantity.
	 */
	String TOTAL_QUANTITY_PROPERTY = "total-quantity";
	/**
	 * wishlist id.
	 */
	String WISHLIST_ID_PROPERTY = "wishlist-id";

	/**
	 * Gets the total quantity.
	 *
	 * @return the total quantity.
	 */
	@Property(name = TOTAL_QUANTITY_PROPERTY)
	Integer getTotalQuantity();

	/**
	 * Get the card ID.
	 *
	 * @return the wishlist ID
	 */
	@Internal
	@Property(name = WISHLIST_ID_PROPERTY)
	String getWishlistId();
}

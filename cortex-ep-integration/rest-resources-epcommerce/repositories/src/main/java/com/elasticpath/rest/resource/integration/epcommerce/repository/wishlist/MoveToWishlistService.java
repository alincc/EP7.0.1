/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist;

import rx.Single;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;

/**
 * Service interface for moving an item from cart to wishlist.
 */
public interface MoveToWishlistService {

	/**
	 * Move an item from cart to wishlist.
	 *
	 * @param lineItemIdentifier the line item identifier
	 * @return the wishlist line item identifier
	 */
	Single<WishlistLineItemIdentifier> move(LineItemIdentifier lineItemIdentifier);
}

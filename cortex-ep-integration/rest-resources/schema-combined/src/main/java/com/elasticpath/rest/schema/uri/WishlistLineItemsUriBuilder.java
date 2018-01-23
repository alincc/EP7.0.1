/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds URIs for wishlist line items.
 */
public interface WishlistLineItemsUriBuilder extends ReadFromOtherUriBuilder<WishlistLineItemsUriBuilder> {


	/**
	 * Set the wishlist line item ID.
	 *
	 * @param lineItemId the wishlist line item ID.
	 * @return the builder
	 */
	WishlistLineItemsUriBuilder setLineItemId(String lineItemId);
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds URIs for wishlists.
 */
public interface WishlistsUriBuilder extends ScopedUriBuilder<WishlistsUriBuilder> {

	/**
	 * Set the wishlist ID.
	 *
	 * @param wishlistId wishlist ID.
	 * @return the builder
	 */
	WishlistsUriBuilder setWishlistId(String wishlistId);

	/**
	 * Set the URI for the thing being added to the wishlist.
	 *
	 * @param addUri the source uri.
	 * @return the builder
	 */
	WishlistsUriBuilder setFormUri(String addUri);

	/**
	 * Set the URI for the item being added to the wishlist.
	 *
	 * @param itemUri the item uri.
	 * @return the builder
	 */
	WishlistsUriBuilder setItemUri(String itemUri);
}

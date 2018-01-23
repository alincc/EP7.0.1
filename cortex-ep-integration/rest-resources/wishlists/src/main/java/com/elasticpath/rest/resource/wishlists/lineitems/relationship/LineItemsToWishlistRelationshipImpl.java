/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import rx.Observable;

import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsForWishlistRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Line items to wishlist link.
 */
public class LineItemsToWishlistRelationshipImpl implements WishlistLineItemsForWishlistRelationship.LinkFrom {

	private final IdentifierPart<String> wishlistIdentifier;

	private final IdentifierPart<String> scope;

	/**
	 * Constructor.
	 *
	 * @param wishlistIdentifier wishlistIdentifier
	 * @param scope              scope
	 */
	@Inject
	public LineItemsToWishlistRelationshipImpl(@UriPart(WishlistIdentifier.WISHLIST_ID) final IdentifierPart<String> wishlistIdentifier,
			@UriPart(WishlistsIdentifier.SCOPE) final IdentifierPart<String> scope) {
		this.wishlistIdentifier = wishlistIdentifier;
		this.scope = scope;
	}

	@Override
	public Observable<WishlistIdentifier> onLinkFrom() {
		return Observable.just(WishlistIdentifier.builder()
				.withWishlistId(wishlistIdentifier)
				.withWishlists(WishlistsIdentifier.builder()
						.withScope(scope)
						.build())
				.build()
		);
	}
}

/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import rx.Observable;

import com.elasticpath.rest.definition.wishlists.WishlistForWishlistLineItemRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Line item to wishlist link.
 */
public class LineItemToWishlistRelationshipImpl implements WishlistForWishlistLineItemRelationship.LinkTo {

	private final IdentifierPart<String> wishlistIdentifier;

	private final IdentifierPart<String> scope;

	/**
	 * Constructor.
	 *
	 * @param wishlistIdentifier wishlistIdentifier
	 * @param scope              scope
	 */
	@Inject
	public LineItemToWishlistRelationshipImpl(@UriPart(WishlistIdentifier.WISHLIST_ID) final IdentifierPart<String> wishlistIdentifier,
			@UriPart(WishlistsIdentifier.SCOPE) final IdentifierPart<String> scope) {
		this.wishlistIdentifier = wishlistIdentifier;
		this.scope = scope;
	}

	@Override
	public Observable<WishlistIdentifier> onLinkTo() {
		return Observable.just(WishlistIdentifier.builder()
				.withWishlistId(wishlistIdentifier)
				.withWishlists(WishlistsIdentifier.builder()
						.withScope(scope)
						.build())
				.build()
		);
	}
}

/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import rx.Observable;

import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsForWishlistLineItemRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Line item to line items link.
 */
public class LineItemToLineItemsRelationshipImpl implements WishlistLineItemsForWishlistLineItemRelationship.LinkTo {

	private final IdentifierPart<String> wishlistIdentifier;

	private final IdentifierPart<String> scope;

	/**
	 * Constructor.
	 *
	 * @param wishlistIdentifier wishlistIdentifier
	 * @param scope              scope
	 */
	@Inject
	public LineItemToLineItemsRelationshipImpl(@UriPart(WishlistIdentifier.WISHLIST_ID) final IdentifierPart<String> wishlistIdentifier,
			@UriPart(WishlistsIdentifier.SCOPE) final IdentifierPart<String> scope) {
		this.wishlistIdentifier = wishlistIdentifier;
		this.scope = scope;
	}

	@Override
	public Observable<WishlistLineItemsIdentifier> onLinkTo() {
		return Observable.just(WishlistLineItemsIdentifier.builder()
				.withWishlist(WishlistIdentifier.builder()
						.withWishlistId(wishlistIdentifier)
						.withWishlists(WishlistsIdentifier.builder()
								.withScope(scope)
								.build())
						.build())
				.build());
	}

}

/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.prototypes;

import javax.inject.Inject;

import rx.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemsResource;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Read line items.
 */
public class ReadLineItemsPrototype implements WishlistLineItemsResource.Read {

	private final IdentifierPart<String> wishlistIdentifier;

	private final IdentifierPart<String> scope;

	private final LinksRepository<WishlistIdentifier, WishlistLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param wishlistIdentifier wishlistIdentifier
	 * @param scope              scope
	 * @param repository         repository
	 */
	@Inject
	public ReadLineItemsPrototype(@UriPart(WishlistIdentifier.WISHLIST_ID) final IdentifierPart<String> wishlistIdentifier,
								  @UriPart(WishlistsIdentifier.SCOPE) final IdentifierPart<String> scope,
								  @ResourceRepository final LinksRepository<WishlistIdentifier, WishlistLineItemIdentifier> repository) {
		this.wishlistIdentifier = wishlistIdentifier;
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Observable<WishlistLineItemIdentifier> onRead() {
		return repository.getElements(WishlistIdentifier.builder()
				.withWishlistId(wishlistIdentifier)
				.withWishlists(WishlistsIdentifier.builder()
						.withScope(scope)
						.build())
				.build());
	}
}

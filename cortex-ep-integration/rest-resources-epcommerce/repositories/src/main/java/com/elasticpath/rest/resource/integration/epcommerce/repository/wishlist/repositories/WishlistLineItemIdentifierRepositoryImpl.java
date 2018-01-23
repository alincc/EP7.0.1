/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.repositories;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import rx.Completable;
import rx.Observable;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Repository for line items in a wishlist.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class WishlistLineItemIdentifierRepositoryImpl<I extends WishlistIdentifier, LI extends WishlistLineItemIdentifier>
		implements LinksRepository<WishlistIdentifier, WishlistLineItemIdentifier> {

	private WishlistRepository wishlistRepository;

	@Override
	public Observable<WishlistLineItemIdentifier> getElements(final WishlistIdentifier wishlistIdentifier) {
		String wishlistId = wishlistIdentifier.getWishlistId().getValue().toString();
		String scope = wishlistIdentifier.getWishlists().getScope().getValue().toString();

		return wishlistRepository.getWishlist(wishlistId)
				.flatMapObservable(wishList -> Observable.from(wishList.getAllItems()))
				.map(shoppingItem -> wishlistRepository.getWishlistLineItemIdentifier(scope, wishlistId, shoppingItem.getGuid()));
	}

	@Override
	public Completable deleteAll(final WishlistIdentifier wishlistIdentifier) {
		String wishlistId = wishlistIdentifier.getWishlistId().getValue().toString();
		return wishlistRepository.removeAllItemsFromWishlist(wishlistId);
	}

	@Reference
	public void setWishlistRepository(final WishlistRepository wishlistRepository) {
		this.wishlistRepository = wishlistRepository;
	}

}

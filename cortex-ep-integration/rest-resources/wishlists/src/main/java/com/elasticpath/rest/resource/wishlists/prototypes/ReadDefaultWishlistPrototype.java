/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.prototypes;

import javax.inject.Inject;

import rx.Single;

import com.elasticpath.rest.definition.wishlists.DefaultWishlistResource;
import com.elasticpath.rest.definition.wishlists.WishlistIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.WishlistRepository;

/**
 * Read prototype for the default wishlist.
 */
public class ReadDefaultWishlistPrototype implements DefaultWishlistResource.Read {

    private final IdentifierPart<String> scope;

    private final WishlistRepository wishlistRepository;

    /**
     * Constructor.
     *
     * @param scope scope
     * @param wishlistRepository the wishlist repository
     */
    @Inject
    public ReadDefaultWishlistPrototype(@UriPart(WishlistsIdentifier.SCOPE) final IdentifierPart<String> scope,
                                        @ResourceRepository final WishlistRepository wishlistRepository) {
        this.scope = scope;
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public Single<WishlistIdentifier> onRead() {
        return wishlistRepository.getDefaultWishlistId(scope.getValue())
                .flatMap(this::buildWishlistIdentifier);
    }

    private Single<WishlistIdentifier> buildWishlistIdentifier(final String wishlistId) {
        return Single.just(WishlistIdentifier.builder()
                .withWishlists(WishlistsIdentifier.builder()
                        .withScope(StringIdentifier.of(scope.getValue()))
                        .build())
                .withWishlistId(StringIdentifier.of(wishlistId))
                .build());
    }
}

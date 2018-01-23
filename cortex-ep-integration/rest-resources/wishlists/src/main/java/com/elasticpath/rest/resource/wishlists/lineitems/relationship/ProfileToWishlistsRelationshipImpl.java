/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.lineitems.relationship;

import javax.inject.Inject;

import rx.Observable;

import com.elasticpath.rest.definition.wishlists.ProfileToWishlistsRelationship;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.helix.data.annotation.UserScopes;
import com.elasticpath.rest.id.type.StringIdentifier;

/**
 * Profile to wishlists link.
 */
public class ProfileToWishlistsRelationshipImpl implements ProfileToWishlistsRelationship.LinkTo {

	@Inject
	@UserScopes
	private Iterable<String> scopes;

	@Override
	public Observable<WishlistsIdentifier> onLinkTo() {
		return Observable.from(scopes)
				.map(StringIdentifier::of)
				.map(scopeId -> WishlistsIdentifier.builder().withScope(scopeId).build())
				.first();

	}
}

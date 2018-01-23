/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.prototypes;

import javax.inject.Inject;

import rx.Single;

import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.MoveToWishlistFormIdentifier;
import com.elasticpath.rest.definition.wishlists.MoveToWishlistFormResource;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.resource.integration.epcommerce.repository.wishlist.MoveToWishlistService;

/**
 * Submit move to wishlist form.
 */
public class SubmitMoveToWishlistFormPrototype implements MoveToWishlistFormResource.Submit {

	private final LineItemIdentifier lineItemIdentifier;

	private final MoveToWishlistService moveToWishlistService;

	/**
	 * Constructor.
	 *
	 * @param moveToWishlistFormIdentifier lineItemIdentifier
	 * @param moveToWishlistService        moveToWishlistService
	 */
	@Inject
	public SubmitMoveToWishlistFormPrototype(@RequestIdentifier final MoveToWishlistFormIdentifier moveToWishlistFormIdentifier,
											 @ResourceService final MoveToWishlistService moveToWishlistService) {
		this.lineItemIdentifier = moveToWishlistFormIdentifier.getLineItem();
		this.moveToWishlistService = moveToWishlistService;
	}

	@Override
	public Single<WishlistLineItemIdentifier> onSubmit() {
		return moveToWishlistService.move(lineItemIdentifier);
	}
}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import java.util.Collection;
import java.util.HashSet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.promotions.CartPromotionsLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Strategy to add promotion link to a cart.
 */
@Singleton
@Named("linkPromotionsToCartStrategy")
public final class LinkPromotionsToCartStrategy implements ResourceStateLinkHandler<CartEntity> {

	private final PromotionsLinkCreator promotionsLinkCreator;
	private final CartPromotionsLookup cartPromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param promotionsLinkCreator the promotions link creator.
	 * @param cartPromotionsLookup the line item promotions lookup.
	 */
	@Inject
	LinkPromotionsToCartStrategy(
			@Named("promotionsLinkCreator")
			final PromotionsLinkCreator promotionsLinkCreator,
			@Named("cartPromotionsLookup")
			final CartPromotionsLookup cartPromotionsLookup) {
		this.promotionsLinkCreator = promotionsLinkCreator;
		this.cartPromotionsLookup = cartPromotionsLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<CartEntity> cartRepresentation) {
		Collection<ResourceLink> linksToAdd = new HashSet<>();

		String cartUri = cartRepresentation.getSelf().getUri();

		linksToAdd.addAll(promotionsLinkCreator.buildAppliedPromotionsLink(cartUri));

		ExecutionResult<Boolean> hasPossiblePromotions;
		try {
			hasPossiblePromotions = cartPromotionsLookup.cartHasPossiblePromotions(cartRepresentation);
		} catch (BrokenChainException bce) {
			hasPossiblePromotions = bce.getBrokenResult();
		}
		linksToAdd.addAll(promotionsLinkCreator.buildPossiblePromotionsLink(cartUri, hasPossiblePromotions));

		return linksToAdd;
	}
}

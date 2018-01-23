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
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.promotions.ItemPromotionsLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Strategy to add promotion link to a item.
 */
@Singleton
@Named("linkPromotionsToItemStrategy")
public final class LinkPromotionsToItemStrategy implements ResourceStateLinkHandler<ItemEntity> {

	private final PromotionsLinkCreator promotionsLinkCreator;
	private final ItemPromotionsLookup itemPromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param promotionsLinkCreator the promotions link creator.
	 * @param itemPromotionsLookup the item promotions lookup.
	 */
	@Inject
	LinkPromotionsToItemStrategy(
			@Named("promotionsLinkCreator")
			final PromotionsLinkCreator promotionsLinkCreator,
			@Named("itemPromotionsLookup")
			final ItemPromotionsLookup itemPromotionsLookup) {
		this.promotionsLinkCreator = promotionsLinkCreator;
		this.itemPromotionsLookup = itemPromotionsLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemEntity> itemRepresentation) {
		Collection<ResourceLink> linksToAdd = new HashSet<>();

		String itemUri = itemRepresentation.getSelf().getUri();

		linksToAdd.addAll(promotionsLinkCreator.buildAppliedPromotionsLink(itemUri));

		ExecutionResult<Boolean> hasPossiblePromotions;
		try {
			hasPossiblePromotions = itemPromotionsLookup.itemHasPossiblePromotions(itemRepresentation);
		} catch (BrokenChainException bce) {
			hasPossiblePromotions = bce.getBrokenResult();
		}
		linksToAdd.addAll(promotionsLinkCreator.buildPossiblePromotionsLink(itemUri, hasPossiblePromotions));

		return linksToAdd;
	}
}

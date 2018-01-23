/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.promotions.ItemPromotionsLookup;
import com.elasticpath.rest.resource.promotions.integration.AppliedItemPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.PossibleItemPromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Lookup class for item promotions.
 */
@Singleton
@Named("itemPromotionsLookup")
public final class ItemPromotionsLookupImpl implements ItemPromotionsLookup {

	private final AppliedItemPromotionsLookupStrategy appliedItemPromotionsLookupStrategy;
	private final PossibleItemPromotionsLookupStrategy possibleItemPromotionsLookupStrategy;
	private final TransformRfoToResourceState<LinksEntity, Collection<String>, ItemEntity> appliedPromotionsTransformer;
	private final TransformRfoToResourceState<LinksEntity, Collection<String>, ItemEntity> possiblePromotionsTransformer;

	/**
	 * Constructor.
	 *
	 * @param appliedItemPromotionsLookupStrategy  the applied item promotions lookup strategy.
	 * @param possibleItemPromotionsLookupStrategy the possible item promotions lookup strategy.
	 * @param appliedPromotionsTransformer         the promotions group transformer.
	 * @param possiblePromotionsTransformer        the possible promotions transformer.
	 */
	@Inject
	ItemPromotionsLookupImpl(
			@Named("appliedItemPromotionsLookupStrategy")
			final AppliedItemPromotionsLookupStrategy appliedItemPromotionsLookupStrategy,
			@Named("possibleItemPromotionsLookupStrategy")
			final PossibleItemPromotionsLookupStrategy possibleItemPromotionsLookupStrategy,
			@Named("appliedPromotionsTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>, ItemEntity> appliedPromotionsTransformer,
			@Named("possiblePromotionsTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>, ItemEntity> possiblePromotionsTransformer) {

		this.appliedItemPromotionsLookupStrategy = appliedItemPromotionsLookupStrategy;
		this.possibleItemPromotionsLookupStrategy = possibleItemPromotionsLookupStrategy;
		this.appliedPromotionsTransformer = appliedPromotionsTransformer;
		this.possiblePromotionsTransformer = possiblePromotionsTransformer;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForItem(final ResourceState<ItemEntity> itemRepresentation) {

		String itemId = itemRepresentation.getEntity().getItemId();
		String scope = itemRepresentation.getScope();

		Collection<String> promotionIds = Assign.ifSuccessful(appliedItemPromotionsLookupStrategy
				.getAppliedPromotionsForItem(scope, itemId));

		ResourceState<LinksEntity> linksRepresentation = appliedPromotionsTransformer.transform(promotionIds,
				(ResourceState) itemRepresentation);

		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ExecutionResult<ResourceState<LinksEntity>> getPossiblePromotionsForItem(final ResourceState<ItemEntity> itemRepresentation) {

		String itemId = itemRepresentation.getEntity().getItemId();
		String scope = itemRepresentation.getScope();

		Collection<String> promotionIds = Assign.ifSuccessful(possibleItemPromotionsLookupStrategy
				.getPossiblePromotionsForItem(scope, itemId));

		ResourceState<LinksEntity> linksRepresentation = possiblePromotionsTransformer.transform(promotionIds,
				(ResourceState) itemRepresentation);

		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}

	@Override
	public ExecutionResult<Boolean> itemHasPossiblePromotions(final ResourceState<ItemEntity> itemRepresentation) {

		String itemId = itemRepresentation.getEntity().getItemId();
		String scope = itemRepresentation.getScope();

		Boolean hasPromotions = Assign.ifSuccessful(possibleItemPromotionsLookupStrategy
				.itemHasPossiblePromotions(scope, itemId));

		return ExecutionResultFactory.createReadOK(hasPromotions);
	}
}

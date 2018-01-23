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
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.CartPromotionsLookup;
import com.elasticpath.rest.resource.promotions.integration.AppliedCartPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.PossibleCartPromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Lookup class for cart promotions.
 */
@Singleton
@Named("cartPromotionsLookup")
public final class CartPromotionsLookupImpl implements CartPromotionsLookup {

	private final AppliedCartPromotionsLookupStrategy appliedCartPromotionsLookupStrategy;
	private final PossibleCartPromotionsLookupStrategy possibleCartPromotionsLookupStrategy;
	private final TransformRfoToResourceState<LinksEntity, Collection<String>, ? extends ResourceEntity> appliedPromotionsTransformer;
	private final TransformRfoToResourceState<LinksEntity, Collection<String>, CartEntity> possiblePromotionsTransformer;

	/**
	 * Constructor.
	 *
	 * @param appliedCartPromotionsLookupStrategy  the applied cart promotions lookup strategy.
	 * @param possibleCartPromotionsLookupStrategy the possible cart promotions lookup strategy.
	 * @param appliedPromotionsTransformer         the promotions group transformer.
	 * @param possiblePromotionsTransformer        the possible promotions transformer.
	 */
	@Inject
	CartPromotionsLookupImpl(
			@Named("appliedCartPromotionsLookupStrategy")
			final AppliedCartPromotionsLookupStrategy appliedCartPromotionsLookupStrategy,
			@Named("possibleCartPromotionsLookupStrategy")
			final PossibleCartPromotionsLookupStrategy possibleCartPromotionsLookupStrategy,
			@Named("appliedPromotionsTransformer")
			final TransformRfoToResourceState<LinksEntity,	Collection<String>,	? extends ResourceEntity> appliedPromotionsTransformer,
			@Named("possiblePromotionsTransformer")
			final TransformRfoToResourceState<LinksEntity,	Collection<String>,	CartEntity> possiblePromotionsTransformer) {

		this.appliedCartPromotionsLookupStrategy = appliedCartPromotionsLookupStrategy;
		this.possibleCartPromotionsLookupStrategy = possibleCartPromotionsLookupStrategy;
		this.appliedPromotionsTransformer = appliedPromotionsTransformer;
		this.possiblePromotionsTransformer = possiblePromotionsTransformer;
	}

	@Override
	@SuppressWarnings("unchecked") // Java 7's generic type inference is rubbish
	public ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForItemInCart(
			final ResourceState<LineItemEntity> cartLineItemRepresentation) {

		LineItemEntity lineItemEntity = cartLineItemRepresentation.getEntity();
		String decodedCartId = Base32Util.decode(lineItemEntity.getCartId());
		String decodedLineItemId = Base32Util.decode(lineItemEntity.getLineItemId());
		String scope = cartLineItemRepresentation.getScope();

		Collection<String> promotionIds = Assign.ifSuccessful(appliedCartPromotionsLookupStrategy
						.getAppliedPromotionsForItemInCart(
								scope,
								decodedCartId,
								decodedLineItemId,
								lineItemEntity.getQuantity())
		);

		ResourceState<LinksEntity> linksRepresentation =
				appliedPromotionsTransformer.transform(promotionIds, (ResourceState) cartLineItemRepresentation);

		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}

	@Override
	@SuppressWarnings("unchecked") // Java 7's generic type inference is rubbish
	public ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForCart(final ResourceState<CartEntity> cartRepresentation) {
		CartEntity cartEntity = cartRepresentation.getEntity();
		String decodedCartId = Base32Util.decode(cartEntity.getCartId());
		String scope = cartRepresentation.getScope();

		Collection<String> promotionIds = Assign.ifSuccessful(appliedCartPromotionsLookupStrategy
				.getAppliedPromotionsForCart(scope, decodedCartId));

		ResourceState<LinksEntity> linksRepresentation = appliedPromotionsTransformer.transform(promotionIds,
				(ResourceState) cartRepresentation);

		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getPossiblePromotionsForCart(final ResourceState<CartEntity> cartRepresentation) {
		CartEntity cartEntity = cartRepresentation.getEntity();
		String decodedCartId = Base32Util.decode(cartEntity.getCartId());
		String scope = cartRepresentation.getScope();

		Collection<String> promotionIds = Assign.ifSuccessful(possibleCartPromotionsLookupStrategy
				.getPossiblePromotionsForCart(scope, decodedCartId));

		ResourceState<LinksEntity> linksRepresentation = possiblePromotionsTransformer.transform(promotionIds,
				cartRepresentation);

		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}

	@Override
	public ExecutionResult<Boolean> cartHasPossiblePromotions(final ResourceState<CartEntity> cartRepresentation) {

		String decodedCartId = Base32Util.decode(cartRepresentation.getEntity().getCartId());
		String scope = cartRepresentation.getScope();

		Boolean hasPromotions = Assign.ifSuccessful(possibleCartPromotionsLookupStrategy
				.cartHasPossiblePromotions(scope, decodedCartId));

		return ExecutionResultFactory.createReadOK(hasPromotions);
	}
}

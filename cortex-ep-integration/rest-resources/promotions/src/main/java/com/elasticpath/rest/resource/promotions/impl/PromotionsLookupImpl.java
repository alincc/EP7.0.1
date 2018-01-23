/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.PromotionsLookup;
import com.elasticpath.rest.resource.promotions.integration.PromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Lookup service for promotions data.
 */
@Singleton
@Named("promotionsLookup")
public final class PromotionsLookupImpl implements PromotionsLookup {

	private final PromotionsLookupStrategy promotionsLookupStrategy;
	private final TransformToResourceState<PromotionEntity, PromotionEntity> promotionDetailsTransformer;
	private final TransformRfoToResourceState<PromotionEntity, PromotionEntity, ?> readFromOtherPromotionDetailsTransformer;

	/**
	 * Constructor.
	 *
	 * @param promotionsLookupStrategy                 the promotions lookup strategy
	 * @param promotionDetailsTransformer              the promotion details transformer
	 * @param readFromOtherPromotionDetailsTransformer the read from other promotion details transformer.
	 */
	@Inject
	PromotionsLookupImpl(
			@Named("promotionsLookupStrategy")
			final PromotionsLookupStrategy promotionsLookupStrategy,
			@Named("promotionDetailsTransformer")
			final TransformToResourceState<PromotionEntity, PromotionEntity> promotionDetailsTransformer,
			@Named("readFromOtherPromotionDetailsTransformer")
			final TransformRfoToResourceState<PromotionEntity, PromotionEntity, ?> readFromOtherPromotionDetailsTransformer) {
		this.promotionsLookupStrategy = promotionsLookupStrategy;
		this.promotionDetailsTransformer = promotionDetailsTransformer;
		this.readFromOtherPromotionDetailsTransformer = readFromOtherPromotionDetailsTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<PromotionEntity>> getPromotionDetails(final String scope, final String promotionId) {

		String decodedPromotionId = Base32Util.decode(promotionId);
		PromotionEntity promotionEntity = Assign.ifSuccessful(promotionsLookupStrategy.getPromotionById(scope, decodedPromotionId));
		ResourceState<PromotionEntity> promotionRepresentation = promotionDetailsTransformer.transform(scope,
				promotionEntity);

		return ExecutionResultFactory.createReadOK(promotionRepresentation);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ExecutionResult<ResourceState<PromotionEntity>> getPurchasePromotionDetails(final String scope, final String promotionId,
																					final String decodedPurchaseId,
																					final ResourceState representation) {
		String decodedPromotionId = Base32Util.decode(promotionId);
		PromotionEntity promotionEntity = Assign.ifSuccessful(
				promotionsLookupStrategy.getPromotionForPurchase(scope, decodedPromotionId, decodedPurchaseId));
		ResourceState<PromotionEntity> promotionRepresentation =
				readFromOtherPromotionDetailsTransformer.transform(promotionEntity, representation);

		return ExecutionResultFactory.createReadOK(promotionRepresentation);
	}
}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Transform a {@link PromotionEntity} into a {@link ResourceState}<{@link PromotionEntity}> using a
 * {@link ResourceState}.
 */
@Singleton
@Named("readFromOtherPromotionDetailsTransformer")
public final class ReadFromOtherPromotionDetailsTransformer
		implements TransformRfoToResourceState<PromotionEntity, PromotionEntity, ResourceEntity> {

	private final PromotionsUriBuilderFactory promotionsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param promotionsUriBuilderFactory the promotions URI builder factory.
	 */
	@Inject
	ReadFromOtherPromotionDetailsTransformer(
			@Named("promotionsUriBuilderFactory")
			final PromotionsUriBuilderFactory promotionsUriBuilderFactory) {
		this.promotionsUriBuilderFactory = promotionsUriBuilderFactory;
	}

	private PromotionEntity updateEntityWithEncodedId(final PromotionEntity promotionEntity, final String encodedPromotionId) {
		return PromotionEntity.builderFrom(promotionEntity)
				.withPromotionId(encodedPromotionId)
				.build();
	}


	@Override
	public ResourceState<PromotionEntity> transform(final PromotionEntity promotionEntity,
			final ResourceState<ResourceEntity> otherRepresentation) {
		String encodedPromotionId = Base32Util.encode(promotionEntity.getPromotionId());
		String otherUri = otherRepresentation.getSelf().getUri();
		String promotionUri = promotionsUriBuilderFactory.get()
				.setSourceUri(otherUri)
				.setPromotionId(encodedPromotionId)
				.build();

		Self self = SelfFactory.createSelf(promotionUri);

		PromotionEntity representationReadyEntity = updateEntityWithEncodedId(promotionEntity, encodedPromotionId);

		return ResourceState.Builder.create(representationReadyEntity)
				.withSelf(self)
				.build();
	}
}

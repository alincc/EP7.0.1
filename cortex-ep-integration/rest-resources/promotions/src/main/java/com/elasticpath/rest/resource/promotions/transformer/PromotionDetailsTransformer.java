/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Transform a {@link PromotionEntity} into a {@link ResourceState}<{@link PromotionEntity}>.
 */
@Singleton
@Named("promotionDetailsTransformer")
public final class PromotionDetailsTransformer implements TransformToResourceState<PromotionEntity, PromotionEntity> {

	private final PromotionsUriBuilderFactory promotionsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param promotionsUriBuilderFactory the promotions URI builder factory.
	 */
	@Inject
	PromotionDetailsTransformer(
			@Named("promotionsUriBuilderFactory")
			final PromotionsUriBuilderFactory promotionsUriBuilderFactory) {
		this.promotionsUriBuilderFactory = promotionsUriBuilderFactory;
	}

	@Override
	public ResourceState<PromotionEntity> transform(final String scope, final PromotionEntity promotionEntity) {
		String encodedPromotionId = Base32Util.encode(promotionEntity.getPromotionId());

		Self self = buildSelf(scope, encodedPromotionId);

		PromotionEntity representationReadyEntity = updateEntityWithEncodedId(promotionEntity, encodedPromotionId);

		return ResourceState.Builder.create(representationReadyEntity)
				.withSelf(self)
				.build();
	}

	private PromotionEntity updateEntityWithEncodedId(final PromotionEntity promotionEntity, final String encodedPromotionId) {
		return PromotionEntity.builderFrom(promotionEntity)
				.withPromotionId(encodedPromotionId)
				.build();
	}

	private Self buildSelf(final String scope, final String encodedPromotionId) {
		String selfUri = promotionsUriBuilderFactory.get()
				.setScope(scope)
				.setPromotionId(encodedPromotionId)
				.build();
		return SelfFactory.createSelf(selfUri);
	}
}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.promotions.PromotionsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Transform collection of applied promotion IDs to a {@link ResourceState}<{@link LinksEntity}>.
 */
@Singleton
@Named("purchaseAppliedPromotionsTransformer")
public final class PurchaseAppliedPromotionsTransformer implements TransformRfoToResourceState<LinksEntity, Collection<String>, ResourceEntity> {

	private final PromotionsUriBuilderFactory promotionsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param promotionsUriBuilderFactory the promotions URI builder factory.
	 */
	@Inject
	PurchaseAppliedPromotionsTransformer(
			@Named("promotionsUriBuilderFactory")
			final PromotionsUriBuilderFactory promotionsUriBuilderFactory) {
		this.promotionsUriBuilderFactory = promotionsUriBuilderFactory;
	}

	@Override
	public ResourceState<LinksEntity> transform(
			final Collection<String> decodedPromotionIds,
			final ResourceState<ResourceEntity> otherRepresentation) {

		String sourceUri = otherRepresentation.getSelf().getUri();
		String selfUri = promotionsUriBuilderFactory.get()
				.setSourceUri(sourceUri)
				.setPromotionType(AppliedPromotions.URI_PART)
				.build();

		Self self = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> links = new ArrayList<>(decodedPromotionIds.size());
		for (String decodedPromotionId : decodedPromotionIds) {
			ResourceLink element = buildPromotionLink(decodedPromotionId, sourceUri);
			links.add(element);
		}

		LinksEntity linksEntity = LinksEntity.builder()
				.build();

		return ResourceState.Builder.create(linksEntity)
				.withSelf(self)
				.withLinks(links)
				.build();
	}


	private ResourceLink buildPromotionLink(final String decodedPromotionId, final String sourceUri) {
		String promotionId = Base32Util.encode(decodedPromotionId);
		String promotionUri = promotionsUriBuilderFactory.get()
				.setSourceUri(sourceUri)
				.setPromotionId(promotionId)
				.build();
		return ElementListFactory.createElement(promotionUri, PromotionsMediaTypes.PROMOTION.id());
	}
}

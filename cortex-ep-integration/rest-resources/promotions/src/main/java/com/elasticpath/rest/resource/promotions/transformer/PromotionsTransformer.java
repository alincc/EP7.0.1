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
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Transform collection of promotion IDs to a {@link ResourceState}<{@link LinksEntity}>.
 */
@Singleton
@Named("promotionsTransformer")
public final class PromotionsTransformer implements TransformToResourceState<LinksEntity, Collection<String>> {

	private final PromotionsUriBuilderFactory promotionsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param promotionsUriBuilderFactory the promotions URI builder factory.
	 */
	@Inject
	PromotionsTransformer(
			@Named("promotionsUriBuilderFactory")
			final PromotionsUriBuilderFactory promotionsUriBuilderFactory) {
		this.promotionsUriBuilderFactory = promotionsUriBuilderFactory;
	}


	@Override
	public ResourceState<LinksEntity> transform(final String scope, final Collection<String> decodedPromotionIds) {
		String selfUri = promotionsUriBuilderFactory.get().setScope(scope).build();
		return buildLinksResource(scope, decodedPromotionIds, selfUri);
	}

	/**
	 * Build the LinksRepresentation for Promotions using the provided selfUri and collection Ids, and scope.
	 * @param scope the scope
	 * @param decodedPromotionIds the collection of promotion IDs
	 * @param selfUri the self URI
	 * @return the promotions links representation
	 */
	ResourceState<LinksEntity> buildLinksResource(
			final String scope,
			final Collection<String> decodedPromotionIds,
			final String selfUri) {

		Self self = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> links = new ArrayList<>(decodedPromotionIds.size());
		for (String decodedPromotionId : decodedPromotionIds) {
			ResourceLink element = buildPromotionLink(scope, decodedPromotionId);
			links.add(element);
		}
		LinksEntity linksEntity = LinksEntity.builder()
				.build();

		return ResourceState.Builder.create(linksEntity)
				.withSelf(self)
				.withLinks(links)
				.build();
	}

	private ResourceLink buildPromotionLink(final String scope, final String decodedPromotionId) {
		String promotionId = Base32Util.encode(decodedPromotionId);
		String promotionUri = promotionsUriBuilderFactory.get()
				.setScope(scope)
				.setPromotionId(promotionId)
				.build();
		return ElementListFactory.createElement(promotionUri, PromotionsMediaTypes.PROMOTION.id());
	}
}

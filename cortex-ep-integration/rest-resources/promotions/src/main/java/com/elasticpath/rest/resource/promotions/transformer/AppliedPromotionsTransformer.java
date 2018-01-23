/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.transformer;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Transform collection of applied promotion IDs to a {@link ResourceState}<{@link LinksEntity}>.
 */
@Singleton
@Named("appliedPromotionsTransformer")
public final class AppliedPromotionsTransformer implements
		TransformRfoToResourceState<LinksEntity, Collection<String>, ResourceEntity> {

	private final PromotionsUriBuilderFactory promotionsUriBuilderFactory;
	private final PromotionsTransformer promotionsTransformer;

	/**
	 * Constructor.
	 *
	 * @param promotionsUriBuilderFactory the promotions URI builder factory.
	 * @param promotionsTransformer the promotions transformer.
	 */
	@Inject
	AppliedPromotionsTransformer(
			@Named("promotionsUriBuilderFactory")
			final PromotionsUriBuilderFactory promotionsUriBuilderFactory,
			@Named("promotionsTransformer")
			final PromotionsTransformer promotionsTransformer) {
		this.promotionsUriBuilderFactory = promotionsUriBuilderFactory;
		this.promotionsTransformer = promotionsTransformer;
	}

	@Override
	public ResourceState<LinksEntity> transform(
			final Collection<String> decodedPromotionIds,
			final ResourceState<ResourceEntity> otherRepresentation) {

		String sourceUri = otherRepresentation.getSelf().getUri();
		String scope = otherRepresentation.getScope();
		String selfUri = promotionsUriBuilderFactory.get()
				.setSourceUri(sourceUri)
				.setPromotionType(AppliedPromotions.URI_PART)
				.build();
		return promotionsTransformer.buildLinksResource(scope, decodedPromotionIds, selfUri);
	}
}

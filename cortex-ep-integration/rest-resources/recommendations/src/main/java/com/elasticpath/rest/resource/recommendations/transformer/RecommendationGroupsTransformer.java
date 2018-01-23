/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.transformer;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.recommendations.rel.RecommendationsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory;
import com.elasticpath.rest.util.rel.RelNameUtil;

/**
 * The transformer for converting a list of recommendations.
 */
@Singleton
@Named("recommendationGroupsTransformer")
public final class RecommendationGroupsTransformer implements TransformToResourceState<LinksEntity, Collection<RecommendationsEntity>> {

	private final RecommendationsUriBuilderFactory recommendationsUriBuilderFactory;

	/**
	 * Default Constructor.
	 *
	 * @param recommendationsUriBuilderFactory the recommendations URI builder factory
	 */
	@Inject
	public RecommendationGroupsTransformer(
			@Named("recommendationsUriBuilderFactory")
			final RecommendationsUriBuilderFactory recommendationsUriBuilderFactory) {

		this.recommendationsUriBuilderFactory = recommendationsUriBuilderFactory;
	}


	@Override
	public ResourceState<LinksEntity> transform(final String scope, final Collection<RecommendationsEntity> recommendationEntities) {

		String selfUri = recommendationsUriBuilderFactory.get().setScope(scope).build();
		Self self = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> resourceLinks = createRecommendationLinks(scope, recommendationEntities);
		return ResourceState.Builder.create(ResourceTypeFactory.createResourceEntity(LinksEntity.class))
				.withLinks(resourceLinks)
				.withResourceInfo(ResourceInfo.builder()
					.withMaxAge(RecommendationsResourceRels.MAX_AGE)
					.build())
				.withSelf(self)
				.build();
	}

	private Collection<ResourceLink> createRecommendationLinks(final String scope, final Collection<RecommendationsEntity> entities) {
		Collection<ResourceLink> links = new ArrayList<>(entities.size());
		for (RecommendationsEntity recommendationEntity : entities) {
			String recommendationGroup = recommendationEntity.getName();
			if (!RelNameUtil.isValidRel(recommendationGroup)) {
				throw new IllegalStateException("Invalid recommendation rel name: " + recommendationGroup);
			}
			String recommendationsItemsUri = recommendationsUriBuilderFactory.get()
					.setScope(scope)
					.setRecommendationGroup(recommendationGroup)
					.build();

			ResourceLink recommendationElement = ResourceLinkFactory.createNoRev(
					recommendationsItemsUri, CollectionsMediaTypes.PAGINATED_LINKS.id(), recommendationGroup);

			links.add(recommendationElement);
		}
		return links;
	}
}

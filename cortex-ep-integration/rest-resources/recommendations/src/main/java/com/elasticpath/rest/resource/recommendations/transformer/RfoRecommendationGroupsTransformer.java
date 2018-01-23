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
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.util.rel.RelNameUtil;

/**
 * The transformer for converting a list of recommendations for an other resource.
 */
@Singleton
@Named("rfoRecommendationGroupsTransformer")
public final class RfoRecommendationGroupsTransformer implements
		TransformRfoToResourceState<LinksEntity, Collection<RecommendationsEntity>, ResourceEntity> {

	private final RecommendationsUriBuilderFactory recommendationsUriBuilderFactory;

	/**
	 * Default Constructor.
	 *
	 * @param recommendationsUriBuilderFactory recommendations uri builder factory.
	 */
	@Inject
	public RfoRecommendationGroupsTransformer(
			@Named("recommendationsUriBuilderFactory")
			final RecommendationsUriBuilderFactory recommendationsUriBuilderFactory) {

		this.recommendationsUriBuilderFactory = recommendationsUriBuilderFactory;
	}

	@Override
	public ResourceState<LinksEntity> transform(final Collection<RecommendationsEntity> recommendationsEntities,
			final ResourceState<ResourceEntity> otherRepresentation) {

		String otherUri = ResourceStateUtil.getSelfUri(otherRepresentation);
		String selfUri = recommendationsUriBuilderFactory.get().setSourceUri(otherUri).build();
		Self self = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> resourceLinks = buildRecommendationGroupLinks(recommendationsEntities, otherUri);

		return ResourceState.Builder.create(ResourceTypeFactory.createResourceEntity(LinksEntity.class))
				.withLinks(resourceLinks)
				.withSelf(self)
				.withResourceInfo(ResourceInfo.builder()
						.withMaxAge(RecommendationsResourceRels.MAX_AGE)
						.build())
				.build();
	}

	private Collection<ResourceLink> buildRecommendationGroupLinks(final Collection<RecommendationsEntity> recommendationsEntities,
																final String otherUri) {

		Collection<ResourceLink> resourceLinks = new ArrayList<>(recommendationsEntities.size());
		for (RecommendationsEntity recommendationEntity : recommendationsEntities) {
			String recommendationGroup = recommendationEntity.getName();
			if (!RelNameUtil.isValidRel(recommendationGroup)) {
				throw new IllegalStateException("Invalid recommendation rel name: " + recommendationGroup);
			}
			String recommendationsItemsUri = recommendationsUriBuilderFactory.get()
					.setSourceUri(otherUri)
					.setRecommendationGroup(recommendationGroup)
					.build();

			ResourceLink recommendationElement = ResourceLinkFactory.createNoRev(
					recommendationsItemsUri, CollectionsMediaTypes.PAGINATED_LINKS.id(), recommendationGroup);

			resourceLinks.add(recommendationElement);
		}
		return resourceLinks;
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.link.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.recommendations.NavigationRecommendationsLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Class to add recommendation links to a navigation.
 */
@Singleton
@Named("linkNavigationStrategy")
public class LinkRecommendationsToNavigationStrategy implements ResourceStateLinkHandler<NavigationEntity> {

	private final String resourceServerName;
	private final NavigationRecommendationsLookup navigationRecommendationsLookup;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param navigationRecommendationsLookup the recommendations lookup
	 */
	@Inject
	LinkRecommendationsToNavigationStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("navigationRecommendationsLookup")
			final NavigationRecommendationsLookup navigationRecommendationsLookup) {
		this.resourceServerName = resourceServerName;
		this.navigationRecommendationsLookup = navigationRecommendationsLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<NavigationEntity> resourceState) {
		ExecutionResult<Boolean> hasRecommendations = navigationRecommendationsLookup.hasRecommendations(
				resourceState.getEntity(), resourceState.getScope());

		return RecommendationsLinkCreationHelper.buildRecommendationsLink(resourceState.getSelf(), hasRecommendations, resourceServerName);
	}
}

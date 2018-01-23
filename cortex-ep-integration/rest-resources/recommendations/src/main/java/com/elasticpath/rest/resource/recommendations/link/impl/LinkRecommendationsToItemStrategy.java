/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.link.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Class to add recommendation links to an item.
 */
@Singleton
@Named("linkItemStrategy")
public final class LinkRecommendationsToItemStrategy implements ResourceStateLinkHandler<ItemEntity> {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	LinkRecommendationsToItemStrategy(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemEntity> resourceState) {
		return RecommendationsLinkCreationHelper.buildRecommendationsLink(resourceState.getSelf(), ExecutionResultFactory.createReadOK(true),
				resourceServerName);
	}
}

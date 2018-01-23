/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.navigations.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.navigations.Navigations;
import com.elasticpath.rest.resource.searches.rel.SearchesResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Create a link to searches/navigations on the navigation representation.
 */
@Singleton
@Named("addSearchNavigationsLinkToNavigationStrategy")
public final class AddSearchNavigationsLinkToNavigationStrategy implements ResourceStateLinkHandler<NavigationEntity> {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName The resource server name.
	 */
	@Inject
	AddSearchNavigationsLinkToNavigationStrategy(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<NavigationEntity> navigation) {
		String nodeId = navigation.getEntity().getNodeId();
		String scope = navigation.getScope();
		String itemListUri = URIUtil.format(resourceServerName, scope, Navigations.URI_PART, Items.URI_PART, nodeId);

		ResourceLink link = ResourceLinkFactory.createNoRev(
				itemListUri,
				CollectionsMediaTypes.PAGINATED_LINKS.id(),
				SearchesResourceRels.ITEMS_REL);

		return Collections.singleton(link);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.navigations.NavigationLookup;
import com.elasticpath.rest.resource.navigations.constants.NavigationsResourceConstants;
import com.elasticpath.rest.resource.navigations.integration.NavigationLookupStrategy;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;
import com.elasticpath.rest.resource.navigations.rel.NavigationsResourceRels;
import com.elasticpath.rest.resource.navigations.transform.NavigationTransformer;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link NavigationLookup}.
 */
@Singleton
@Named("navigationLookup")
public final class NavigationLookupImpl implements NavigationLookup {

	private final String resourceServerName;
	private final NavigationLookupStrategy navigationLookupStrategy;
	private final NavigationTransformer navigationTransformer;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param navigationLookupStrategy the navigation lookup strategy
	 * @param navigationTransformer the navigation transformer
	 */
	@Inject
	public NavigationLookupImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("navigationLookupStrategy")
			final NavigationLookupStrategy navigationLookupStrategy,
			@Named("navigationTransformer")
			final NavigationTransformer navigationTransformer) {
		this.resourceServerName = resourceServerName;
		this.navigationLookupStrategy = navigationLookupStrategy;
		this.navigationTransformer = navigationTransformer;
	}


	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getRootNavigationNodes(final String scope) {
		Collection<String> navigationNodes = Assign.ifSuccessful(navigationLookupStrategy.findRootNodeIds(scope));
		Collection<String> encodedNavigationNodes = Base32Util.encodeAll(navigationNodes);

		String selfUri = URIUtil.format(resourceServerName, scope);
		Self self = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> rootNavigationNodeLinks = createRootNavigationNodeLinks(encodedNavigationNodes, selfUri);

		ResourceState<LinksEntity> resourceState = ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(NavigationsResourceConstants.DEFAULT_MAX_AGE)
						.build())
				.addingLinks(rootNavigationNodeLinks)
				.build();
		return ExecutionResultFactory.createReadOK(resourceState);
	}

	@Override
	public ExecutionResult<ResourceState<NavigationEntity>> getNavigationNode(final String scope, final String navigationId) {

		String decodedNavigationId = Base32Util.decode(navigationId);
		NavigationDto navigationDto = Assign.ifSuccessful(navigationLookupStrategy.find(scope, decodedNavigationId));
		ResourceState<NavigationEntity> navigation = navigationTransformer.transformToRepresentation(scope, navigationDto);

		return ExecutionResultFactory.createReadOK(navigation);
	}


	private Collection<ResourceLink> createRootNavigationNodeLinks(final Collection<String> rootNavigationNodes, final String rootSelfUri) {
		Collection<ResourceLink> rootNavigationNodeLinks = new ArrayList<>(rootNavigationNodes.size());
		for (String rootNavigationNode : rootNavigationNodes) {
			ResourceLink rootNavigationNodeLink = ElementListFactory.createElementWithRev(URIUtil.format(rootSelfUri, rootNavigationNode),
					NavigationsMediaTypes.NAVIGATION.id(), NavigationsResourceRels.TOP_REV);
			rootNavigationNodeLinks.add(rootNavigationNodeLink);
		}
		return rootNavigationNodeLinks;
	}
}

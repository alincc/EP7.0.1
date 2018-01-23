/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.transform;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.definition.navigations.NavigationsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.navigations.constants.NavigationsResourceConstants;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;
import com.elasticpath.rest.resource.navigations.rel.NavigationsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Transform between {@link com.elasticpath.rest.schema.ResourceState} and {@link NavigationDto}.
 */
@Singleton
@Named("navigationTransformer")
public class NavigationTransformer {

	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public NavigationTransformer(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	/**
	 * Transform to representation.
	 *
	 * @param navigationDto the navigation dto
	 * @param scope the scope
	 * @return the navigation representation
	 */
	public ResourceState<NavigationEntity> transformToRepresentation(final String scope, final NavigationDto navigationDto) {
		String navigationId = Base32Util.encode(navigationDto.getNavigationCorrelationId());

		NavigationEntity navigationEntity = NavigationEntity.builder()
				.withNodeId(navigationId)
				.withName(navigationDto.getName())
				.withDisplayName(navigationDto.getDisplayName())
				.withDetails(navigationDto.getAttributes())
				.build();

		Collection<ResourceLink> links = new ArrayList<>();
		String baseUri = URIUtil.format(resourceServerName, scope);
		processParentNavigationNode(navigationDto, links, baseUri);
		processChildNavigationNodes(navigationDto, links, baseUri);
		processTopNavigationNodeLink(links, baseUri);
		Self self = createSelf(navigationDto, baseUri);
		return ResourceState.Builder.create(navigationEntity)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(NavigationsResourceConstants.DEFAULT_MAX_AGE)
						.build())
				.withScope(scope)
				.addingLinks(links)
				.build();
	}

	private Self createSelf(final NavigationDto navigationDto, final String baseUri) {
		String navigationId = Base32Util.encode(navigationDto.getNavigationCorrelationId());
		return SelfFactory.createSelf(URIUtil.format(baseUri, navigationId));
	}

	private void processChildNavigationNodes(final NavigationDto navigationDto, final Collection<ResourceLink> links, final String baseUri) {
		Collection<String> childNavigationCorrelationIds = navigationDto.getChildNavigationCorrelationIds();
		if (CollectionUtil.isNotEmpty(childNavigationCorrelationIds)) {
			for (String childNavigationCorrelationId : childNavigationCorrelationIds) {
				String childNavigationId = Base32Util.encode(childNavigationCorrelationId);
				ResourceLink childNavigationLink = ResourceLinkFactory.createNoRev(URIUtil.format(baseUri, childNavigationId),
						NavigationsMediaTypes.NAVIGATION.id(), NavigationsResourceRels.CHILD_REL);
				links.add(childNavigationLink);
			}
		}
	}

	private void processParentNavigationNode(final NavigationDto navigationDto, final Collection<ResourceLink> links, final String baseUri) {
		if (navigationDto.getParentNavigationCorrelationId() != null) {
			String parentNavigationId = Base32Util.encode(navigationDto.getParentNavigationCorrelationId());
			links.add(ResourceLinkFactory.createNoRev(URIUtil.format(baseUri, parentNavigationId),
					NavigationsMediaTypes.NAVIGATION.id(), NavigationsResourceRels.PARENT_REL));
		}
	}

	private void processTopNavigationNodeLink(final Collection<ResourceLink> links, final String baseUri) {
		links.add(ResourceLinkFactory.createNoRev(baseUri, CollectionsMediaTypes.LINKS.id(),
				NavigationsResourceRels.TOP_REL));
	}
}

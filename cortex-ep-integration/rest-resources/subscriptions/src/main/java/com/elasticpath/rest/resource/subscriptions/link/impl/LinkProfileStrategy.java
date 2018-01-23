/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.subscriptions.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.subscriptions.rel.SubscriptionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates a link on profile to subscriptions.
 */
@Singleton
@Named("linkProfileStrategy")
public final class LinkProfileStrategy implements ResourceStateLinkHandler<ProfileEntity> {

	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name
	 */
	@Inject
	LinkProfileStrategy(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<ProfileEntity> profile) {
		String scope = profile.getScope();

		String subscriptionsUri = URIUtil.format(resourceServerName, scope);
		ResourceLink link = ResourceLinkFactory.createNoRev(subscriptionsUri, CollectionsMediaTypes.LINKS.id(),
				SubscriptionResourceRels.SUBSCRIPTIONS_REL);
		return Collections.singleton(link);
	}
}

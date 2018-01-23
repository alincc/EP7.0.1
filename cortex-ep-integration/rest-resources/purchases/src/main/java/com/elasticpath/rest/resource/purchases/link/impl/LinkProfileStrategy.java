/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates links from an profile to a purchase.
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
	public Collection<ResourceLink> getLinks(final ResourceState<ProfileEntity> representation) {

		String scope = representation.getScope();

		String purchasesUri = URIUtil.format(resourceServerName, scope);
		ResourceLink link = ResourceLinkFactory.createNoRev(purchasesUri, CollectionsMediaTypes.LINKS.id(), PurchaseResourceRels.PURCHASES_REL);
		return Collections.singleton(link);
	}
}

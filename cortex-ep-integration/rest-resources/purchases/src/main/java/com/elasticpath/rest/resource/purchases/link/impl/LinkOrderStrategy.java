/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.purchases.PurchasesResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Creates links from an order to a purchase.
 */
@Singleton
@Named("linkOrderStrategy")
public final class LinkOrderStrategy implements ResourceStateLinkHandler<OrderEntity> {

	private final String resourceServerName;
	private final PurchasesResourceLinkFactory linkFactory;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name
	 * @param linkFactory Purchases Link Factory
	 */
	@Inject
	LinkOrderStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("purchaseLinkFactory")
			final PurchasesResourceLinkFactory linkFactory) {

		this.resourceServerName = resourceServerName;
		this.linkFactory = linkFactory;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<OrderEntity> representation) {
		String orderUri = ResourceStateUtil.getSelfUri(representation);
		return Collections.singleton(linkFactory.createPurchaseFormResourceLink(resourceServerName, orderUri));
	}
}

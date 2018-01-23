/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.link.impl;

import static com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels.PURCHASE_REL;
import static com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels.SHIPMENTS_REL;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Adds shipments link to purchases.
 */
@Singleton
@Named("addShipmentsLinkToPurchase")
public final class AddShipmentsLinkToPurchase implements ResourceStateLinkHandler<PurchaseEntity> {

	private final ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;

	/**
	 * Constructor.
	 * 
	 * @param shipmentsUriBuilderFactory the shipmentsUriBuilderFactory
	 */
	@Inject
	AddShipmentsLinkToPurchase(
			@Named("shipmentsUriBuilderFactory")
			final ShipmentsUriBuilderFactory shipmentsUriBuilderFactory) {

		this.shipmentsUriBuilderFactory = shipmentsUriBuilderFactory;
	}


	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<PurchaseEntity> resourceState) {
		final String purchaseUri = ResourceStateUtil.getSelfUri(resourceState);
		String uri = shipmentsUriBuilderFactory.get().setSourceUri(purchaseUri).build();
		ResourceLink resourceLink = ResourceLinkFactory.create(uri, CollectionsMediaTypes.LINKS.id(), SHIPMENTS_REL, PURCHASE_REL);
		return Collections.singleton(resourceLink);
	}
}

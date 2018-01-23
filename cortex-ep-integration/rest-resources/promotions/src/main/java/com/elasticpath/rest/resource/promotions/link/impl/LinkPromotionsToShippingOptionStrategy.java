/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.link.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Strategy to add promotion link to a shipping option.
 */
@Singleton
@Named("linkPromotionsToShippingOptionStrategy")
public final class LinkPromotionsToShippingOptionStrategy  implements ResourceStateLinkHandler<ShippingOptionEntity> {

	private final PromotionsLinkCreator promotionsLinkCreator;

	private static final String SHIPMENTDETAILS_RESOURCE_PATH_PART = "/shipmentdetails";

	/**
	 * Constructor.
	 *
	 * @param promotionsLinkCreator the promotions link creator.
	 */
	@Inject
	LinkPromotionsToShippingOptionStrategy(
			@Named("promotionsLinkCreator")
			final PromotionsLinkCreator promotionsLinkCreator) {
		this.promotionsLinkCreator = promotionsLinkCreator;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShippingOptionEntity> shippingOptionRepresentation) {
		// ShippingOptionEntity is shared by shipments and ShipmentDetails
		String selfUri = shippingOptionRepresentation.getSelf().getUri();
		if (!(selfUri.contains(SHIPMENTDETAILS_RESOURCE_PATH_PART))) {
			return ImmutableList.of();
		}
		String shippingOptionUri = shippingOptionRepresentation.getSelf().getUri();
		return promotionsLinkCreator.buildAppliedPromotionsLink(shippingOptionUri);
	}
}

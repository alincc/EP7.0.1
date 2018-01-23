/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.link.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.shipmentdetails.ShipmentdetailsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.resource.shipments.shippingoption.rel.ShippingOptionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Creates a link from the Shipment to the ShippingOption.
 */
@Singleton
@Named("addShippingOptionLinkToShipment")
public final class AddShippingOptionLinkToShipment implements ResourceStateLinkHandler<ShipmentEntity> {

	private final ShippingOptionUriBuilderFactory shippingOptionUriBuilderFactory;

	/**
	 * Constructor.
	 * 
	 * @param shippingOptionUriBuilderFactory a {@link com.elasticpath.rest.schema.uri.ShippingOptionUriBuilderFactory}
	 */
	@Inject
	public AddShippingOptionLinkToShipment(
			@Named("shippingOptionUriBuilderFactory")
			final ShippingOptionUriBuilderFactory shippingOptionUriBuilderFactory) {
		this.shippingOptionUriBuilderFactory = shippingOptionUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentEntity> resourceState) {
		String shipmentUri = ResourceStateUtil.getSelfUri(resourceState);
		String shippingOptionUri = shippingOptionUriBuilderFactory.get().setSourceUri(shipmentUri).build();
		ResourceLink link = ResourceLinkFactory.create(shippingOptionUri, ShipmentdetailsMediaTypes.SHIPPING_OPTION.id(),
				ShippingOptionResourceRels.SHIPPING_OPTION_REL, ShipmentsResourceRels.SHIPMENT_REV);
		return Collections.singleton(link);
	}


}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipments.addresses.ShippingAddressLookup;
import com.elasticpath.rest.resource.shipments.addresses.rel.ShippingAddressResourceRels;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShippingAddressUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Links the Shipping Address to the Shipment representation.
 */
@Singleton
@Named("linkShippingAddressToShipmentStrategy")
public final class LinkShippingAddressToShipmentStrategy implements ResourceStateLinkHandler<ShipmentEntity> {

	private final ShippingAddressLookup shippingAddressLookup;

	private final ShippingAddressUriBuilderFactory shippingAddressUriBuilderFactory;

	/**
	 * Constructor.
	 * 
	 * @param shippingAddressLookup the shippingAddressLookup
	 * @param shippingAddressUriBuilderFactory the shippingAddressUriBuilderFactory
	 */
	@Inject
	LinkShippingAddressToShipmentStrategy(
			@Named("shippingAddressLookup")
			final ShippingAddressLookup shippingAddressLookup,
			@Named("shippingAddressUriBuilderFactory")
			final ShippingAddressUriBuilderFactory shippingAddressUriBuilderFactory) {

		this.shippingAddressLookup = shippingAddressLookup;
		this.shippingAddressUriBuilderFactory = shippingAddressUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentEntity> resourceState) {

		ExecutionResult<ResourceState<AddressEntity>> addressResult = shippingAddressLookup.getShippingAddress(resourceState);

		Collection<ResourceLink> linksToAdd;
		if (addressResult.isSuccessful()) {
			String uri = shippingAddressUriBuilderFactory.get().setSourceUri(ResourceStateUtil.getSelfUri(resourceState)).build();

			ResourceLink link
				= ResourceLinkFactory.create(
					uri,
					AddressesMediaTypes.ADDRESS.id(),
					ShippingAddressResourceRels.SHIPPING_ADDRESS_REL,
					ShipmentsResourceRels.SHIPMENT_REV);
			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}
}

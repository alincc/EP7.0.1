/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.shipments.addresses.rel.ShippingAddressResourceRels;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ShippingAddressUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transforms a shipping {@link AddressEntity}.
 */
@Singleton
@Named("shippingAddressTransformer")
public final class ShippingAddressTransformer implements
		TransformRfoToResourceState<AddressEntity, AddressEntity, ShipmentEntity> {

	private final ShippingAddressUriBuilderFactory shippingAddressUriBuilderFactory;

	/**
	 * Default constructor.
	 * 
	 * @param shippingAddressUriBuilderFactory the shipping address uri builder factory
	 */
	@Inject
	public ShippingAddressTransformer(
			@Named("shippingAddressUriBuilderFactory")
			final ShippingAddressUriBuilderFactory shippingAddressUriBuilderFactory) {
		this.shippingAddressUriBuilderFactory = shippingAddressUriBuilderFactory;
	}

	/**
	 * Transforms a shipping address entity into a representation.
	 * 
	 * @param addressEntity the address entity
	 * @param shipmentRepresentation the shipment representation
	 * @return the address representation.
	 */
	public ResourceState<AddressEntity> transform(
			final AddressEntity addressEntity,
			final ResourceState<ShipmentEntity> shipmentRepresentation) {

		String shipmentUri = ResourceStateUtil.getSelfUri(shipmentRepresentation);
		String selfUri = shippingAddressUriBuilderFactory.get().setSourceUri(shipmentUri).build();
		Self self = SelfFactory.createSelf(selfUri);

		ResourceLink shipmentLink = ResourceLinkFactory.create(shipmentUri, ShipmentsMediaTypes.SHIPMENT.id(), ShipmentsResourceRels.SHIPMENT_REL,
				ShippingAddressResourceRels.SHIPPING_ADDRESS_REV);

		return ResourceState.Builder.create(addressEntity)
				.withScope(shipmentRepresentation.getScope())
				.withSelf(self)
				.addingLinks(shipmentLink)
				.build();
	}
}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.transform;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.resource.shipments.shippingoption.rel.ShippingOptionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transformer for converting {@link ShippingOptionEntity} into {@link com.elasticpath.rest.schema.ResourceState}.
 */
@Named("shippingOptionTransformer")
public final class ShippingOptionTransformer implements
		TransformRfoToResourceState<ShippingOptionEntity, ShippingOptionEntity, ShipmentEntity> {

	private final ShippingOptionUriBuilderFactory shippingOptionUriBuilderFactory;

	/**
	 * Constructor.
	 * 
	 * @param shippingOptionUriBuilderFactory a {@link com.elasticpath.rest.schema.uri.ShippingOptionUriBuilderFactory}
	 */
	@Inject
	public ShippingOptionTransformer(
			@Named("shippingOptionUriBuilderFactory")
			final ShippingOptionUriBuilderFactory shippingOptionUriBuilderFactory) {
		this.shippingOptionUriBuilderFactory = shippingOptionUriBuilderFactory;
	}

	@Override
	public ResourceState<ShippingOptionEntity> transform(final ShippingOptionEntity entity,
			final ResourceState<ShipmentEntity> shipmentRepresentation) {
		
		String shipmentUri = ResourceStateUtil.getSelfUri(shipmentRepresentation);
		String selfUri = shippingOptionUriBuilderFactory.get().setSourceUri(shipmentUri).build();
		Self self = SelfFactory.createSelf(selfUri);

		return ResourceState.Builder.create(entity)
				.withScope(shipmentRepresentation.getScope())
				.withSelf(self)
				.withLinks(Collections.singleton(createShipmentLink(shipmentUri)))
				.build();
	}

	private ResourceLink createShipmentLink(final String shipmentUri) {
		return ResourceLinkFactory.create(
				shipmentUri,
				ShipmentsMediaTypes.SHIPMENT.id(),
				ShipmentsResourceRels.SHIPMENT_REL,
				ShippingOptionResourceRels.SHIPPING_OPTION_REV);
	}
}

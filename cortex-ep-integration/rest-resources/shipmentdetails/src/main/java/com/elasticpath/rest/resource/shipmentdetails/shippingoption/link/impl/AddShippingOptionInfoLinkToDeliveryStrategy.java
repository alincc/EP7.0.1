/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.rel.ShipmentDetailsRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilderFactory;

/**
 * Link strategy to add a shippingoptioninfo link to an order delivery.
 */
@Singleton
@Named("addShipmentShippingOptionInfoLinkToDeliveryStrategy")
public final class AddShippingOptionInfoLinkToDeliveryStrategy implements ResourceStateLinkHandler<DeliveryEntity> {

	private final ShippingOptionInfoUriBuilderFactory shippingOptionInfoUriBuilderFactory;
	private final ShipmentDetailsLookup shipmentDetailsLookup;


	/**
	 * Default constructor.
	 *
	 * @param shipmentDetailsLookup the shipment details lookup
	 * @param shippingOptionInfoUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilderFactory}
	 */
	@Inject
	public AddShippingOptionInfoLinkToDeliveryStrategy(
			@Named("shipmentDetailsLookup")
			final ShipmentDetailsLookup shipmentDetailsLookup,
			@Named("shippingOptionInfoUriBuilderFactory")
			final ShippingOptionInfoUriBuilderFactory shippingOptionInfoUriBuilderFactory) {

		this.shipmentDetailsLookup = shipmentDetailsLookup;
		this.shippingOptionInfoUriBuilderFactory = shippingOptionInfoUriBuilderFactory;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<DeliveryEntity> delivery) {

		ExecutionResult<String> shipmentDetailsId = shipmentDetailsLookup.findShipmentDetailsIdForDelivery(delivery);

		if (shipmentDetailsId.isSuccessful()) {
			String scope = delivery.getScope();

			String shippingOptionInfoUri = shippingOptionInfoUriBuilderFactory.get()
															.setScope(scope)
															.setShipmentDetailsId(shipmentDetailsId.getData())
															.build();

			return Collections.singleton(createShippingOptionInfoLink(shippingOptionInfoUri));
		}

		return Collections.emptyList();
	}

	private ResourceLink createShippingOptionInfoLink(final String shippingOptionInfoUri) {
		return ResourceLinkFactory.create(shippingOptionInfoUri, ControlsMediaTypes.INFO.id(),
										ShipmentDetailsRels.SHIPPING_OPTION_INFO_REL, ShipmentDetailsRels.DELIVERY_REV);
	}
}

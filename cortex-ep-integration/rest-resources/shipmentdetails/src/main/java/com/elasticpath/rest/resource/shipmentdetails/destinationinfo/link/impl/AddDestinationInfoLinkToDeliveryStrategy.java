/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.constants.DestinationInfoConstants;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.rel.DestinationInfoRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The Strategy to add address info links to physical deliveries.
 */
@Singleton
@Named("addDestinationInfoLinkToDeliveryStrategy")
public final class AddDestinationInfoLinkToDeliveryStrategy implements ResourceStateLinkHandler<DeliveryEntity> {

	private final String resourceServerName;
	private final ShipmentDetailsLookup shipmentDetailsLookup;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param shipmentDetailsLookup the shipment details lookup
	 */
	@Inject
	public AddDestinationInfoLinkToDeliveryStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("shipmentDetailsLookup")
			final ShipmentDetailsLookup shipmentDetailsLookup) {

		this.resourceServerName = resourceServerName;
		this.shipmentDetailsLookup = shipmentDetailsLookup;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<DeliveryEntity> delivery) {

		final Collection<ResourceLink> result;

		if (DestinationInfoConstants.SHIPMENT_TYPE.equals(delivery.getEntity().getDeliveryType())) {
			ExecutionResult<String> shipmentDetailsIdResult = shipmentDetailsLookup.findShipmentDetailsIdForDelivery(delivery);
			if (shipmentDetailsIdResult.isSuccessful()) {
				String scope = delivery.getScope();
				String destinationInfoUri = URIUtil.format(resourceServerName, scope, shipmentDetailsIdResult.getData(), DestinationInfo.URI_PART);
				ResourceLink destinationInfoLink = ResourceLinkFactory.create(destinationInfoUri, ControlsMediaTypes.INFO.id(),
						DestinationInfoRepresentationRels.DESTINATION_INFO_REL, DestinationInfoRepresentationRels.DELIVERY_REV);

				result = Collections.singleton(destinationInfoLink);
			} else {
				result = Collections.emptyList();
			}
		} else {
			result = Collections.emptyList();
		}

		return result;
	}
}

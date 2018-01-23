/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.link.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.ShipmentDetailsLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.rel.DestinationInfoRepresentationRels;
import com.elasticpath.rest.resource.shipmentdetails.rel.ShipmentDetailsRels;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.impl.NeedInfoHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Link strategy which adds {@code needinfo} links for {@code destinationinfo} and {@code shippingoptioninfo} to the order resource.
 */
@Singleton
@Named("addShippingOptionNeedInfoLinksToOrderStrategy")
public final class AddShippingOptionNeedInfoLinksToOrderStrategy implements ResourceStateLinkHandler<OrderEntity> {
	private final String resourceServerName;
	private final ShippingOptionInfoUriBuilderFactory shippingOptionInfoUriBuilderFactory;
	private final ShipmentDetailsLookup shipmentDetailsLookup;
	private final NeedInfoHandler needInfoHandler;


	/**
	 * Default constructor.
	 * @param resourceServerName the resource server name
	 * @param shipmentDetailsLookup the shipment details lookup
	 * @param shippingOptionInfoUriBuilderFactory the {@link ShippingOptionInfoUriBuilderFactory}
	 * @param needInfoHandler the {@link NeedInfoHandler}
	 */
	@Inject
	public AddShippingOptionNeedInfoLinksToOrderStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("shipmentDetailsLookup")
			final ShipmentDetailsLookup shipmentDetailsLookup,
			@Named("shippingOptionInfoUriBuilderFactory")
			final ShippingOptionInfoUriBuilderFactory shippingOptionInfoUriBuilderFactory,
			@Named
			final NeedInfoHandler needInfoHandler) {

		this.resourceServerName = resourceServerName;
		this.needInfoHandler = needInfoHandler;
		this.shipmentDetailsLookup = shipmentDetailsLookup;
		this.shippingOptionInfoUriBuilderFactory = shippingOptionInfoUriBuilderFactory;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<OrderEntity> order) {
		ExecutionResult<Collection<String>> shipmentDetailsIdsResult =
				shipmentDetailsLookup.findShipmentDetailsIdsForOrder(order.getScope(), order.getEntity().getOrderId());

		if (!shipmentDetailsIdsResult.isSuccessful()) {
			return Collections.emptyList();
		}

		Collection<ResourceLink> needInfoLinks = new ArrayList<>();
		Collection<String> shipmentDetailsIds = shipmentDetailsIdsResult.getData();
		for (String shipmentDetailsId : shipmentDetailsIds) {
			needInfoLinks.addAll(getNeedInfoLinksForShipmentDetails(order.getScope(), shipmentDetailsId));
		}

		return needInfoLinks;
	}

	private Collection<ResourceLink> getNeedInfoLinksForShipmentDetails(final String scope, final String shipmentDetailId) {
		Collection<ResourceLink> destinationNeedInfoLinks = getDestinationNeedInfoLinks(scope, shipmentDetailId);

		if (!destinationNeedInfoLinks.isEmpty()) {
			return destinationNeedInfoLinks;
		}

		return getShippingOptionNeedInfoLinks(scope, shipmentDetailId);
	}

	private Collection<ResourceLink> getDestinationNeedInfoLinks(final String scope, final String shipmentDetailId) {
		String destinationInfoUri = URIUtil.format(resourceServerName, scope, shipmentDetailId, DestinationInfo.URI_PART);

		return needInfoHandler.getNeedInfoLinksForInfo(destinationInfoUri, DestinationInfoRepresentationRels.DESTINATION_REL);
	}

	private Collection<ResourceLink> getShippingOptionNeedInfoLinks(final String scope, final String shipmentDetailId) {
		String shippingOptionInfoUri = shippingOptionInfoUriBuilderFactory.get()
				.setScope(scope)
				.setShipmentDetailsId(shipmentDetailId)
				.build();

		return needInfoHandler.getNeedInfoLinksForInfo(shippingOptionInfoUri, ShipmentDetailsRels.SHIPPINGOPTION_REL);
	}

}

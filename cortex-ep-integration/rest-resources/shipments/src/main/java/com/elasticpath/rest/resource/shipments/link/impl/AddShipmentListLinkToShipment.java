/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.link.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Adds a "list" back-link to a purchase Shipment representation.
 * The back-link points to the list of Shipments on a purchase.
 */
@Singleton
@Named("addShipmentListLinkToShipment")
public final class AddShipmentListLinkToShipment implements ResourceStateLinkHandler<ShipmentEntity> {

	private final PurchaseUriBuilderFactory purchaseUriBuilderFactory;
	private final ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param purchaseUriBuilderFactory a {@link PurchaseUriBuilderFactory}
	 * @param shipmentsUriBuilderFactory a {@link ShipmentsUriBuilderFactory}
	 */
	@Inject
	public AddShipmentListLinkToShipment(
			@Named("purchaseUriBuilderFactory")
			final PurchaseUriBuilderFactory purchaseUriBuilderFactory,
			@Named("shipmentsUriBuilderFactory")
			final ShipmentsUriBuilderFactory shipmentsUriBuilderFactory) {
		this.purchaseUriBuilderFactory = purchaseUriBuilderFactory;
		this.shipmentsUriBuilderFactory = shipmentsUriBuilderFactory;
	}

	/**
	 * Create all links from this shipment to other resources.
	 *
	 * @param shipmentRepresentation The shipment representation.
	 * @return a {@link java.util.Collection} of {@link ResourceLink}s
	 */
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentEntity> shipmentRepresentation) {
		ShipmentEntity shipmentEntity = shipmentRepresentation.getEntity();
		String scope = shipmentRepresentation.getScope();
		ResourceLink linkToShipments = getLinkToShipments(scope, shipmentEntity);
		return ImmutableList.<ResourceLink>builder()
				.add(linkToShipments)
				.build();
	}

	/**
	 * Create a link from this shipment to the list that contains it.
	 *
	 * @param scope the scope
	 * @param shipmentEntity the entity being linked from
	 * @return a {@link ResourceLink}
	 */
	ResourceLink getLinkToShipments(final String scope, final ShipmentEntity shipmentEntity) {
		String purchaseUri = purchaseUriBuilderFactory.get()
				.setScope(scope)
				.setDecodedPurchaseId(shipmentEntity.getPurchaseId())
				.build();
		String shipmentsUri = shipmentsUriBuilderFactory.get()
				.setSourceUri(purchaseUri)
				.build();
		return ElementListFactory.createListWithoutElement(shipmentsUri, CollectionsMediaTypes.LINKS.id());
	}
}
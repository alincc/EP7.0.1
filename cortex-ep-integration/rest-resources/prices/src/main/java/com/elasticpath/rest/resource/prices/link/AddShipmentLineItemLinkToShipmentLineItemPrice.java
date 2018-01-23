/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;

/**
 * Adds a back link from a shipment line item price to a shipment line item.
 */
@Singleton
@Named("addShipmentLineItemLinkToShipmentLineItemPrice")
public final class AddShipmentLineItemLinkToShipmentLineItemPrice implements ResourceStateLinkHandler<ShipmentLineItemPriceEntity> {
	private final PurchaseUriBuilderFactory purchaseUriBuilderFactory;
	private final ShipmentLineItemUriBuilderFactory shipmentLineItemUriBuilderFactory;
	private final ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;

	/**
	 * Constructor.
	 * @param purchaseUriBuilderFactory the PurchaseUriBuilderFactory.
	 * @param shipmentLineItemUriBuilderFactory the {@link ShipmentLineItemUriBuilderFactory}
	 * @param shipmentsUriBuilderFactory the {@link ShipmentsUriBuilderFactory}
	 */
	@Inject
	public AddShipmentLineItemLinkToShipmentLineItemPrice(
			@Named("purchaseUriBuilderFactory")
			final PurchaseUriBuilderFactory purchaseUriBuilderFactory,
			@Named("shipmentLineItemUriBuilderFactory")
			final ShipmentLineItemUriBuilderFactory shipmentLineItemUriBuilderFactory,
			@Named("shipmentsUriBuilderFactory")
			final ShipmentsUriBuilderFactory shipmentsUriBuilderFactory) {
		this.purchaseUriBuilderFactory = purchaseUriBuilderFactory;
		this.shipmentLineItemUriBuilderFactory = shipmentLineItemUriBuilderFactory;
		this.shipmentsUriBuilderFactory = shipmentsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentLineItemPriceEntity> shipmentLineItemPrice) {
		// TODO: or self.substring("/prices")...
		ShipmentLineItemPriceEntity shipmentLineItemEntity = shipmentLineItemPrice.getEntity();
		String purchaseUri = purchaseUriBuilderFactory.get()
				.setScope(shipmentLineItemPrice.getScope())
				.setPurchaseId(Base32Util.encode(shipmentLineItemEntity.getPurchaseId()))
				.build();
		String shipmentUri = shipmentsUriBuilderFactory.get()
				.setSourceUri(purchaseUri)
				.setShipmentId(shipmentLineItemEntity.getShipmentId())
				.build();
		String lineItemUri = shipmentLineItemUriBuilderFactory.get()
				.setSourceUri(shipmentUri)
				.setLineItemId(shipmentLineItemEntity.getShipmentLineItemId())
				.build();
		return Collections.singleton(ResourceLinkFactory.create(lineItemUri,
				ShipmentsMediaTypes.SHIPMENT_LINE_ITEM.id(),
				PriceRepresentationRels.SHIPMENT_LINE_ITEM_REL,
				PriceRepresentationRels.PRICE_REV));
	}
}
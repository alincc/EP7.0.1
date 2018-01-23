/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShipmentLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Adds a link from shipment to the list of line items.
 */
@Singleton
@Named("addLineItemLinkToShipmentStrategy")
public final class AddLineItemLinkToShipmentStrategy implements ResourceStateLinkHandler<ShipmentEntity> {

	private final ShipmentLineItemsUriBuilderFactory shipmentLineItemsUriBuilderFactory;

	/**
	 * Constructor.
	 * 
	 * @param shipmentLineItemsUriBuilderFactory the shipment line items URI builder factory
	 */
	@Inject
	public AddLineItemLinkToShipmentStrategy(@Named("shipmentLineItemsUriBuilderFactory")
	final ShipmentLineItemsUriBuilderFactory shipmentLineItemsUriBuilderFactory) {
		this.shipmentLineItemsUriBuilderFactory = shipmentLineItemsUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentEntity> resourceState) {

		String shipmentLineItemsUri = shipmentLineItemsUriBuilderFactory.get()
				.setSourceUri(ResourceStateUtil.getSelfUri(resourceState))
				.build();

		ResourceLink link = ResourceLinkFactory.createNoRev(shipmentLineItemsUri, CollectionsMediaTypes.LINKS.id(),
				ShipmentLineItemResourceRels.LINE_ITEMS_REL);

		return Collections.singleton(link);
	}
}

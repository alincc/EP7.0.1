/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.link.impl;

import static com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes.SHIPMENT_LINE_ITEM;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipments.LineItems;
import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Link Handler.
 */
@Singleton
@Named("addShipmentLineItemLinkToShipmentLineItemTaxes")
public final class AddShipmentLineItemLinkToShipmentLineItemTaxes implements ResourceStateLinkHandler<TaxesEntity> {

	// TODO: The fact that I need these constants means that the programming model is insufficient.
	private static final String TAXES_RESOURCE_PATH_PART = "/taxes";
	private static final String SHIPMENTS_RESOURCE_PATH_PART = "/shipments";

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<TaxesEntity> resourceState) {

		// Incoming self should be: /taxes/shipments/{purchases-uri}/{shipment-id}/lineitems/{lineitem-id}
		String selfUri = resourceState.getSelf().getUri();
		if (!(selfUri.contains(SHIPMENTS_RESOURCE_PATH_PART) && selfUri.contains(LineItems.PATH_PART))) {
			return ImmutableList.of();
		}

		// Outgoing link should be /shipments/{purchases-uri}/{shipment-id}/lineitems/{lineitem-id} aka. the "other-uri".
		return ImmutableList.<ResourceLink>builder()
				.add(createShipmentLineItemLink(selfUri))
				.build();
	}

	private ResourceLink createShipmentLineItemLink(final String taxesSelfUri) {
		// Starting with /taxes/shipments/{purchases-uri}/{shipment-id}/lineitems/{lineitem-id}
		// Outgoing link should be /shipments/{purchases-uri}/{shipment-id}/lineitems/{lineitem-id}
		// Just need to trim the leading "/taxes":
		String shipmentLineItemUri = taxesSelfUri.substring(TAXES_RESOURCE_PATH_PART.length());

		return ResourceLinkFactory.create(
				shipmentLineItemUri,
				SHIPMENT_LINE_ITEM.id(),
				ShipmentLineItemResourceRels.LINE_ITEM_REL,
				ShipmentLineItemResourceRels.LINE_ITEM_TAXES_REV
		);
	}
}

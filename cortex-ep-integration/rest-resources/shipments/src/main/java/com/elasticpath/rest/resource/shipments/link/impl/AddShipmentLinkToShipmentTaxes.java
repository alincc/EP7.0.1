/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.link.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableList;

import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Link Handler.
 */
@Singleton
@Named("addShipmentLinkToShipmentTaxes")
public final class AddShipmentLinkToShipmentTaxes implements ResourceStateLinkHandler<TaxesEntity> {

	private static final String TAXES_PATH_PART = "/taxes";

	// Pattern that matches: /taxes/shipments/purchases/{scope}/{purchase-id}/{shipment-id}
	private static final String SHIPMENT_TAXES_URI_PATTERN = "^/taxes/shipments/purchases/[^/]+/[^/]+/[^/]+/?$";

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<TaxesEntity> resourceState) {

		String taxesSelfUri = resourceState.getSelf().getUri();
		if (!taxesSelfUri.matches(SHIPMENT_TAXES_URI_PATTERN)) {
			return ImmutableList.of();
		}

		ResourceLink shipmentLink = createShipmentLink(taxesSelfUri);
		return ImmutableList.<ResourceLink>builder()
				.add(shipmentLink)
				.build();
	}

	private ResourceLink createShipmentLink(final String taxesSelfUri) {
		String shipmentUri = taxesSelfUri.substring(TAXES_PATH_PART.length());
		return ResourceLinkFactory.create(
				shipmentUri,
				ShipmentsMediaTypes.SHIPMENT.id(),
				ShipmentsResourceRels.SHIPMENT_REL,
				ShipmentsResourceRels.TAX_REV);
	}
}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.link;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.taxes.TaxesMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.taxes.rel.TaxesResourceRels;
import com.elasticpath.rest.resource.taxes.shipment.rel.ShipmentTaxesResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Creates a link from Shipment Line Item to Shipment Line Item taxes.
 */
@Singleton
@Named("addTaxesLinkToShipmentLineItem")
public class AddTaxesLinkToShipmentLineItem implements ResourceStateLinkHandler<ShipmentLineItemEntity> {

	private final TaxesUriBuilderFactory taxesUriBuilderFactory;

	/**
	 * Constructor. 
	 * 
	 * @param taxesUriBuilderFactory a {@link TaxesUriBuilderFactory}
	 */
	@Inject
	public AddTaxesLinkToShipmentLineItem(
			@Named("taxesUriBuilderFactory")
			final TaxesUriBuilderFactory taxesUriBuilderFactory) {
		this.taxesUriBuilderFactory = taxesUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentLineItemEntity> shipment) {
		String sourceUri = ResourceStateUtil.getSelfUri(shipment);
		String taxesUri = taxesUriBuilderFactory.get()
				.setSourceUri(sourceUri)
				.build();
		ResourceLink taxesLink
				= ResourceLinkFactory.create(
					taxesUri,
					TaxesMediaTypes.TAXES.id(),
					TaxesResourceRels.TAX_REL,
					ShipmentTaxesResourceRels.SHIPMENT_LINE_ITEM_REV);
		return Collections.singletonList(taxesLink);
	}

}

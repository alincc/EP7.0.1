/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.link;

import static com.elasticpath.rest.definition.taxes.TaxesMediaTypes.TAXES;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.taxes.rel.TaxesResourceRels;
import com.elasticpath.rest.resource.taxes.shipment.rel.ShipmentTaxesResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Creates a link from Shipment to ShipmentTaxes.
 */
@Singleton
@Named("linkShipmentToShipmentTaxesStrategy")
public class LinkShipmentToShipmentTaxesStrategy implements ResourceStateLinkHandler<ShipmentEntity> {

	private final TaxesUriBuilderFactory taxesUriBuilderFactory;

	/**
	 * Constructor. 
	 * 
	 * @param taxesUriBuilderFactory a {@link TaxesUriBuilderFactory}
	 */
	@Inject
	public LinkShipmentToShipmentTaxesStrategy(
			@Named("taxesUriBuilderFactory")
			final TaxesUriBuilderFactory taxesUriBuilderFactory) {
		this.taxesUriBuilderFactory = taxesUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentEntity> shipment) {
		String sourceUri = ResourceStateUtil.getSelfUri(shipment);
		String taxesUri = taxesUriBuilderFactory.get()
				.setSourceUri(sourceUri)
				.build();
		ResourceLink link = ResourceLinkFactory
				.create(taxesUri, TAXES.id(), TaxesResourceRels.TAX_REL, ShipmentTaxesResourceRels.SHIPMENT_REV);
		return Collections.singletonList(link);
	}

}

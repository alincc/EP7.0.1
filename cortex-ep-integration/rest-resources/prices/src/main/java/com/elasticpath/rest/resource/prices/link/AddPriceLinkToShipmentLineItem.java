/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.link;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.prices.PricesMediaTypes;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.prices.rel.PriceRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Create a Link to Price on a Cart Line Item Representation.
 */
@Singleton
@Named("addPriceLinkToShipmentLineItem")
public final class AddPriceLinkToShipmentLineItem implements ResourceStateLinkHandler<ShipmentLineItemEntity> {

	private final PricesUriBuilderFactory pricesUriBuilderFactory;

	/**
	 * Constructor.
	 * @param pricesUriBuilderFactory  the {@link PricesUriBuilderFactory}.
	 */
	@Inject
	public AddPriceLinkToShipmentLineItem(
			@Named("pricesUriBuilderFactory")
			final PricesUriBuilderFactory pricesUriBuilderFactory) {
		this.pricesUriBuilderFactory = pricesUriBuilderFactory;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentLineItemEntity> lineItem) {
		String priceUri = pricesUriBuilderFactory.get()
				.setSourceUri(lineItem.getSelf().getUri())
				.build();
		ResourceLink link = ResourceLinkFactory.create(priceUri,
				PricesMediaTypes.SHIPMENT_LINE_ITEM_PRICE.id(), PriceRepresentationRels.PRICE_REL,
				PriceRepresentationRels.SHIPMENT_LINE_ITEM_REV);
		return Collections.singleton(link);
	}
}

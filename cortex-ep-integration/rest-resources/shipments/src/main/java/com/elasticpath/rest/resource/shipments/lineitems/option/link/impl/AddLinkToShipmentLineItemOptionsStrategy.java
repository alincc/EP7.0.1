/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.shipments.lineitems.option.integration.ShipmentLineItemOptionsLookupStrategy;
import com.elasticpath.rest.resource.shipments.lineitems.option.rel.ShipmentLineItemOptionResourceRels;
import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Strategy to add link to options on a shipment line item.
 */
@Singleton
@Named("addLinkToShipmentLineItemOptionsStrategy")
public final class AddLinkToShipmentLineItemOptionsStrategy implements ResourceStateLinkHandler<ShipmentLineItemEntity> {

	private final ShipmentLineItemOptionsLookupStrategy shipmentLineItemOptionsLookupStrategy;

	private final ShipmentLineItemOptionsUriBuilderFactory shipmentLineItemOptionsUriBuilderFactory;

	/**
	 * Default constructor.
	 * 
	 * @param shipmentLineItemOptionsLookupStrategy the ShipmentLineItemOptionsLookupStrategy
	 * @param shipmentLineItemOptionsUriBuilderFactory the ShipmentLineItemOptionsUriBuilderFactory
	 */
	@Inject
	public AddLinkToShipmentLineItemOptionsStrategy(
			@Named("shipmentLineItemOptionsLookupStrategy")
			final ShipmentLineItemOptionsLookupStrategy shipmentLineItemOptionsLookupStrategy,
			@Named("shipmentLineItemOptionsUriBuilderFactory")
			final ShipmentLineItemOptionsUriBuilderFactory shipmentLineItemOptionsUriBuilderFactory) {

		this.shipmentLineItemOptionsLookupStrategy = shipmentLineItemOptionsLookupStrategy;
		this.shipmentLineItemOptionsUriBuilderFactory = shipmentLineItemOptionsUriBuilderFactory;
	}


	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentLineItemEntity> resourceState) {

		final Collection<ResourceLink> result;
		ShipmentLineItemEntity shipmentLineItem = resourceState.getEntity();

		ExecutionResult<Collection<String>> findOptionsResult = shipmentLineItemOptionsLookupStrategy
				.findLineItemOptionIds(shipmentLineItem);

		if (findOptionsResult.isFailure()) {
			result = Collections.emptyList();
		} else if (CollectionUtil.isNotEmpty(findOptionsResult.getData())) {
			String lineItemUri = ResourceStateUtil.getSelfUri(resourceState);
			String lineItemOptionsUri = shipmentLineItemOptionsUriBuilderFactory.get().setSourceUri(lineItemUri).build();
			ResourceLink lineItemOptionsLink = ResourceLinkFactory.create(lineItemOptionsUri, CollectionsMediaTypes.LINKS.id(),
					ShipmentLineItemOptionResourceRels.LINE_ITEMS_OPTIONS_REL, ShipmentLineItemResourceRels.LINE_ITEM_REL);
			result = Collections.singleton(lineItemOptionsLink);
		} else {
			result = Collections.emptyList();
		}

		return result;
	}
}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.ShipmentLineItemsUriBuilderFactory;

/**
 * A transfomer for shipment line items.
 */
@Singleton
@Named("shipmentLineItemTransformer")
public final class ShipmentLineItemTransformerImpl implements TransformToResourceState<ShipmentLineItemEntity, ShipmentLineItemEntity> {

	private final ResourceOperationContext operationContext;

	private final ShipmentLineItemsUriBuilderFactory shipmentLineItemsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param operationContext the resource operation context
	 * @param shipmentLineItemsUriBuilderFactory the shipment line items URI builder factory
	 */
	@Inject
	public ShipmentLineItemTransformerImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext,
			@Named("shipmentLineItemsUriBuilderFactory")
			final ShipmentLineItemsUriBuilderFactory shipmentLineItemsUriBuilderFactory) {
		this.operationContext = operationContext;
		this.shipmentLineItemsUriBuilderFactory = shipmentLineItemsUriBuilderFactory;
	}

	@Override
	public ResourceState<ShipmentLineItemEntity> transform(
			final String scope,
			final ShipmentLineItemEntity entity) {

		String shipmentUri = entity.getParentUri();

		String selfUri = operationContext.getResourceOperation().getUri();

		Self self = SelfFactory.createSelf(selfUri);

		String lineItemsUri = shipmentLineItemsUriBuilderFactory.get().setSourceUri(shipmentUri).build();
		ResourceLink lineItemsLink = ResourceLinkFactory.createNoRev(lineItemsUri, CollectionsMediaTypes.LINKS.id(), "list");

		ResourceLink shipmentLink =
				ResourceLinkFactory.createNoRev(shipmentUri, ShipmentsMediaTypes.SHIPMENT.id(), ShipmentsResourceRels.SHIPMENT_REL);

		return ResourceState.Builder.create(entity)
				.withSelf(self)
				.withScope(scope)
				.addingLinks(lineItemsLink, shipmentLink)
				.build();
	}

}

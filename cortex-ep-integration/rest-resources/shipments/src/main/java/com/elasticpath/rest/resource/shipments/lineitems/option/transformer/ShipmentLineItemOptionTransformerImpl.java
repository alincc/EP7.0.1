/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.shipments.lineitems.option.rel.ShipmentLineItemOptionResourceRels;
import com.elasticpath.rest.resource.shipments.lineitems.option.value.rel.ShipmentLineItemOptionValueResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionValueUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transformer for shipment line item option.
 */
@Singleton
@Named("shipmentLineItemOptionTransformer")
public final class ShipmentLineItemOptionTransformerImpl implements
		TransformRfoToResourceState<ShipmentLineItemOptionEntity, ShipmentLineItemOptionEntity, ShipmentLineItemEntity> {

	private final ResourceOperationContext operationContext;

	private final ShipmentLineItemOptionsUriBuilderFactory shipmentLineItemOptionsUriBuilderFactory;

	private final ShipmentLineItemOptionValueUriBuilderFactory shipmentLineItemOptionValueUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param operationContext the resource operation context
	 * @param shipmentLineItemOptionsUriBuilderFactory the ShipmentLineItemOptionsUriBuilderFactory
	 * @param shipmentLineItemOptionValueUriBuilderFactory the ShipmentLineItemOptionValueUriBuilderFactory
	 */
	@Inject
	public ShipmentLineItemOptionTransformerImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext,
			@Named("shipmentLineItemOptionsUriBuilderFactory")
			final ShipmentLineItemOptionsUriBuilderFactory shipmentLineItemOptionsUriBuilderFactory,
			@Named("shipmentLineItemOptionValueUriBuilderFactory")
			final ShipmentLineItemOptionValueUriBuilderFactory shipmentLineItemOptionValueUriBuilderFactory) {
		this.operationContext = operationContext;
		this.shipmentLineItemOptionsUriBuilderFactory = shipmentLineItemOptionsUriBuilderFactory;
		this.shipmentLineItemOptionValueUriBuilderFactory = shipmentLineItemOptionValueUriBuilderFactory;

	}

	@Override
	public ResourceState<ShipmentLineItemOptionEntity> transform(
			final ShipmentLineItemOptionEntity entity, final ResourceState<ShipmentLineItemEntity> shipmentLineItem) {

		String selfUri = operationContext.getResourceOperation().getUri();
		Self self = SelfFactory.createSelf(selfUri);

		String lineItemUri = ResourceStateUtil.getSelfUri(shipmentLineItem);
		String lineItemOptionLinksUri = shipmentLineItemOptionsUriBuilderFactory.get().setSourceUri(lineItemUri).build();
		ResourceLink lineItemOptionsLink = ResourceLinkFactory.createNoRev(lineItemOptionLinksUri, CollectionsMediaTypes.LINKS.id(), "list");

		String lineItemOptionValueUri = shipmentLineItemOptionValueUriBuilderFactory.get()
				.setSourceUri(selfUri)
				.setOptionValueId(entity.getLineItemOptionValueId())
				.build();
		ResourceLink lineItemOptionValueUriLink = ResourceLinkFactory.create(
				lineItemOptionValueUri, ShipmentsMediaTypes.SHIPMENT_LINE_ITEM_OPTION_VALUE.id(),
				ShipmentLineItemOptionValueResourceRels.LINE_ITEMS_OPTION_VALUE_REL, ShipmentLineItemOptionResourceRels.LINE_ITEMS_OPTION_REV);

		return ResourceState.Builder.create(entity)
				.withScope(shipmentLineItem.getScope())
				.withSelf(self)
				.addingLinks(lineItemOptionsLink, lineItemOptionValueUriLink)
				.build();
	}


}

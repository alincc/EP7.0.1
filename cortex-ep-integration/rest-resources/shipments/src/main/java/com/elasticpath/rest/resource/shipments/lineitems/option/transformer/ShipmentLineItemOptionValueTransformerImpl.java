/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.shipments.lineitems.option.rel.ShipmentLineItemOptionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transformer for shipment line item option values.
 */
@Singleton
@Named("shipmentLineItemOptionValueTransformer")
public final class ShipmentLineItemOptionValueTransformerImpl implements TransformRfoToResourceState<ShipmentLineItemOptionValueEntity,
		ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionEntity> {

	private final ResourceOperationContext operationContext;

	/**
	 * Constructor.
	 *
	 * @param operationContext the resource operation context
	 */
	@Inject
	public ShipmentLineItemOptionValueTransformerImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext) {
		this.operationContext = operationContext;
	}


	@Override
	public ResourceState<ShipmentLineItemOptionValueEntity> transform(
			final ShipmentLineItemOptionValueEntity entity, final ResourceState<ShipmentLineItemOptionEntity> lineItemOptionRepresentation) {

		String lineItemOptionUri = ResourceStateUtil.getSelfUri(lineItemOptionRepresentation);

		String selfUri = operationContext.getResourceOperation().getUri();
		Self self = SelfFactory.createSelf(selfUri);

		ResourceLink lineItemOptionsLink = ResourceLinkFactory.createNoRev(lineItemOptionUri, ShipmentsMediaTypes.SHIPMENT_LINE_ITEM_OPTION.id(),
				ShipmentLineItemOptionResourceRels.LINE_ITEMS_OPTION_REV);

		return ResourceState.Builder.create(entity)
				.withSelf(self)
				.addingLinks(lineItemOptionsLink)
				.build();
	}

}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.resource.shipments.rel.ShipmentsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transformer for shipment line itme links.
 */
@Singleton
@Named("shipmentLineItemLinksTransformer")
public final class ShipmentLineItemLinksTransformerImpl implements TransformRfoToResourceState<LinksEntity, Collection<String>, ShipmentEntity> {

	private final ResourceOperationContext operationContext;

	private final ShipmentLineItemUriBuilderFactory shipmentLineItemUriBuilderFactory;


	/**
	 * Constructor.
	 *
	 * @param operationContext the resource operation context
	 * @param shipmentLineItemUriBuilderFactory the shipment line item URI builder factory
	 */
	@Inject
	public ShipmentLineItemLinksTransformerImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext,
			@Named("shipmentLineItemUriBuilderFactory")
			final ShipmentLineItemUriBuilderFactory shipmentLineItemUriBuilderFactory) {
		this.operationContext = operationContext;
		this.shipmentLineItemUriBuilderFactory = shipmentLineItemUriBuilderFactory;
	}

	@Override
	public ResourceState<LinksEntity> transform(final Collection<String> shipmentLineItemIDs,
			final ResourceState<ShipmentEntity> otherRepresentation) {

		String shipmentUri = ResourceStateUtil.getSelfUri(otherRepresentation);
		ResourceLink shipmentLink = ResourceLinkFactory.create(shipmentUri, ShipmentsMediaTypes.SHIPMENT.id(),
				ShipmentsResourceRels.SHIPMENT_REL,	ShipmentLineItemResourceRels.LINE_ITEMS_REV);

		String selfUri = operationContext.getResourceOperation().getUri();

		Self self = SelfFactory.createSelf(selfUri);

		List<ResourceLink> links = new ArrayList<>();
		links.add(shipmentLink);

		for (String lineItemId : shipmentLineItemIDs) {
			String elementUri = shipmentLineItemUriBuilderFactory.get()
					.setSourceUri(shipmentUri)
					.setLineItemId(lineItemId)
					.build();
			ResourceLink link = ElementListFactory.createElementOfList(elementUri, ShipmentsMediaTypes.SHIPMENT_LINE_ITEM.id());
			links.add(link);
		}

		return ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withLinks(links)
				.build();
	}

}

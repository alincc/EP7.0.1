/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentsMediaTypes;
import com.elasticpath.rest.resource.shipments.lineitems.option.rel.ShipmentLineItemOptionResourceRels;
import com.elasticpath.rest.resource.shipments.lineitems.rel.ShipmentLineItemResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ShipmentLineItemOptionUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Transformer for shipment line item option links.
 */
@Singleton
@Named("shipmentLineItemOptionLinksTransformer")
public final class ShipmentLineItemOptionLinksTransformerImpl implements TransformRfoToResourceState<LinksEntity, Collection<String>,
		ShipmentLineItemEntity> {

	private final ShipmentLineItemOptionUriBuilderFactory shipmentLineItemOptionUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionUriBuilderFactory the ShipmentLineItemOptionUriBuilderFactory
	 */
	@Inject
	public ShipmentLineItemOptionLinksTransformerImpl(
			@Named("shipmentLineItemOptionUriBuilderFactory")
			final ShipmentLineItemOptionUriBuilderFactory shipmentLineItemOptionUriBuilderFactory) {

		this.shipmentLineItemOptionUriBuilderFactory = shipmentLineItemOptionUriBuilderFactory;

	}

	@Override
	public ResourceState<LinksEntity> transform(final Collection<String> shipmentLineItemOptionIDs,
			final ResourceState<ShipmentLineItemEntity> lineItemRepresentation) {

		String lineItemUri = ResourceStateUtil.getSelfUri(lineItemRepresentation);
		ResourceLink lineItemLink = ResourceLinkFactory.create(lineItemUri, ShipmentsMediaTypes.SHIPMENT_LINE_ITEM.id(),
				ShipmentLineItemResourceRels.LINE_ITEM_REL, ShipmentLineItemOptionResourceRels.LINE_ITEMS_OPTIONS_REV);

		String selfUri = shipmentLineItemOptionUriBuilderFactory.get().setSourceUri(lineItemUri).build();
		Self self = SelfFactory.createSelf(selfUri);

		List<ResourceLink> links = new ArrayList<>();
		links.add(lineItemLink);

		if (shipmentLineItemOptionIDs != null) {
			for (String shipmentLineItemOptionID : shipmentLineItemOptionIDs) {
				String lineItemOptionUri = shipmentLineItemOptionUriBuilderFactory.get().setSourceUri(lineItemUri)
						.setOptionId(shipmentLineItemOptionID).build();
				ResourceLink link = ElementListFactory.createElementOfList(lineItemOptionUri, ShipmentsMediaTypes.SHIPMENT_LINE_ITEM_OPTION.id());
				links.add(link);
			}
		}

		return ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withLinks(links)
				.build();
		}
}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.TotalResourceLinkCreator;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 *  Create link to totals from a shipment line item.
 */
@Singleton
@Named("shipmentLineItemToTotalLinkHandler")
public final class ShipmentLineItemToTotalLinkHandler implements ResourceStateLinkHandler<ShipmentLineItemEntity> {

	private final TotalResourceLinkCreator totalResourceLinkCreator;

	/**
	  * Constructor.
	  *
	  * @param totalResourceLinkCreator the total resource link creator
	  */
	@Inject
	ShipmentLineItemToTotalLinkHandler(
			@Named("totalResourceLinkCreator")
			final TotalResourceLinkCreator totalResourceLinkCreator
	) {
		this.totalResourceLinkCreator = totalResourceLinkCreator;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentLineItemEntity> shipmentLineItem) {
		return totalResourceLinkCreator.createLinkToOtherResource(ResourceStateUtil.getSelfUri(shipmentLineItem), TotalResourceRels.LINE_ITEM_REV);
	}
}

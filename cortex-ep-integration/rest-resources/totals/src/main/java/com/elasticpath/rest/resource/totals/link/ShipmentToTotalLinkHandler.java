/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.TotalResourceLinkCreator;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Create a Link to shipment total on a Shipment Representation.
 */
@Singleton
@Named("shipmentToTotalLinkHandler")
public class ShipmentToTotalLinkHandler implements ResourceStateLinkHandler<ShipmentEntity> {

	private final TotalResourceLinkCreator totalResourceLinkCreator;

	private final TotalLookup<ShipmentEntity> totalLookup;

	/**
	 * Constructor.
	 *
	 * @param totalResourceLinkCreator the total resource link creator
	 * @param totalLookup the total lookup
	 */
	@Inject
	ShipmentToTotalLinkHandler(
			@Named("totalResourceLinkCreator")
			final TotalResourceLinkCreator totalResourceLinkCreator,
			@Named("shipmentTotalLookup")
			final TotalLookup<ShipmentEntity> totalLookup) {
		this.totalResourceLinkCreator = totalResourceLinkCreator;
		this.totalLookup = totalLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ShipmentEntity> shipment) {
		ExecutionResult<ResourceState<TotalEntity>> shipmentTotalResult = totalLookup.getTotal(shipment);

		return totalResourceLinkCreator.createLinkToOtherResource(ResourceStateUtil.getSelfUri(shipment), shipmentTotalResult,
				TotalResourceRels.SHIPMENT_REV);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.TotalResourceLinkCreator;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Create a Link to order total on an Item Representation.
 */
@Singleton
@Named("orderToTotalLinkHandler")
public final class OrderToTotalLinkHandler implements ResourceStateLinkHandler<OrderEntity> {

	private final TotalResourceLinkCreator totalResourceLinkCreator;

	/**
	 * Constructor.
	 *
	 * @param totalResourceLinkCreator the total resource link creator
	 */
	@Inject
	OrderToTotalLinkHandler(
			@Named("totalResourceLinkCreator")
			final TotalResourceLinkCreator totalResourceLinkCreator) {
		this.totalResourceLinkCreator = totalResourceLinkCreator;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<OrderEntity> order) {
		return totalResourceLinkCreator.createLinkToOtherResource(ResourceStateUtil.getSelfUri(order), TotalResourceRels.ORDER_REV);
	}
}

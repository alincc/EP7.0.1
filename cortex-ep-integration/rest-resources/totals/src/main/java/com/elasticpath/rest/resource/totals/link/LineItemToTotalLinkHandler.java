/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.TotalResourceLinkCreator;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 *  Create link to totals from a line item.
 */
@Singleton
@Named
public final class LineItemToTotalLinkHandler implements ResourceStateLinkHandler<LineItemEntity> {

	private final TotalResourceLinkCreator totalResourceLinkCreator;
	private final TotalLookup<LineItemEntity> totalLookup;

	/**
	  * Constructor.
	  *
	  * @param totalResourceLinkCreator the total resource link creator
	  * @param totalLookup Total lookup.
	  */
	@Inject
	LineItemToTotalLinkHandler(
			@Named("totalResourceLinkCreator")
			final TotalResourceLinkCreator totalResourceLinkCreator,
			@Named("lineItemTotalLookup")
			final TotalLookup<LineItemEntity> totalLookup) {
		this.totalResourceLinkCreator = totalResourceLinkCreator;
		this.totalLookup = totalLookup;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<LineItemEntity> cartLineItem) {
		ExecutionResult<ResourceState<TotalEntity>> lineItemTotalResult = totalLookup.getTotal(cartLineItem);

		return totalResourceLinkCreator.createLinkToOtherResource(
				ResourceStateUtil.getSelfUri(cartLineItem), lineItemTotalResult, TotalResourceRels.LINE_ITEM_REV);
	}
}

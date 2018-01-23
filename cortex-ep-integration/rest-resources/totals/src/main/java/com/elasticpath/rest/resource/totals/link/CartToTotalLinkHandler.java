/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.TotalResourceLinkCreator;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Create a Link to Prices on an Item Representation.
 */
@Singleton
@Named("cartToTotalLinkHandler")
public final class CartToTotalLinkHandler implements ResourceStateLinkHandler<CartEntity> {

	private final TotalResourceLinkCreator totalResourceLinkCreator;
	private final TotalLookup<CartEntity> totalLookup;

	/**
	 * Constructor.
	 *
	 * @param totalResourceLinkCreator the total resource link creator
	 * @param totalLookup Total lookup.
	 */
	@Inject
	CartToTotalLinkHandler(
			@Named("totalResourceLinkCreator")
			final TotalResourceLinkCreator totalResourceLinkCreator,
			@Named("cartTotalLookup")
			final TotalLookup<CartEntity> totalLookup) {
		this.totalResourceLinkCreator = totalResourceLinkCreator;
		this.totalLookup = totalLookup;
	}


	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<CartEntity> cart) {
		ExecutionResult<ResourceState<TotalEntity>> cartTotalResult = totalLookup.getTotal(cart);

		return totalResourceLinkCreator.createLinkToOtherResource(ResourceStateUtil.getSelfUri(cart), cartTotalResult, TotalResourceRels.CART_REV);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.definition.rates.RatesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.rates.integration.CartLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Create a Link to Rate on a Cart Line Item Representation.
 */
@Singleton
@Named("linkCartLineItemStrategy")
public final class LinkCartLineItemStrategy implements ResourceStateLinkHandler<LineItemEntity> {

	private final String resourceServerName;
	private final CartLineItemRateLookupStrategy cartLineItemRateLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param cartLineItemRateLookupStrategy the line item rate lookup
	 */
	@Inject
	LinkCartLineItemStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("cartLineItemRateLookupStrategy")
			final CartLineItemRateLookupStrategy cartLineItemRateLookupStrategy) {

		this.resourceServerName = resourceServerName;
		this.cartLineItemRateLookupStrategy = cartLineItemRateLookupStrategy;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<LineItemEntity> lineItem) {
		final Collection<ResourceLink> linksToAdd;

		String lineItemUri = ResourceStateUtil.getSelfUri(lineItem);
		String uri = URIUtil.format(resourceServerName, lineItemUri);
		ResourceLink link = ResourceLinkFactory.create(uri,	RatesMediaTypes.RATE.id(), RateRepresentationRels.RATE_REL,
				RateRepresentationRels.LINE_ITEM_REV);

		String scope = lineItem.getScope();
		String cartId = Base32Util.decode(lineItem.getEntity().getCartId());
		String lineItemId = Base32Util.decode(lineItem.getEntity().getLineItemId());
		ExecutionResult<RateEntity> result = cartLineItemRateLookupStrategy.getLineItemRate(scope, cartId, lineItemId);

		if (result.getResourceStatus().isSuccessful()) {
			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}
}

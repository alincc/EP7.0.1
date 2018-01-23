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
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.definition.rates.RatesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.rates.integration.PurchaseLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates a link to rates on a purchase lineitem representation.
 */
@Singleton
@Named("linkPurchaseLineItemStrategy")
public class LinkPurchaseLineItemStrategy implements ResourceStateLinkHandler<PurchaseLineItemEntity> {

	private final String resourceServerName;
	private final PurchaseLineItemRateLookupStrategy purchaseLineItemRateLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param purchaseLineItemRateLookupStrategy the line item rate lookup
	 */
	@Inject
	LinkPurchaseLineItemStrategy(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("purchaseLineItemRateLookupStrategy")
			final PurchaseLineItemRateLookupStrategy purchaseLineItemRateLookupStrategy) {

		this.resourceServerName = resourceServerName;
		this.purchaseLineItemRateLookupStrategy = purchaseLineItemRateLookupStrategy;
	}
	

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<PurchaseLineItemEntity> lineItem) {
		String purchaseId = Base32Util.decode(lineItem.getEntity().getPurchaseId());
		String lineItemId = Base32Util.decode(lineItem.getEntity().getLineItemId());

		ExecutionResult<RateEntity> itemRate = purchaseLineItemRateLookupStrategy.getLineItemRate(lineItem.getScope(), purchaseId, lineItemId);

		final Collection<ResourceLink> linksToAdd;
		if (itemRate.getResourceStatus().isSuccessful()) {
			String lineItemUri = ResourceStateUtil.getSelfUri(lineItem);
			String uri = URIUtil.format(resourceServerName, lineItemUri);
			ResourceLink link = ResourceLinkFactory.create(uri,
					RatesMediaTypes.RATE.id(), RateRepresentationRels.RATE_REL,
					RateRepresentationRels.LINE_ITEM_REV);

			linksToAdd = Collections.singleton(link);
		} else {
			linksToAdd = Collections.emptyList();
		}

		return linksToAdd;
	}

}
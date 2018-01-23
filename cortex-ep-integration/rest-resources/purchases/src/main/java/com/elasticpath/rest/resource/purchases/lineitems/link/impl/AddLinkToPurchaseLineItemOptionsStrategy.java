/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Strategy to add link to options on a purchase line item.
 */
@Singleton
@Named("addLinkToPurchaseLineItemOptionsStrategy")
public final class AddLinkToPurchaseLineItemOptionsStrategy implements ResourceStateLinkHandler<PurchaseLineItemEntity> {

	private final PurchaseLineItemOptionsLookup purchaseLinkItemOptionsLookup;


	/**
	 * Default constructor.
	 *
	 * @param purchaseLinkItemOptionsLookup the purchase link item options lookup
	 */
	@Inject
	public AddLinkToPurchaseLineItemOptionsStrategy(
			@Named("purchaseLineItemOptionsLookup")
			final PurchaseLineItemOptionsLookup purchaseLinkItemOptionsLookup) {

		this.purchaseLinkItemOptionsLookup = purchaseLinkItemOptionsLookup;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<PurchaseLineItemEntity> representation) {

		PurchaseLineItemEntity purchaseLineItemEntity = representation.getEntity();
		String scope = representation.getScope();
		String purchaseId = purchaseLineItemEntity.getPurchaseId();
		String lineItemId = purchaseLineItemEntity.getLineItemId();

		ExecutionResult<Collection<String>> findOptionsResult;

		try {
			findOptionsResult = purchaseLinkItemOptionsLookup
					.findOptionIdsForLineItem(scope, purchaseId, lineItemId);
		} catch (BrokenChainException bce) {
			return Collections.emptyList();
		}

		final Collection<ResourceLink> result;

		if (CollectionUtil.isEmpty(findOptionsResult.getData())) {
			result = Collections.emptyList();
		} else {
			String lineItemUri = ResourceStateUtil.getSelfUri(representation);
			String optionsUri = URIUtil.format(lineItemUri, Options.URI_PART);
			ResourceLink optionsLink = ResourceLinkFactory.create(optionsUri, CollectionsMediaTypes.LINKS.id(),
					PurchaseLineItemsResourceRels.OPTIONS_REL, PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_REV);
			result = Collections.singleton(optionsLink);
		}

		return result;
	}
}

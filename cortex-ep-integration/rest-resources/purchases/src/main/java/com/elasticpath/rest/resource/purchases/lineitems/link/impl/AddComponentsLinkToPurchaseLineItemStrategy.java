/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Adds a link from the purchase line item to the list of line item components.
 */
@Singleton
@Named("addComponentsLinkToPurchaseLineItemStrategy")
public final class AddComponentsLinkToPurchaseLineItemStrategy implements ResourceStateLinkHandler<PurchaseLineItemEntity> {

	private final PurchaseLineItemLookup purchaseLineItemLookup;


	/**
	 * Constructor for injection.
	 *
	 * @param purchaseLineItemLookup the purchase line item lookup
	 */
	@Inject
	public AddComponentsLinkToPurchaseLineItemStrategy(
			@Named("purchaseLineItemLookup")
			final PurchaseLineItemLookup purchaseLineItemLookup) {

		this.purchaseLineItemLookup = purchaseLineItemLookup;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<PurchaseLineItemEntity> representation) {

		final Collection<ResourceLink> links;

		PurchaseLineItemEntity purchaseLineItemEntity = representation.getEntity();
		String scope = representation.getScope();
		String lineItemId = purchaseLineItemEntity.getLineItemId();
		String purchaseId = purchaseLineItemEntity.getPurchaseId();

		ExecutionResult<Boolean> isBundleResult = purchaseLineItemLookup.isLineItemBundle(scope, purchaseId, lineItemId);

		if (isBundleResult.isSuccessful() && isBundleResult.getData()) {
			String selfUri = ResourceStateUtil.getSelfUri(representation);
			String componentsUri = URIUtil.format(selfUri, Components.URI_PART);
			ResourceLink componentsLink = ResourceLinkFactory.create(componentsUri, CollectionsMediaTypes.LINKS.id(),
					PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_COMPONENTS_REL, PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_REV);
			links = Collections.singleton(componentsLink);
		} else {
			links = Collections.emptyList();
		}

		return links;
	}
}

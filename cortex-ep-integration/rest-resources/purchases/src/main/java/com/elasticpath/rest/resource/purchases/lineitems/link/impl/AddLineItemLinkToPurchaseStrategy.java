/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.purchases.lineitems.LineItems;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Adds a link from the purchase to the list of line items.
 */
@Singleton
@Named("addLineItemLinkToPurchaseStrategy")
public final class AddLineItemLinkToPurchaseStrategy implements ResourceStateLinkHandler<PurchaseEntity> {

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<PurchaseEntity> representation) {

		String uri = URIUtil.format(ResourceStateUtil.getSelfUri(representation), LineItems.URI_PART);
		ResourceLink link = ResourceLinkFactory.create(uri, CollectionsMediaTypes.LINKS.id(),
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEMS_REL, PurchaseResourceRels.PURCHASE_REV);

		return Collections.singleton(link);
	}
}

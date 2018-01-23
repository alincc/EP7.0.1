/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.purchases.addresses.BillingAddress;
import com.elasticpath.rest.resource.purchases.addresses.rel.BillingAddressResourceRels;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Create a link to billing address on the order representation.
 */
@Singleton
@Named("addBillingAddressLinkToPurchaseStrategy")
public final class AddBillingAddressLinkToPurchaseStrategy implements ResourceStateLinkHandler<PurchaseEntity> {

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<PurchaseEntity> representation) {
		String billingAddressUri = URIUtil.format(representation.getSelf().getUri(), BillingAddress.URI_PART);
		ResourceLink link = ResourceLinkFactory.create(billingAddressUri,
				AddressesMediaTypes.ADDRESS.id(), BillingAddressResourceRels.BILLING_ADDRESS_REL, PurchaseResourceRels.PURCHASE_REV);
		return Collections.singleton(link);
	}
}

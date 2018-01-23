/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeansResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Adds links to payment means for a purchase.
 */
@Singleton
@Named("linkPurchasePaymentStrategy")
public final class LinkPurchaseStrategy implements ResourceStateLinkHandler<PurchaseEntity> {

	private final PaymentMeansResourceLinkFactory paymentsMeansLinkFactory;


	/**
	 * Constructor.
	 *
	 * @param paymentsMeansLinkFactory paymentMeans link factory.
	 */
	@Inject
	LinkPurchaseStrategy(
			@Named("paymentMeansResourceLinkFactory")
			final PaymentMeansResourceLinkFactory paymentsMeansLinkFactory) {

		this.paymentsMeansLinkFactory = paymentsMeansLinkFactory;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<PurchaseEntity> purchaseRepresentation) {

		ResourceLink link = paymentsMeansLinkFactory.createPaymentMeansListsLinkForPurchase(ResourceStateUtil.getSelfUri(purchaseRepresentation));
		return Collections.singleton(link);
	}
}

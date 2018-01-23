/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.rel.PurchaseResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates links to the payments sub-resource.
 */
@Singleton
@Named("paymentMeansResourceLinkFactory")
public final class PaymentMeansResourceLinkFactory {

	/**
	 * Create the payments link for purchases.
	 *
	 * @param purchaseUri the purchase URI
	 * @return the link
	 */
	public ResourceLink createPaymentMeansListsLinkForPurchase(final String purchaseUri) {
		return ResourceLink.builderFrom(createPaymentMeansLink(purchaseUri))
				.withRel(PurchaseResourceRels.PAYMENT_MEANS_REL)
				.withRev(PurchaseResourceRels.PURCHASE_REV)
				.build();
	}

	/**
	 * Create the self link for list of payments.
	 *
	 * @param purchaseUri the purchase URI
	 * @return the link
	 */
	public Self createPaymentMeansSelf(final String purchaseUri) {
		String uri = URIUtil.format(purchaseUri, PurchaseResourceRels.PAYMENT_MEANS);
		return SelfFactory.createSelf(uri);
	}

	/**
	 * Create the self link for list of payments.
	 *
	 * @param purchaseUri the purchase URI
	 * @return the link
	 */
	public ResourceLink createPaymentMeansLink(final String purchaseUri) {
		String uri = URIUtil.format(purchaseUri, PurchaseResourceRels.PAYMENT_MEANS);
		return ResourceLinkFactory.createUriType(uri, CollectionsMediaTypes.LINKS.id());
	}

	/**
	 * Create the payments link for purchases.
	 *
	 * @param purchaseUri the purchase URI
	 * @return the link
	 */
	public ResourceLink createPaymentMeansListsLinkForPayment(final String purchaseUri) {
		ResourceLink link = createPaymentMeansLink(purchaseUri);
		return ElementListFactory.createListWithoutElement(link.getUri(), link.getType());
	}

	/**
	 * Create the self link for a payment.
	 *
	 * @param purchaseUri the purchase URI
	 * @param paymentId the payment ID
	 * @param type the type
	 * @return the link
	 */
	public Self createPaymentMeansSelf(final String purchaseUri, final String paymentId, final String type) {
		String uri = URIUtil.format(purchaseUri, PurchaseResourceRels.PAYMENT_MEANS, paymentId);
		return SelfFactory.createSelf(uri, type);
	}

	/**
	 * Create the purchase link for payments.
	 *
	 * @param purchaseUri the URI of the purchase
	 * @return the link
	 */
	public ResourceLink createPurchaseLinkFromPurchaseUri(final String purchaseUri) {
		return ResourceLinkFactory.create(purchaseUri,
				PurchasesMediaTypes.PURCHASE.id(),
				PurchaseResourceRels.PURCHASE_REL,
				PurchaseResourceRels.PAYMENT_MEANS_REV);
	}

	/**
	 * Create the purchase link for payment.
	 *
	 * @param purchaseUri purchase uri
	 * @return the link
	 */
	public ResourceLink createPurchaseLinkForPaymentMeans(final String purchaseUri) {
		return ResourceLinkFactory.createNoRev(purchaseUri, PurchasesMediaTypes.PURCHASE.id(), PurchaseResourceRels.PURCHASE_REL);
	}
}

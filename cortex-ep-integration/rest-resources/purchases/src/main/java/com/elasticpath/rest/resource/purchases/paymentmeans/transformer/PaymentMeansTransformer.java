/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeansResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Assembles the payment means {@link ResourceState}.
 */
@Singleton
@Named("paymentMeansTransformer")
public final class PaymentMeansTransformer {

	private final PaymentMeansResourceLinkFactory paymentMeansResourceLinkFactory;


	/**
	 * Constructor.
	 *
	 * @param paymentMeansResourceLinkFactory the payment means resource link factory
	 */
	@Inject
	PaymentMeansTransformer(
			@Named("paymentMeansResourceLinkFactory")
			final PaymentMeansResourceLinkFactory paymentMeansResourceLinkFactory) {

		this.paymentMeansResourceLinkFactory = paymentMeansResourceLinkFactory;
	}


	/**
	 * Transforms a @{link PaymentMeansEntity} to its associated payment means @{link ResourceState}.
	 *
	 * @param paymentMeansDto the order Payment DTO.
	 * @param paymentMeansId the payment means id
	 * @param purchaseUri the purchase uri
	 * @return a {@link ResourceState}
	 */
	public ResourceState<PaymentMeansEntity> transformToRepresentation(
			final PaymentMeansEntity paymentMeansDto,
			final String paymentMeansId,
			final String purchaseUri) {

		return ResourceState.Builder.create(paymentMeansDto)
				.withSelf(paymentMeansResourceLinkFactory.createPaymentMeansSelf(purchaseUri, paymentMeansId, null))
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.addingLinks(
						paymentMeansResourceLinkFactory.createPaymentMeansListsLinkForPayment(purchaseUri),
						paymentMeansResourceLinkFactory.createPurchaseLinkForPaymentMeans(purchaseUri))
				.build();
	}
}

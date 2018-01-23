/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.resource.commons.handler.registry.PaymentHandlerRegistry;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeansLookup;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeansResourceLinkFactory;
import com.elasticpath.rest.resource.purchases.paymentmeans.integration.PaymentMeansLookupStrategy;
import com.elasticpath.rest.resource.purchases.paymentmeans.transformer.PaymentMeansTransformer;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Look up class for payment means.
 */
@Singleton
@Named("paymentMeansLookup")
public final class PaymentMeansLookupImpl implements PaymentMeansLookup {

	private final PaymentMeansLookupStrategy paymentMeansLookupStrategy;
	private final PaymentMeansTransformer paymentMeansTransformer;
	private final PaymentMeansResourceLinkFactory paymentMeansResourceLinkFactory;
	private final PaymentHandlerRegistry paymentMeansHandlerRegistry;

	/**
	 * Constructor.
	 *
	 * @param paymentMeansLookupStrategy      the purchase means lookup strategy
	 * @param paymentMeansTransformer         payment means assembler
	 * @param paymentMeansResourceLinkFactory payment means resource links
	 * @param paymentMeansHandlerRegistry     the payment means handler registry
	 */
	@Inject
	public PaymentMeansLookupImpl(
			@Named("paymentMeansLookupStrategy")
			final PaymentMeansLookupStrategy paymentMeansLookupStrategy,
			@Named("paymentMeansTransformer")
			final PaymentMeansTransformer paymentMeansTransformer,
			@Named("paymentMeansResourceLinkFactory")
			final PaymentMeansResourceLinkFactory paymentMeansResourceLinkFactory,
			@Named("paymentMeansHandlerRegistry")
			final PaymentHandlerRegistry paymentMeansHandlerRegistry) {

		this.paymentMeansLookupStrategy = paymentMeansLookupStrategy;
		this.paymentMeansTransformer = paymentMeansTransformer;
		this.paymentMeansResourceLinkFactory = paymentMeansResourceLinkFactory;
		this.paymentMeansHandlerRegistry = paymentMeansHandlerRegistry;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> findPaymentMeansIdsByPurchaseId(final String scope, final String purchaseId,
																		final String purchaseUri) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		Collection<PaymentMeansEntity> paymentMeans = Assign.ifSuccessful(paymentMeansLookupStrategy.getPurchasePayments(scope,
				decodedPurchaseId));
		ResourceState<LinksEntity> paymentMeansList = getPaymentMeansLinksRepresentation(purchaseUri, paymentMeans);
		return ExecutionResultFactory.createReadOK(paymentMeansList);
	}

	/**
	 * Creates a links representation from a collection of {@link PaymentMeansEntity}s.
	 *
	 * @param purchaseUri  the purchase URI
	 * @param paymentMeans the collection of payment means
	 * @return the links representation
	 */
	ResourceState<LinksEntity> getPaymentMeansLinksRepresentation(final String purchaseUri, final Collection<PaymentMeansEntity> paymentMeans) {
		Collection<ResourceLink> resourceLinks = new ArrayList<>();
		Self self = paymentMeansResourceLinkFactory.createPaymentMeansSelf(purchaseUri);
		resourceLinks.add(paymentMeansResourceLinkFactory.createPurchaseLinkFromPurchaseUri(purchaseUri));
		for (PaymentMeansEntity paymentMean : paymentMeans) {
			String elementUri = URIUtil.format(self.getUri(), Base32Util.encode(paymentMean.getPaymentMeansId()));

			PaymentHandler paymentMeansHandler = paymentMeansHandlerRegistry.lookupHandler(paymentMean);
			ResourceLink link = ElementListFactory.createElementOfList(elementUri, paymentMeansHandler.representationType());
			resourceLinks.add(link);
		}

		return ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(paymentMeansResourceLinkFactory.createPaymentMeansSelf(purchaseUri))
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(PurchaseResourceConstants.MAX_AGE)
						.build())
				.withLinks(resourceLinks)
				.build();
	}

	@Override
	public ExecutionResult<ResourceState<PaymentMeansEntity>> findPaymentMeansById(
			final String scope, final String purchaseUri, final String purchaseId, final String paymentMeansId) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);
		String decodedPaymentMeansId = Base32Util.decode(paymentMeansId);
		PaymentMeansEntity orderPaymentDto = Assign.ifSuccessful(
				paymentMeansLookupStrategy.getPurchasePayment(scope, decodedPurchaseId, decodedPaymentMeansId));
		ResourceState<PaymentMeansEntity> representation =
				paymentMeansTransformer.transformToRepresentation(orderPaymentDto, paymentMeansId, purchaseUri);
		return ExecutionResultFactory.createReadOK(representation);
	}
}

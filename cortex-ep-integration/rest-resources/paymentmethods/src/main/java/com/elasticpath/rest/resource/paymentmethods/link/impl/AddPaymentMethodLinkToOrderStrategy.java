/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.link.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.rel.NeedInfoRels;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Create a link to payment info on the order representation.
 */
@Singleton
@Named("addPaymentMethodLinkToOrderStrategy")
public final class AddPaymentMethodLinkToOrderStrategy implements ResourceStateLinkHandler<OrderEntity> {

	private final PaymentMethodLookup paymentMethodLookup;
	private final PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory;


	/**
	 * Constructor for injection.
	 *
	 * @param paymentMethodLookup the payment method lookup
	 * @param paymentMethodInfoUriBuilderFactory the payment method info URI builder factory
	 */
	@Inject
	public AddPaymentMethodLinkToOrderStrategy(
			@Named("paymentMethodLookup")
			final PaymentMethodLookup paymentMethodLookup,
			@Named("paymentMethodInfoUriBuilderFactory")
			final PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory) {
		this.paymentMethodLookup = paymentMethodLookup;
		this.paymentMethodInfoUriBuilderFactory = paymentMethodInfoUriBuilderFactory;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<OrderEntity> order) {

		final Collection<ResourceLink> linksToAdd;

		OrderEntity orderEntity = order.getEntity();
		String orderUri = ResourceStateUtil.getSelfUri(order);

		String scope = order.getScope();
		String orderId = orderEntity.getOrderId();

		ExecutionResult<Boolean> isPaymentRequiredResult = paymentMethodLookup.isPaymentRequired(
				scope, orderId);

		String paymentmethodInfoUri = createPaymentMethodInfoUri(orderUri);
		ResourceLink paymentMethodInfoLink = ResourceLinkFactory.create(paymentmethodInfoUri, ControlsMediaTypes.INFO.id(),
				PaymentMethodRels.PAYMENTMETHODINFO_REL, PaymentMethodRels.ORDER_REV);
		linksToAdd = new ArrayList<>(2);
		linksToAdd.add(paymentMethodInfoLink);

		if (isPaymentRequiredResult.isSuccessful() && isPaymentRequiredResult.getData()) {
			ExecutionResult<Boolean> isPaymentMethodSelectedForOrderResult =
					paymentMethodLookup.isPaymentMethodSelectedForOrder(scope, orderId);
			if (isPaymentMethodSelectedForOrderResult.isSuccessful() && !isPaymentMethodSelectedForOrderResult.getData()) {
				ResourceLink needInfoLink = ResourceLinkFactory.createNoRev(paymentmethodInfoUri,
						ControlsMediaTypes.INFO.id(), NeedInfoRels.NEEDINFO);
				linksToAdd.add(needInfoLink);
		}
	}

		return linksToAdd;
	}

	/**
	 * Creates a payment method info URI from the given order URI.
	 * @param orderUri the order URI
	 * @return the payment method info URI
	 */
	String createPaymentMethodInfoUri(final String orderUri) {
		return paymentMethodInfoUriBuilderFactory.get()
				.setSourceUri(orderUri)
				.build();
	}
}

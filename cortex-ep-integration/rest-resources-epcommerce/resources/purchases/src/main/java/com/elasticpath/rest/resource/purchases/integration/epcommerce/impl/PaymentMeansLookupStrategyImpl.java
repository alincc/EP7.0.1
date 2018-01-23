/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.domain.wrapper.OrderPaymentWrapper;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.OrderPaymentTransformer;
import com.elasticpath.rest.resource.purchases.paymentmeans.integration.PaymentMeansLookupStrategy;

/**
 * EP Commerce implementation of the purchase payment lookup strategy.
 */
@Singleton
@Named("paymentMeansLookupStrategy")
public class PaymentMeansLookupStrategyImpl implements PaymentMeansLookupStrategy {

	private static final String NO_BILLING_ADDRESS_FOR_PAYMENT_MEANS = "No billing address for PaymentMeans with GUID %s was found in store %s";
	private static final String NO_PAYMENT_MEANS_FOUND = "No PaymentMeans with GUID %s was found in store %s";

	private static final Comparator<OrderPayment> ORDER_PAYMENT_COMPARATOR = new Comparator<OrderPayment>() {

		@Override
		public int compare(final OrderPayment order1, final OrderPayment order2) {
			return order1.getLastModifiedDate().compareTo(order2.getLastModifiedDate());
		}
	};

	private final OrderRepository orderRepository;
	private final OrderPaymentTransformer orderPaymentTransformer;

	/**
	 * Constructor.
	 *
	 * @param orderRepository         the order repository
	 * @param orderPaymentTransformer the purchase payment transformer
	 */
	@Inject
	public PaymentMeansLookupStrategyImpl(
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("orderPaymentTransformer")
			final OrderPaymentTransformer orderPaymentTransformer) {

		this.orderRepository = orderRepository;
		this.orderPaymentTransformer = orderPaymentTransformer;
	}

	@Override
	public ExecutionResult<PaymentMeansEntity> getPurchasePayment(final String storeCode, final String orderGuid, final String orderPaymentUid) {

		Long orderPaymentUidLong = Assign.ifNotNull(parseLong(orderPaymentUid),
				OnFailure.returnNotFound("Failed to get payment means."));

		Order order = Assign.ifSuccessful(orderRepository.findByGuid(storeCode, orderGuid));
		Collection<OrderPayment> orderPayments = order.getOrderPayments();

		OrderPayment requestedOrderPayment = null;
		for (OrderPayment orderPayment : orderPayments) {
			if (orderPayment.getUidPk() == orderPaymentUidLong) {
				requestedOrderPayment = orderPayment;
				break;
			}
		}
		Ensure.notNull(requestedOrderPayment,
				OnFailure.returnNotFound(NO_PAYMENT_MEANS_FOUND, orderPaymentUid, storeCode));
		OrderAddress billingAddress = Assign.ifNotNull(order.getBillingAddress(),
				OnFailure.returnNotFound(NO_BILLING_ADDRESS_FOR_PAYMENT_MEANS, orderPaymentUid, storeCode));

		return getPaymentMeanForPurchasePayment(requestedOrderPayment, billingAddress);
	}

	@Override
	public ExecutionResult<Collection<PaymentMeansEntity>> getPurchasePayments(final String storeCode, final String orderGuid) {

		Order order = Assign.ifSuccessful(orderRepository.findByGuid(storeCode, orderGuid));
		List<OrderPayment> orderPayments = new ArrayList<>(order.getOrderPayments());
		Collections.sort(orderPayments, ORDER_PAYMENT_COMPARATOR);
		Collection<PaymentMeansEntity> paymentMeansForOrder = new ArrayList<>(orderPayments.size());
		for (OrderPayment orderPayment : orderPayments) {
			if (OrderPayment.AUTHORIZATION_TRANSACTION.equals(orderPayment.getTransactionType())) {
				PaymentMeansEntity paymentMeansDto = Assign.ifSuccessful(getPaymentMeanForPurchasePayment(orderPayment,
						order.getBillingAddress()));

				paymentMeansForOrder.add(paymentMeansDto);
				break;
			}
		}
		return ExecutionResultFactory.createReadOK(paymentMeansForOrder);
	}

	private ExecutionResult<PaymentMeansEntity> getPaymentMeanForPurchasePayment(final OrderPayment orderPayment, final OrderAddress billingAddress) {

		OrderPaymentWrapper orderPaymentWrapper = ResourceTypeFactory.createResourceEntity(OrderPaymentWrapper.class)
				.setOrderPayment(orderPayment)
				.setOrderAddress(billingAddress);
		PaymentMeansEntity orderPaymentDto = orderPaymentTransformer.transformToEntity(orderPaymentWrapper);
		return ExecutionResultFactory.createReadOK(orderPaymentDto);

	}

	private Long parseLong(final String orderPaymentUid) {
		Long orderPaymentUidLong;
		try {
			orderPaymentUidLong = Long.valueOf(orderPaymentUid);
		} catch (NumberFormatException e) {
			orderPaymentUidLong = null;
		}
		return orderPaymentUidLong;
	}
}

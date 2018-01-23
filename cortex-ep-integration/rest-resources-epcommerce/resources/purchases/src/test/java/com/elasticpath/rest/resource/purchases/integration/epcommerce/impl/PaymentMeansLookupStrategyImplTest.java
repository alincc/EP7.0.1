/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PaymentMeansCreditCardEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.domain.wrapper.OrderPaymentWrapper;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.OrderPaymentTransformer;

/**
 * Test for {@link PaymentMeansLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentMeansLookupStrategyImplTest {

	private static final String STORE_CODE = "STORE CODE";
	private static final String ORDER_GUID = "ORDER_GUID";
	private static final String INVALID_PURCHASE_ID = "INVALID_PURCHASE_ID";
	private static final long ORDER_PAYMENT_UIDPK = 123456;
	private static final long ALTERNATE_ORDER_PAYMENT_UIDPK = 654321;
	private static final String ORDER_PAYMENT_UID_LONG = Long.toString(ORDER_PAYMENT_UIDPK);
	private static final String NON_NUMERIC_PURCHASE_PAYMENT_ID = "NON_NUMERIC_PURCHASE_PAYMENT_ID";
	private static final String INVALID_PURCHASE_PAYMENT_ID = "654321";
	private static final long OFFSET = 3600;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private OrderPaymentTransformer orderPaymentTransformer;

	@InjectMocks
	private PaymentMeansLookupStrategyImpl paymentMeansLookupStrategy;

	/**
	 * Test successful get purchase payment.
	 */
	@Test
	public void testSuccessfulGetPurchasePayment() {
		OrderPayment orderPayment = createOrderPayment(ORDER_PAYMENT_UIDPK, null, null);
		Set<OrderPayment> orderPayments = Collections.singleton(orderPayment);
		OrderAddress orderBillingAddress = new OrderAddressImpl();
		Order order = createOrder(orderPayments, orderBillingAddress);
		OrderPaymentWrapper orderPaymentWrapper = ResourceTypeFactory.createResourceEntity(OrderPaymentWrapper.class)
				.setOrderPayment(orderPayment)
				.setOrderAddress(orderBillingAddress);
		PaymentMeansCreditCardEntity paymentMeansDto = ResourceTypeFactory.createResourceEntity(PaymentMeansCreditCardEntity.class);

		shouldFindByGuidWithResult(ORDER_GUID, ExecutionResultFactory.createCreateOKWithData(order, false));
		shouldTransformToEntity(orderPaymentWrapper, paymentMeansDto);

		ExecutionResult<PaymentMeansEntity> result = paymentMeansLookupStrategy.getPurchasePayment(STORE_CODE,
				ORDER_GUID,
				ORDER_PAYMENT_UID_LONG);

		assertTrue("This should be a successful operation.", result.isSuccessful());
		assertEquals("The data payload should be as expected.", paymentMeansDto, result.getData());
	}

	/**
	 * Test get purchase payment with order billing address set to null.
	 */
	@Test
	public void testGetPurchasePaymentWithNoOrderBillingAddress() {
		OrderPayment orderPayment = createOrderPayment(ORDER_PAYMENT_UIDPK, null, null);
		Set<OrderPayment> orderPayments = Collections.singleton(orderPayment);
		Order order = createOrder(orderPayments, null);

		shouldFindByGuidWithResult(ORDER_GUID, ExecutionResultFactory.createCreateOKWithData(order, false));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMeansLookupStrategy.getPurchasePayment(STORE_CODE,
				ORDER_GUID,
				ORDER_PAYMENT_UID_LONG);
	}

	/**
	 * Test get purchase payment with invalid purchase payment id.
	 */
	@Test
	public void testGetPurchasePaymentWithInvalidPurchasePaymentId() {
		OrderPayment orderPayment = createOrderPayment(ORDER_PAYMENT_UIDPK, null, null);
		Set<OrderPayment> orderPayments = Collections.singleton(orderPayment);
		Order order = createOrder(orderPayments, null);

		shouldFindByGuidWithResult(ORDER_GUID, ExecutionResultFactory.createCreateOKWithData(order, false));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMeansLookupStrategy.getPurchasePayment(STORE_CODE,
				ORDER_GUID,
				INVALID_PURCHASE_PAYMENT_ID);
	}

	/**
	 * Test get purchase payment with order repository find by GUID failure.
	 */
	@Test
	public void testGetPurchasePaymentWithOrderRepositoryFindByGuidFailure() {
		shouldFindByGuidWithResult(INVALID_PURCHASE_ID, ExecutionResultFactory.<Order>createNotFound("Failed to get payment means."));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMeansLookupStrategy.getPurchasePayment(STORE_CODE,
				INVALID_PURCHASE_ID,
				ORDER_PAYMENT_UID_LONG);
	}

	/**
	 * Test get purchase payment with invalid non numeric purchase payment ID.
	 */
	@Test
	public void testGetPurchasePaymentWithInvalidNonNumericPurchasePaymentId() {
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMeansLookupStrategy.getPurchasePayment(STORE_CODE,
				ORDER_GUID,
				NON_NUMERIC_PURCHASE_PAYMENT_ID);
	}

	/**
	 * Test get purchase payment IDs with multiple order payments will pick the first authorization transaction order payment as the only purchase
	 * payment id.
	 */
	@Test
	public void testGetPurchasePaymentIdsWithMultipleOrderPaymentsWillPickTheFirstAuthorizationTransactionOrderPaymentAsTheOnlyPurchasePaymentId() {
		Date lastModifiedDate = new Date();
		OrderPayment orderPayment = createOrderPayment(ORDER_PAYMENT_UIDPK, lastModifiedDate, OrderPayment.REVERSE_AUTHORIZATION);
		OrderPayment orderPaymentWithLaterDate = createOrderPayment(ALTERNATE_ORDER_PAYMENT_UIDPK,
				new Date(lastModifiedDate.getTime() + OFFSET),
				OrderPayment.AUTHORIZATION_TRANSACTION);
		HashSet<OrderPayment> orderPayments = new HashSet<>(Arrays.asList(orderPaymentWithLaterDate, orderPayment));

		OrderAddress orderAddress = new OrderAddressImpl();
		Order order = createOrder(orderPayments, orderAddress);
		shouldFindByGuidWithResult(ORDER_GUID, ExecutionResultFactory.createCreateOKWithData(order, false));

		OrderPaymentWrapper orderPaymentWrapper = ResourceTypeFactory.createResourceEntity(OrderPaymentWrapper.class)
				.setOrderPayment(orderPaymentWithLaterDate)
				.setOrderAddress(orderAddress);

		PaymentMeansEntity expectedPaymentMeansDto = ResourceTypeFactory.createResourceEntity(PaymentMeansCreditCardEntity.class);
		shouldTransformToEntity(orderPaymentWrapper, expectedPaymentMeansDto);

		ExecutionResult<Collection<PaymentMeansEntity>> result = paymentMeansLookupStrategy.getPurchasePayments(STORE_CODE, ORDER_GUID);

		assertTrue("This should be a successful operation.", result.isSuccessful());
		assertThat("The list of payment IDs contain only the first occurring order payment with an AUTHORIZATION_TRANSACTION.",
			result.getData(), Matchers.contains(expectedPaymentMeansDto));
	}

	/**
	 * Test get purchase payment IDs from an order with no order payments.
	 */
	@Test
	public void testGetPurchasePaymentIdsWithNoOrderPayments() {
		Set<OrderPayment> orderPayments = Collections.emptySet();
		Order order = createOrder(orderPayments, null);

		shouldFindByGuidWithResult(ORDER_GUID, ExecutionResultFactory.createCreateOKWithData(order, false));

		ExecutionResult<Collection<PaymentMeansEntity>> result = paymentMeansLookupStrategy.getPurchasePayments(STORE_CODE, ORDER_GUID);

		assertTrue("This should be a successful operation.", result.isSuccessful());
		assertThat("There should be an empty list returned.", result.getData(), Matchers.empty());
	}

	/**
	 * Test get purchase payment IDs with order repository find by GUID failure.
	 */
	@Test
	public void testGetPurchasePaymentIdsWithOrderRepositoryFindByGuidFailure() {
		shouldFindByGuidWithResult(INVALID_PURCHASE_ID, ExecutionResultFactory.<Order>createNotFound("Find by guid error"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		paymentMeansLookupStrategy.getPurchasePayments(STORE_CODE, INVALID_PURCHASE_ID);

	}

	private void shouldFindByGuidWithResult(final String purchaseId, final ExecutionResult<Order> result) {
		when(orderRepository.findByGuid(STORE_CODE, purchaseId)).thenReturn(result);
	}

	private void shouldTransformToEntity(final OrderPaymentWrapper orderPaymentWrapper, final PaymentMeansEntity paymentMeansDto) {
		when(orderPaymentTransformer.transformToEntity(orderPaymentWrapper)).thenReturn(paymentMeansDto);
	}

	private Order createOrder(final Set<OrderPayment> orderPayments, final OrderAddress orderAddress) {
		Order order = new OrderImpl();
		order.setOrderPayments(orderPayments);
		order.setBillingAddress(orderAddress);
		return order;
	}

	private OrderPayment createOrderPayment(final long uidPk, final Date date, final String transactionType) {
		OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		orderPayment.setUidPk(uidPk);
		orderPayment.setLastModifiedDate(date);
		orderPayment.setTransactionType(transactionType);
		return orderPayment;
	}
}

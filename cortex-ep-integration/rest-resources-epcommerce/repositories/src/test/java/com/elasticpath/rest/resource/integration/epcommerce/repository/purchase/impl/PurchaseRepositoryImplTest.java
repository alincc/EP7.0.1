/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.elasticpath.commons.exception.EpValidationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.domain.catalog.AvailabilityException;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformerFactory;
import com.elasticpath.service.shoppingcart.CheckoutService;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseRepositoryImplTest {

	@Mock
	private PaymentMethodTransformerFactory paymentMethodTransformerFactory;
	@Mock
	private CheckoutService checkoutService;
	@Mock
	private ExceptionTransformer exceptionTransformer;

	@InjectMocks
	private PurchaseRepositoryImpl purchaseRepository;

	@Test
	public void testCheckout() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);
		CheckoutResults checkoutResults = mock(CheckoutResults.class);

		when(checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true)).thenReturn(checkoutResults);

		ExecutionResult<CheckoutResults> result = purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment);

		assertTrue(result.isSuccessful());
		assertEquals(checkoutResults, result.getData());
	}

	@Test
	public void testCheckoutException() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);

		when(checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true))
				.thenThrow(new EpServiceException("an exception"));

		ExecutionResult<CheckoutResults> result = purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment);

		assertTrue(result.isFailure());
	}

	@Test
	public void testCheckoutInvalidBusinessStateException() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);

		String productNotAvailableError = "error message";
		StructuredErrorMessage structuredErrorMessage = mock(StructuredErrorMessage.class);
		when(checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true))
				.thenThrow(
						new AvailabilityException(
								productNotAvailableError,
								asList(structuredErrorMessage)
						)

				);
		Message mockMessage = mock(Message.class);
		when(exceptionTransformer.getExecutionResult(any(InvalidBusinessStateException.class))).thenReturn(ExecutionResultFactory
				.createStateFailureWithMessages(productNotAvailableError, singletonList(mockMessage)));

		ExecutionResult<CheckoutResults> result = purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment);

		assertTrue(result.isFailure());
		assertThat(result.getStructuredErrorMessages())
				.containsOnly(mockMessage);
	}

	@Test
	public void testCheckoutEpValidationException() {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		ShoppingCartTaxSnapshot taxSnapshot = mock(ShoppingCartTaxSnapshot.class);
		CustomerSession customerSession = mock(CustomerSession.class);
		OrderPayment orderPayment = mock(OrderPayment.class);

		String validationError = "validation error";
		StructuredErrorMessage structuredErrorMessage = mock(StructuredErrorMessage.class);
		when(checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true))
				.thenThrow(
						new EpValidationException(
								validationError,
								asList(structuredErrorMessage)
						)

				);
		Message mockMessage = mock(Message.class);
		when(exceptionTransformer.getExecutionResult(any(EpValidationException.class))).thenReturn(ExecutionResultFactory
				.createStateFailureWithMessages(validationError, singletonList(mockMessage)));

		ExecutionResult<CheckoutResults> result = purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment);

		assertTrue(result.isFailure());
		assertThat(result.getStructuredErrorMessages())
				.containsOnly(mockMessage);
	}

	@Test
	public void testGetOrderPaymentFromPaymentMethod() {
		PaymentMethod paymentMethod = mock(PaymentMethod.class);
		PaymentMethodTransformer paymentMethodTransformer = mock(PaymentMethodTransformer.class);
		OrderPayment orderPayment = mock(OrderPayment.class);

		when(paymentMethodTransformerFactory.getTransformerInstance(paymentMethod)).thenReturn(paymentMethodTransformer);
		when(paymentMethodTransformer.transformToOrderPayment(paymentMethod)).thenReturn(orderPayment);

		ExecutionResult<OrderPayment> result = purchaseRepository.getOrderPaymentFromPaymentMethod(paymentMethod);

		assertTrue(result.isSuccessful());
		assertEquals(orderPayment, result.getData());
	}

	@Test
	public void testGetOrderPaymentWithInvalidPaymentMethod() {
		PaymentMethod paymentMethod = mock(PaymentMethod.class);

		when(paymentMethodTransformerFactory.getTransformerInstance(paymentMethod)).thenThrow(new IllegalArgumentException());

		ExecutionResult<OrderPayment> result = purchaseRepository.getOrderPaymentFromPaymentMethod(paymentMethod);

		assertTrue(result.isFailure());
	}
}
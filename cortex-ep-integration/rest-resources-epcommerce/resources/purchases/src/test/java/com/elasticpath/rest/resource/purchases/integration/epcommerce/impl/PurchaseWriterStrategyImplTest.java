/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import org.assertj.core.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.CustomerCreditCard;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerCreditCardImpl;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.misc.impl.CheckoutResultsImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderMessageIds;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.PurchaseRepository;
import com.elasticpath.service.payment.PaymentServiceException;

/**
 * Test class for {@link PurchaseWriterStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseWriterStrategyImplTest {
	private static final String STATE_FAILURE = "A state failure should have occurred.";
	private static final String OPERATION_SUCCESS = "This should result in a successful operation.";
	private static final String OPERATION_FAILURE = "This operation should result in a failure.";
	private static final String PAYMENT_METHOD_GUID = "PAYMENT_METHOD_GUID";
	private static final String STORE_CODE = "STORE_CODE";
	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String ORDER_NUMBER = "ORDER_NUMBER";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShoppingCart shoppingCart;
	@Mock
	private ShoppingCartPricingSnapshot pricingSnapshot;
	@Mock
	private ShoppingCartTaxSnapshot taxSnapshot;
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private PurchaseRepository purchaseRepository;
	@Mock
	private CustomerSessionRepository customerSessionRepository;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;
	@Mock
	private CustomerSession customerSession;

	@InjectMocks
	private PurchaseWriterStrategyImpl strategy;

	@Test
	public void testCreatePurchase() {
		OrderPayment orderPayment = mock(OrderPayment.class);
		when(purchaseRepository.getOrderPaymentFromPaymentMethod(any(PaymentMethod.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(orderPayment));
		CartOrder cartOrder = createCartOrder(PAYMENT_METHOD_GUID);
		Order order = createOrder();
		CheckoutResults checkoutResults = createCheckoutResults(order, false, null);

		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, cartOrder))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(customerSession));
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));
		when(purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment))
				.thenReturn(ExecutionResultFactory.createReadOK(checkoutResults));

		ExecutionResult<String> result = strategy.createPurchase(STORE_CODE, CART_ORDER_GUID);

		assertTrue(OPERATION_SUCCESS, result.isSuccessful());
		assertEquals(ORDER_NUMBER, result.getData());
		assertEquals("Order should have been created.", ResourceStatus.CREATE_OK, result.getResourceStatus());
	}

	@Test
	public void testCreatePurchaseWithNullPaymentMethodForZeroCostCart() {
		CartOrder cartOrder = new CartOrderImpl();
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, cartOrder))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));

		final OrderPaymentImpl orderPayment = new OrderPaymentImpl();
		Order order = createOrder();

		when(purchaseRepository.createNewOrderPaymentEntity()).thenReturn(orderPayment);
		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(customerSession));
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));

		CheckoutResults checkoutResults = createCheckoutResults(order, false, null);
		when(purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment))
				.thenReturn(ExecutionResultFactory.createReadOK(checkoutResults));

		ExecutionResult<String> result = strategy.createPurchase(STORE_CODE, CART_ORDER_GUID);

		assertTrue(OPERATION_SUCCESS, result.isSuccessful());
		assertEquals(ORDER_NUMBER, result.getData());
		assertEquals("Order should have been created.", ResourceStatus.CREATE_OK, result.getResourceStatus());
	}

	@Test
	public void testCreatePurchaseWithFailedEnrichedShoppingCart() {
		OrderPayment orderPayment = mock(OrderPayment.class);
		when(purchaseRepository.getOrderPaymentFromPaymentMethod(any(PaymentMethod.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(orderPayment));
		CartOrder cartOrder = createCartOrder(PAYMENT_METHOD_GUID);

		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(customerSession));
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, cartOrder))
				.thenReturn(ExecutionResultFactory.<ShoppingCart>createNotFound("cart not found"));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(pricingSnapshot));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.createPurchase(STORE_CODE, CART_ORDER_GUID);
	}

	@Test
	public void testCreatePurchaseWithFailedCartOrderResult() {
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID))
				.thenReturn(ExecutionResultFactory.<CartOrder>createNotFound("order not found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.createPurchase(STORE_CODE, CART_ORDER_GUID);
	}

	@Test
	public void testCreatePurchaseWithFailedPayment() {
		createPurchaseWithException(new CardErrorException(""));
	}

	@Test
	public void testCreatePurchaseWithPaymentProcessingException() {
		createPurchaseWithException(new PaymentProcessingException("exception"));
	}

	@Test
	public void testCreatePurchaseWithPaymentGatewayException() {
		createPurchaseWithException(new PaymentGatewayException("exception"));
	}

	@Test
	public void testCreatePurchaseWithPaymentServiceException() {
		createPurchaseWithException(new PaymentServiceException("exception"));
	}

	@Test
	public void testCreatePurchaseWithWrappedException() {
		PaymentProcessingException paymentProcessingException = new PaymentProcessingException("payment exception");
		createPurchaseWithException(new EpServiceException("wrapped exception", paymentProcessingException));
	}

	/**
	 * Creates the purchase with the specifically handled exception.
	 *
	 * @param exception the exception
	 */
	public void createPurchaseWithException(final RuntimeException exception) {
		OrderPayment orderPayment = mock(OrderPayment.class);
		when(purchaseRepository.getOrderPaymentFromPaymentMethod(any(PaymentMethod.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(orderPayment));
		CartOrder cartOrder = createCartOrder(PAYMENT_METHOD_GUID);
		Order order = createOrder();
		CheckoutResults checkoutResults = createCheckoutResults(order, true, exception);

		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(customerSession));
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, cartOrder))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));
		when(purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment))
				.thenReturn(ExecutionResultFactory.createReadOK(checkoutResults));

		ExecutionResult<String> result = strategy.createPurchase(STORE_CODE, CART_ORDER_GUID);

		assertEquals(ResourceStatus.STATE_FAILURE, result.getResourceStatus());
	}

	@Test
	public void testCreatePurchaseWithInsufficientInventoryException() {
		OrderPayment orderPayment = mock(OrderPayment.class);
		when(purchaseRepository.getOrderPaymentFromPaymentMethod(any(PaymentMethod.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(orderPayment));
		CartOrder cartOrder = createCartOrder(PAYMENT_METHOD_GUID);
		String errorMessage = "not enough inventory";

		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(customerSession));
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, cartOrder))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));
		when(purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment))
				.thenReturn(ExecutionResultFactory.createStateFailureWithMessages(
						errorMessage,
						asList(
								Message.builder()
										.withId(OrderMessageIds.INSUFFICIENT_INVENTORY)
										.withDebugMessage(errorMessage)
										.withData(ImmutableMap.of())
										.build()
						)
						)
				);

		try {
			strategy.createPurchase(STORE_CODE, CART_ORDER_GUID);
		} catch (final BrokenChainException exception) {
			ExecutionResult<CheckoutResults> executionResult = exception.getBrokenResult();
			Assertions.assertThat(executionResult.getStructuredErrorMessages())
					.extracting(Message::getId)
					.containsOnly(OrderMessageIds.INSUFFICIENT_INVENTORY);
			return;
		}
		fail("Expecting BrokenChainException to be thrown");
	}

	@Test
	public void testCreatePurchaseWithUnhandledCheckoutFailure() {
		OrderPayment orderPayment = mock(OrderPayment.class);
		when(purchaseRepository.getOrderPaymentFromPaymentMethod(any(PaymentMethod.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(orderPayment));
		CartOrder cartOrder = createCartOrder(PAYMENT_METHOD_GUID);
		Order order = createOrder();
		CheckoutResults checkoutResults = createCheckoutResults(order, true, new EpServiceException("checkout failure"));

		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(customerSession));
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, cartOrder))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));
		when(purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment))
				.thenReturn(ExecutionResultFactory.createReadOK(checkoutResults));

		ExecutionResult<String> result = strategy.createPurchase(STORE_CODE, CART_ORDER_GUID);

		assertTrue(OPERATION_FAILURE, result.isFailure());
		assertEquals(STATE_FAILURE, ResourceStatus.STATE_FAILURE, result.getResourceStatus());
	}

	/**
	 * Test create purchase if checkout throws exception.
	 */
	@Test
	public void testCreatePurchaseWithCheckoutThowingInsufficientInventoryException() {
		OrderPayment orderPayment = mock(OrderPayment.class);
		when(purchaseRepository.getOrderPaymentFromPaymentMethod(any(PaymentMethod.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(orderPayment));
		CartOrder cartOrder = createCartOrder(PAYMENT_METHOD_GUID);

		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(customerSession));
		when(cartOrderRepository.findByGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(cartOrder));
		when(cartOrderRepository.getEnrichedShoppingCart(STORE_CODE, cartOrder))
				.thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart)).thenReturn(ExecutionResultFactory.createReadOK(taxSnapshot));
		when(purchaseRepository.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment))
				.thenReturn(ExecutionResultFactory.<CheckoutResults>createStateFailure("state failure"));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		strategy.createPurchase(STORE_CODE, CART_ORDER_GUID);

	}

	private Order createOrder() {
		Order order = new OrderImpl();
		order.setOrderNumber(ORDER_NUMBER);
		return order;
	}

	private CartOrderImpl createCartOrder(final String paymentMethodGuid) {
		CartOrderImpl cartOrder = new CartOrderImpl();
		CustomerCreditCard creditCard = new CustomerCreditCardImpl();
		creditCard.setGuid(paymentMethodGuid);
		cartOrder.usePaymentMethod(creditCard);
		return cartOrder;
	}

	private CheckoutResults createCheckoutResults(final Order order, final boolean orderFailed, final RuntimeException exception) {
		CheckoutResults checkoutResults = new CheckoutResultsImpl();
		checkoutResults.setOrder(order);
		checkoutResults.setOrderFailed(orderFailed);
		checkoutResults.setFailureCause(exception);
		return checkoutResults;
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.PurchaseRepository;
import com.elasticpath.rest.resource.purchases.integration.PurchaseWriterStrategy;

/**
 * EP Commerce implementation of the purchase writer strategy.
 */
@Singleton
@Named("purchaseWriterStrategy")
public class PurchaseWriterStrategyImpl implements PurchaseWriterStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(PurchaseWriterStrategyImpl.class);

	private final CartOrderRepository cartOrderRepository;
	private final PurchaseRepository purchaseRepository;
	private final CustomerSessionRepository customerSessionRepository;
	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository             the cart order repository
	 * @param customerSessionRepository 	  the customer session repository
	 * @param purchaseRepository			  purchase repository
	 * @param pricingSnapshotRepository		  pricing snapshot repository
	 */
	@Inject
	public PurchaseWriterStrategyImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("customerSessionRepository")
			final CustomerSessionRepository customerSessionRepository,
			@Named("purchaseRepository")
			final PurchaseRepository purchaseRepository,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository) {
		this.cartOrderRepository = cartOrderRepository;
		this.customerSessionRepository = customerSessionRepository;
		this.purchaseRepository = purchaseRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public ExecutionResult<String> createPurchase(final String storeCode, final String cartOrderGuid) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		OrderPayment orderPayment = createOrderPaymentFromCartOrder(cartOrder);
		CustomerSession customerSession = Assign.ifSuccessful(customerSessionRepository.findOrCreateCustomerSession());
		ShoppingCart shoppingCart = Assign.ifSuccessful(cartOrderRepository.getEnrichedShoppingCart(storeCode, cartOrder));
		ShoppingCartTaxSnapshot taxSnapshot = Assign.ifSuccessful(pricingSnapshotRepository.getShoppingCartTaxSnapshot(shoppingCart));

		return checkout(shoppingCart, taxSnapshot, customerSession, orderPayment);
	}

	private ExecutionResult<String> checkout(
			final ShoppingCart shoppingCart,
			final ShoppingCartTaxSnapshot taxSnapshot,
			final CustomerSession customerSession,
			final OrderPayment orderPayment) {
		ExecutionResult<String> result;

		CheckoutResults checkoutResult = Assign.ifSuccessful(purchaseRepository.checkout(shoppingCart,
																						taxSnapshot,
																						customerSession,
																						orderPayment));
		if (checkoutResult.isOrderFailed()) {
			result = handleCheckoutFailure(checkoutResult.getFailureCause());
		} else {
			String orderNumber = checkoutResult.getOrder().getGuid();
			result = ExecutionResultFactory.createCreateOKWithData(orderNumber, false);
		}

		return result;
	}

	private ExecutionResult<String> handleCheckoutFailure(final RuntimeException checkoutException) {
		final ExecutionResult<String> result;
		if (checkoutException instanceof EpServiceException) {
			String message = ExceptionUtils.getRootCauseMessage(checkoutException);
			result = ExecutionResultFactory.createStateFailure(message);
		} else {
			LOG.warn("Unexpected checkout error: {}", checkoutException.getMessage());
			LOG.debug("Unexpected checkout error", checkoutException);
			result = ExecutionResultFactory.createStateFailure("The purchase failed: " + checkoutException.getMessage());
		}
		return result;
	}

	private OrderPayment createOrderPaymentFromCartOrder(final CartOrder cartOrder) {
		final OrderPayment result;
		PaymentMethod paymentMethod = cartOrder.getPaymentMethod();
		if (paymentMethod == null) {
			result = createEmptyCreditCardOrderPaymentForZeroTotalPurchase();
		} else {
			result = Assign.ifSuccessful(purchaseRepository.getOrderPaymentFromPaymentMethod(paymentMethod));
		}
		return result;
	}

	private OrderPayment createEmptyCreditCardOrderPaymentForZeroTotalPurchase() {
		OrderPayment orderPayment = purchaseRepository.createNewOrderPaymentEntity();
		orderPayment.setPaymentMethod(PaymentType.CREDITCARD);
		return orderPayment;
	}
}

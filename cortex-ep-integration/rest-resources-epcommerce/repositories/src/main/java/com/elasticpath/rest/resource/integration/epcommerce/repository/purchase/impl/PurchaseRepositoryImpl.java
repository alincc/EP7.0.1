/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.commons.exception.EpValidationException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.PurchaseRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformer;
import com.elasticpath.service.payment.gateway.PaymentMethodTransformerFactory;
import com.elasticpath.service.shoppingcart.CheckoutService;

/**
 * Default implementation.
 */
@Singleton
@Named("purchaseRepository")
public class PurchaseRepositoryImpl implements PurchaseRepository {
	private static final Logger LOG = LoggerFactory.getLogger(PurchaseRepositoryImpl.class);

	private final BeanFactory coreBeanFactory;
	private final PaymentMethodTransformerFactory paymentMethodTransformerFactory;
	private final CheckoutService checkoutService;
	private final ExceptionTransformer exceptionTransformer;

	/**
	 * Default Constructor.
	 *
	 * @param coreBeanFactory                 core bean factory.
	 * @param paymentMethodTransformerFactory payment method transformer factory.
	 * @param checkoutService                 checkout service.
	 * @param exceptionTransformer            the exception transformer.
	 */
	@Inject
	public PurchaseRepositoryImpl(
			@Named("coreBeanFactory")
			final BeanFactory coreBeanFactory,
			@Named("paymentMethodTransformerFactory")
			final PaymentMethodTransformerFactory paymentMethodTransformerFactory,
			@Named("checkoutService")
			final CheckoutService checkoutService,
			@Named("exceptionTransformer")
			final ExceptionTransformer exceptionTransformer) {
		this.coreBeanFactory = coreBeanFactory;
		this.paymentMethodTransformerFactory = paymentMethodTransformerFactory;
		this.checkoutService = checkoutService;
		this.exceptionTransformer = exceptionTransformer;
	}

	@Override
	@CacheResult
	@SuppressWarnings("PMD.AvoidInstanceofChecksInCatchClause")
	public ExecutionResult<CheckoutResults> checkout(final ShoppingCart shoppingCart,
			final ShoppingCartTaxSnapshot taxSnapshot,
			final CustomerSession customerSession,
			final OrderPayment orderPayment) {
		ExecutionResult<CheckoutResults> result;
		try {
			result = ExecutionResultFactory.createReadOK(checkoutService.checkout(
					shoppingCart,
					taxSnapshot,
					customerSession,
					orderPayment,
					true
			));
		} catch (EpSystemException exception) {
			if (exception instanceof InvalidBusinessStateException) {
				result = exceptionTransformer.getExecutionResult((InvalidBusinessStateException) exception);
			} else if (exception instanceof EpValidationException) {
				result = exceptionTransformer.getExecutionResult((EpValidationException) exception);
			} else {
				result = handleCheckoutException(exception);
			}
		} catch (RuntimeException exception) {
			result = handleCheckoutException(exception);
		}
		return result;
	}

	@Override
	@CacheResult
	public ExecutionResult<OrderPayment> getOrderPaymentFromPaymentMethod(final PaymentMethod paymentMethod) {
		PaymentMethodTransformer transformer;
		try {
			transformer = paymentMethodTransformerFactory.getTransformerInstance(paymentMethod);
		} catch (IllegalArgumentException exception) {
			return ExecutionResultFactory.createNotFound("No PaymentMethodTransformer for payment method: " + paymentMethod);
		}

		return ExecutionResultFactory.createReadOK(transformer.transformToOrderPayment(paymentMethod));
	}

	@Override
	public OrderPayment createNewOrderPaymentEntity() {
		return coreBeanFactory.getBean(ContextIdNames.ORDER_PAYMENT);
	}

	private ExecutionResult<CheckoutResults> handleCheckoutException(final RuntimeException checkoutException) {
		final ExecutionResult<CheckoutResults> result;
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
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.domain.wrapper;

import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Wrapper for order payments consisting of {@link OrderPayment} and {@link OrderAddress}.
 */
public interface OrderPaymentWrapper extends ResourceEntity {

	/**
	 * Gets the order payment.
	 *
	 * @return the order payment
	 */
	OrderPayment getOrderPayment();

	/**
	 * Gets the order address.
	 *
	 * @return the order address
	 */
	OrderAddress getOrderAddress();

	/**
	 * Sets the order payment.
	 *
	 * @param orderPayment the order payment
	 * @return this wrapper
	 */
	OrderPaymentWrapper setOrderPayment(OrderPayment orderPayment);

	/**
	 * Sets the order address.
	 *
	 * @param orderAddress the order address
	 * @return this wrapper
	 */
	OrderPaymentWrapper setOrderAddress(OrderAddress orderAddress);
}
/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoWriter;
import com.elasticpath.rest.resource.orders.integration.billinginfo.BillingInfoWriterStrategy;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Write billing information.
 */
@Singleton
@Named("billingInfoWriter")
public final class BillingInfoWriterImpl implements BillingInfoWriter {

	private final BillingInfoWriterStrategy billingInfoWriterStrategy;

	/**
	 * Constructor.
	 *
	 * @param billingInfoWriterStrategy billing info writer strategy
	 */
	@Inject
	BillingInfoWriterImpl(
			@Named("billingInfoWriterStrategy")
			final BillingInfoWriterStrategy billingInfoWriterStrategy) {

		this.billingInfoWriterStrategy = billingInfoWriterStrategy;
	}


	@Override
	public ExecutionResult<Boolean> setAddressForOrder(final String scope, final String orderId, final String addressId) {
		String decodedOrderId = Base32Util.decode(orderId);
		return billingInfoWriterStrategy.setBillingAddress(scope, addressId, decodedOrderId);
	}
}

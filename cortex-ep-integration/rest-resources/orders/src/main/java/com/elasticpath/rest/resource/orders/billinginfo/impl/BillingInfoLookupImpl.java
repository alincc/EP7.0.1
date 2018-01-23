/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoLookup;
import com.elasticpath.rest.resource.orders.integration.billinginfo.BillingInfoLookupStrategy;
import com.elasticpath.rest.id.util.Base32Util;

/**
 * Look up billing information using core.
 */
@Singleton
@Named("billingInfoLookup")
public final class BillingInfoLookupImpl implements BillingInfoLookup {

	private final BillingInfoLookupStrategy billingInfoLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param billingInfoLookupStrategy the billing info lookup strategy
	 */
	@Inject
	public BillingInfoLookupImpl(
			@Named("billingInfoLookupStrategy")
			final BillingInfoLookupStrategy billingInfoLookupStrategy) {

		this.billingInfoLookupStrategy = billingInfoLookupStrategy;
	}

	@Override
	public ExecutionResult<String> findAddressForOrder(final String scope, final String orderId) {
		String decodedOrderId = Base32Util.decode(orderId);
		String addressId = Assign.ifSuccessful(billingInfoLookupStrategy.getBillingAddress(scope, decodedOrderId));
		return ExecutionResultFactory.createReadOK(Base32Util.encode(addressId));
	}
}

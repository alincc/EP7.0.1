/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.PurchaseWriter;
import com.elasticpath.rest.resource.purchases.integration.PurchaseWriterStrategy;

/**
 * Creates purchases using core.
 */
@Singleton
@Named("purchaseWriter")
public final class PurchaseWriterImpl implements PurchaseWriter {

	private final PurchaseWriterStrategy purchaseWriterStrategy;


	/**
	 * Constructor.
	 *
	 * @param purchaseWriterStrategy the purchase writer strategy
	 */
	@Inject
	public PurchaseWriterImpl(
			@Named("purchaseWriterStrategy")
			final PurchaseWriterStrategy purchaseWriterStrategy) {

		this.purchaseWriterStrategy = purchaseWriterStrategy;
	}


	@Override
	public ExecutionResult<String> createPurchase(final String scope, final String orderId) {

		String decodedOrderId = Base32Util.decode(orderId);
		String purchaseId = Assign.ifSuccessful(purchaseWriterStrategy.createPurchase(scope, decodedOrderId));
		return ExecutionResultFactory.createCreateOKWithData(Base32Util.encode(purchaseId), false);
	}
}

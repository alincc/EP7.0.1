/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;


/**
 * The Interface PaymentMeansLookupStrategy.
 */
public interface PaymentMeansLookupStrategy {

	/**
	 * Gets the purchase payment.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedPaymentMeansId the decoded payment means id
	 * @return the purchase payment DTO
	 */
	ExecutionResult<PaymentMeansEntity> getPurchasePayment(String scope, String decodedPurchaseId, String decodedPaymentMeansId);

	/**
	 * Gets the purchase payment IDs.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @return the purchase payment IDs
	 */
	ExecutionResult<Collection<PaymentMeansEntity>> getPurchasePayments(String scope, String decodedPurchaseId);
}

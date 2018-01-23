/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for PaymentMeans on purchases.
 */
public interface PaymentMeansLookup {

	/**
	 * Find the PaymentMeans IDs given a purchase ID.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase ID
	 * @param purchaseUri the parent URI
	 * @return {@link ExecutionResult} collection of PaymentMeans IDs on success
	 */
	ExecutionResult<ResourceState<LinksEntity>> findPaymentMeansIdsByPurchaseId(String scope, String purchaseId, String purchaseUri);

	/**
	 * Find the PaymentMeans given its ID.
	 *
	 *
	 * @param scope the scope
	 * @param purchaseUri the purchase uri
	 * @param purchaseId the purchase ID
	 * @param paymentMeansId the paymentMeans ID
	 * @return {@link ExecutionResult} containing the PaymentMeans Representation.
	 */
	ExecutionResult<ResourceState<PaymentMeansEntity>> findPaymentMeansById(String scope, String purchaseUri,
			String purchaseId, String paymentMeansId);
}

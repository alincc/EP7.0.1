/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.paymentmeans.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeans;
import com.elasticpath.rest.resource.purchases.paymentmeans.PaymentMeansLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * The operator for the payments sub-resource.
 */
@Singleton
@Named("paymentMeansResourceOperator")
@Path({AnyResourceUri.PATH_PART, PaymentMeans.PATH_PART})
public final class PaymentMeansResourceOperatorImpl implements ResourceOperator {

	private final PaymentMeansLookup paymentMeansLookup;

	/**
	 * Constructor.
	 *
	 * @param paymentMeansLookup payment means lookup
	 */
	@Inject
	public PaymentMeansResourceOperatorImpl(
			@Named("paymentMeansLookup")
			final PaymentMeansLookup paymentMeansLookup) {

		this.paymentMeansLookup = paymentMeansLookup;
	}

	/**
	 * Process READ operation on a list of Purchase payment means.
	 *
	 * @param purchase the purchase.
	 * @param operation the Resource Operation.
	 * @return the Operation result with Purchases form.
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processListOfPaymentMeansRead(
			@AnyResourceUri
			final ResourceState<PurchaseEntity> purchase,
			final ResourceOperation operation) {

		String scope = purchase.getScope();
		String purchaseId = purchase.getEntity().getPurchaseId();
		String purchaseUri = ResourceStateUtil.getSelfUri(purchase);
		ExecutionResult<ResourceState<LinksEntity>> result = paymentMeansLookup.findPaymentMeansIdsByPurchaseId(scope, purchaseId, purchaseUri);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process READ operation on a single payment means.
	 *
	 * @param purchase the purchase.
	 * @param paymentMeansId the payment id
	 * @param operation the Resource Operation.
	 * @return the Operation result with Purchases form.
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processPaymentMeansRead(
			@AnyResourceUri
			final ResourceState<PurchaseEntity> purchase,
			@ResourceId
			final String paymentMeansId,
			final ResourceOperation operation) {

		String scope = purchase.getScope();
		String purchaseUri = ResourceStateUtil.getSelfUri(purchase);
		String purchaseId = purchase.getEntity().getPurchaseId();

		ExecutionResult<ResourceState<PaymentMeansEntity>> result =
				paymentMeansLookup.findPaymentMeansById(scope, purchaseUri, purchaseId, paymentMeansId);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

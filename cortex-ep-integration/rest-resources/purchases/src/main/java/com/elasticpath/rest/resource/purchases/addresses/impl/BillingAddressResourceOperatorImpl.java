/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.purchases.addresses.BillingAddress;
import com.elasticpath.rest.resource.purchases.addresses.PurchaseBillingAddressLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The operator for the payments sub-resource.
 */
@Singleton
@Named("billingAddressResourceOperator")
@Path({AnyResourceUri.PATH_PART, BillingAddress.PATH_PART})
public final class BillingAddressResourceOperatorImpl implements ResourceOperator {

	private final PurchaseBillingAddressLookup purchaseBillingAddressLookup;

	/**
	 * Constructor.
	 *
	 * @param purchaseBillingAddressLookup the billing address lookup
	 */
	@Inject
	BillingAddressResourceOperatorImpl(
			@Named("purchaseBillingAddressLookup")
			final PurchaseBillingAddressLookup purchaseBillingAddressLookup) {
		this.purchaseBillingAddressLookup = purchaseBillingAddressLookup;
	}

	/**
	 * Process READ operation on a purchase's billing address.
	 *
	 * @param purchase the purchase.
	 * @param operation the Resource Operation.
	 * @return the Operation result with Purchases form.
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processBillingAddressRead(
			@AnyResourceUri
			final ResourceState<PurchaseEntity> purchase,
			final ResourceOperation operation) {

		String scope = purchase.getScope();
		String purchaseId = purchase.getEntity().getPurchaseId();

		ExecutionResult<ResourceState<AddressEntity>> result = purchaseBillingAddressLookup.getBillingAddress(scope, purchaseId);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

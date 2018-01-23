/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.purchases.addresses.PurchaseBillingAddressLookup;
import com.elasticpath.rest.resource.purchases.addresses.integration.BillingAddressLookupStrategy;
import com.elasticpath.rest.resource.purchases.addresses.transform.PurchaseBillingAddressTransformer;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Lookup for a purchase billing address.
 */
@Singleton
@Named("purchaseBillingAddressLookup")
public final class PurchaseBillingAddressLookupImpl implements PurchaseBillingAddressLookup {

	private final BillingAddressLookupStrategy billingAddressLookupStrategy;
	private final PurchaseBillingAddressTransformer purchaseBillingAddressTransformer;


	/**
	 * Default constructor.
	 *
	 * @param billingAddressLookupStrategy      the lookup strategy
	 * @param purchaseBillingAddressTransformer the transformer
	 */
	@Inject
	PurchaseBillingAddressLookupImpl(
			@Named("billingAddressLookupStrategy")
			final BillingAddressLookupStrategy billingAddressLookupStrategy,
			@Named("purchaseBillingAddressTransformer")
			final PurchaseBillingAddressTransformer purchaseBillingAddressTransformer) {

		this.billingAddressLookupStrategy = billingAddressLookupStrategy;
		this.purchaseBillingAddressTransformer = purchaseBillingAddressTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<AddressEntity>> getBillingAddress(final String scope, final String purchaseId) {

		String decodedPurchaseId = Base32Util.decode(purchaseId);

		AddressEntity addressEntity = Assign.ifSuccessful(billingAddressLookupStrategy.getBillingAddress(scope, decodedPurchaseId));
		ResourceState<AddressEntity> representation =
				purchaseBillingAddressTransformer.transformToRepresentation(scope, purchaseId, addressEntity);
		return ExecutionResultFactory.createReadOK(representation);
	}
}

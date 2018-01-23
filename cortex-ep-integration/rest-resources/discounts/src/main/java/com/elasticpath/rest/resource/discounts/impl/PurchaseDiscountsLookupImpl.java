/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.discounts.PurchaseDiscountsLookup;
import com.elasticpath.rest.resource.discounts.integration.PurchaseDiscountsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Look up for discounts on purchases.
 */
@Singleton
@Named("purchaseDiscountsLookup")
public final class PurchaseDiscountsLookupImpl implements PurchaseDiscountsLookup {

	private final TransformRfoToResourceState<DiscountEntity, DiscountEntity, PurchaseEntity> discountPurchaseTransformer;
	private final PurchaseDiscountsLookupStrategy purchaseDiscountsLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param discountPurchaseTransformer the discount purchase transformer
	 * @param purchaseDiscountsLookupStrategy the purchase discounts lookup strategy
	 */
	@Inject
	public PurchaseDiscountsLookupImpl(
			@Named("discountsPurchaseTransformer")
			final TransformRfoToResourceState<DiscountEntity, DiscountEntity, PurchaseEntity> discountPurchaseTransformer,
			@Named("purchaseDiscountsLookupStrategy")
			final PurchaseDiscountsLookupStrategy purchaseDiscountsLookupStrategy) {
		this.discountPurchaseTransformer = discountPurchaseTransformer;
		this.purchaseDiscountsLookupStrategy = purchaseDiscountsLookupStrategy;
	}

	@Override
	public ExecutionResult<ResourceState<DiscountEntity>> getPurchaseDiscounts(
			final ResourceState<PurchaseEntity> purchaseRepresentation) {

		PurchaseEntity purchaseEntity = purchaseRepresentation.getEntity();
		String purchaseId = purchaseEntity.getPurchaseId();
		String scope = purchaseRepresentation.getScope();
		final String purchaseIdDecoded = Base32Util.decode(purchaseId);

		DiscountEntity purchaseDiscount = Assign.ifSuccessful(purchaseDiscountsLookupStrategy.getPurchaseDiscounts(purchaseIdDecoded, scope));

		ResourceState<DiscountEntity> discountRepresentation = discountPurchaseTransformer.transform(purchaseDiscount,
				purchaseRepresentation);
		return ExecutionResultFactory.createReadOK(discountRepresentation);
	}

}

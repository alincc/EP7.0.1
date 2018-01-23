/**
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.discounts.CartDiscountsLookup;
import com.elasticpath.rest.resource.discounts.PurchaseDiscountsLookup;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operations on Discounts.
 */
@Singleton
@Named("discountsResourceOperator")
@Path(ResourceName.PATH_PART)
public class DiscountsResourceOperatorImpl implements ResourceOperator {

	private final CartDiscountsLookup cartsDiscountsLookup;
	private final PurchaseDiscountsLookup purchaseDiscountLookup;

	/**
	 * Constructor.
	 *
	 * @param cartsDiscountsLookup   the {@link com.elasticpath.rest.resource.discounts.CartDiscountsLookup}
	 * @param purchaseDiscountLookup the {@link com.elasticpath.rest.resource.discounts.PurchaseDiscountsLookup}
	 */
	@SuppressWarnings("PMD.LooseCoupling")
	@Inject
	public DiscountsResourceOperatorImpl(
			@Named("cartDiscountsLookup")
			final CartDiscountsLookup cartsDiscountsLookup,
			@Named("purchaseDiscountsLookup")
			final PurchaseDiscountsLookup purchaseDiscountLookup) {

		this.cartsDiscountsLookup = cartsDiscountsLookup;
		this.purchaseDiscountLookup = purchaseDiscountLookup;
	}

	/**
	 * Handles the READ operation for the discount.
	 *
	 * @param otherRepresentation the other {@link com.elasticpath.rest.schema.ResourceState}
	 * @param operation           the resource operation
	 * @return the operation result
	 */
	@Path({AnyResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadForCart(
			@AnyResourceUri
			final ResourceState<CartEntity> otherRepresentation,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<DiscountEntity>> result = cartsDiscountsLookup.getCartDiscounts(otherRepresentation);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles the READ operation for the discount.
	 *
	 * @param otherRepresentation the other {@link com.elasticpath.rest.schema.ResourceState}
	 * @param operation           the resource operation
	 * @return the operation result
	 */
	@Path({AnyResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadForPurchase(
			@AnyResourceUri
			final ResourceState<PurchaseEntity> otherRepresentation,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<DiscountEntity>> result = purchaseDiscountLookup.getPurchaseDiscounts(otherRepresentation);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.purchases.PurchaseLookup;
import com.elasticpath.rest.resource.purchases.PurchaseWriter;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Resource Operator for creating a Purchases.
 */
@Singleton
@Named("createPurchaseForOrderResourceOperator")
@Path(ResourceName.PATH_PART)
public final class CreatePurchaseForOrderResourceOperator implements ResourceOperator {

	private final String resourceServerName;
	private final PurchaseLookup purchaseLookup;
	private final PurchaseWriter purchaseWriter;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName resource server name
	 * @param purchaseLookup     purchase lookup
	 * @param purchaseWriter     purchase writer
	 */
	@Inject
	CreatePurchaseForOrderResourceOperator(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("purchaseLookup")
			final PurchaseLookup purchaseLookup,
			@Named("purchaseWriter")
			final PurchaseWriter purchaseWriter) {

		this.resourceServerName = resourceServerName;
		this.purchaseLookup = purchaseLookup;
		this.purchaseWriter = purchaseWriter;
	}


	/**
	 * Process CREATE operation.
	 *
	 * @param order the order representation
	 * @param operation the resource operation
	 * @return the {@link com.elasticpath.rest.OperationResult} with the newly created {@link com.elasticpath.rest.schema.ResourceState}
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.CREATE)
	public OperationResult processCreatePurchase(
			@AnyResourceUri(readLinks = true)
			final ResourceState<OrderEntity> order,
			final ResourceOperation operation) {

		String scope = order.getScope();
		String orderId = order.getEntity().getOrderId();
		boolean isPurchasable = Assign.ifSuccessful(purchaseLookup.isOrderPurchasable(scope, orderId));
		Ensure.isTrue(isPurchasable,
				OnFailure.returnStateFailure("The order %s is not purchasable", orderId));
		String purchaseId = Assign.ifSuccessful(purchaseWriter.createPurchase(scope, orderId));
		String selfUri = URIUtil.format(resourceServerName, scope, purchaseId);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(
				ExecutionResultFactory.createCreateOK(selfUri, false),
				operation);
	}
}

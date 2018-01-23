/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.purchases.lineitems.LineItemId;
import com.elasticpath.rest.resource.purchases.lineitems.LineItems;
import com.elasticpath.rest.resource.purchases.lineitems.command.ReadPurchaseLineItemResourceCommand;
import com.elasticpath.rest.resource.purchases.lineitems.command.ReadPurchaseLineItemsCommand;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Resource Operator for Purchases.
 */
@Singleton
@Named("purchaseLineItemsOperator")
@Path(ResourceName.PATH_PART)
public final class PurchaseLineItemsOperatorImpl implements ResourceOperator {

	private final Provider<ReadPurchaseLineItemResourceCommand.Builder> readPurchaseLineItemCommandBuilderProvider;
	private final Provider<ReadPurchaseLineItemsCommand.Builder> readPurchaseLineItemsListCommandBuilder;


	/**
	 * Default Constructor.
	 * @param readPurchaseLineItemsListCommandBuilder the read purchase line items command builder.
	 * @param readPurchaseLineItemResourceCommandBuilder the resource command builder for a line item.
	 */
	@Inject
	public PurchaseLineItemsOperatorImpl(
			@Named("readPurchaseLineItemsCommandBuilder")
			final Provider<ReadPurchaseLineItemsCommand.Builder> readPurchaseLineItemsListCommandBuilder,
			@Named("readPurchaseLineItemResourceCommandBuilder")
			final Provider<ReadPurchaseLineItemResourceCommand.Builder> readPurchaseLineItemResourceCommandBuilder) {

		this.readPurchaseLineItemsListCommandBuilder = readPurchaseLineItemsListCommandBuilder;
		this.readPurchaseLineItemCommandBuilderProvider = readPurchaseLineItemResourceCommandBuilder;
	}


	/**
	 * Process READ operation on the lineitems list for a purchase.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase ID
	 * @param operation the Resource Operation
	 * @return the {@link OperationResult} with a links {@link ResourceState}
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, LineItems.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadLineItemList(
			@Scope
			final String scope,
			@ResourceId
			final String purchaseId,
			final ResourceOperation operation) {

		ReadPurchaseLineItemsCommand readPurchaseLineItemCommand = readPurchaseLineItemsListCommandBuilder.get()
				.setPurchaseId(purchaseId)
				.setScope(scope)
				.build();

		ExecutionResult<ResourceState<LinksEntity>> result = readPurchaseLineItemCommand.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process READ operation on a single line item element for a purchase.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase ID
	 * @param lineItemId the line item id
	 * @param operation the Resource Operation
	 * @return the {@link OperationResult} with a purchase line item {@link ResourceState}
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, LineItems.PATH_PART, LineItemId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadLineItem(
			@Scope
			final String scope,
			@ResourceId
			final String purchaseId,
			@LineItemId
			final String lineItemId,
			final ResourceOperation operation) {

		ReadPurchaseLineItemResourceCommand command = readPurchaseLineItemCommandBuilderProvider.get()
				.setScope(scope)
				.setLineItemId(lineItemId)
				.setPurchaseId(purchaseId)
				.build();

		ExecutionResult<ResourceState<PurchaseLineItemEntity>> result = command.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

}

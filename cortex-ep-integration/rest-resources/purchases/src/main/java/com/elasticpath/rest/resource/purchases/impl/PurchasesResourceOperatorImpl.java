/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.purchases.command.ReadPurchaseListCommand;
import com.elasticpath.rest.resource.purchases.command.ReadPurchaseResourceCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Resource Operator for Purchases.
 */
@Singleton
@Named("purchasesResourceOperator")
@Path(ResourceName.PATH_PART)
public final class PurchasesResourceOperatorImpl implements ResourceOperator {

	private final Provider<ReadPurchaseListCommand.Builder> readPurchaseListCommandProvider;
	private final Provider<ReadPurchaseResourceCommand.Builder> readCommandProvider;

	/**
	 * Constructor.
	 * @param readPurchaseListCommandProvider the read purchase list command provider.
	 * @param readCommandProvider Read Command.
	 */
	@Inject
	PurchasesResourceOperatorImpl(
			@Named("readPurchaseListCommandBuilder")
			final Provider<ReadPurchaseListCommand.Builder> readPurchaseListCommandProvider,
			@Named("readPurchaseResourceCommandBuilder")
			final Provider<ReadPurchaseResourceCommand.Builder> readCommandProvider) {

		this.readPurchaseListCommandProvider = readPurchaseListCommandProvider;
		this.readCommandProvider = readCommandProvider;
	}


	/**
	 * Process READ operation on the list of purchases.
	 *
	 * @param scope the scope
	 * @param operation the Resource Operation
	 * @return the {@link OperationResult} with a {@link ResourceState}
	 */
	@Path(Scope.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadPurchaseList(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		Command<ResourceState<LinksEntity>> command = readPurchaseListCommandProvider.get()
				.setScope(scope)
				.build();

		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process READ operation on Purchases.
	 *
	 * @param scope the scope
	 * @param purchaseId the purchase ID
	 * @param operation the Resource Operation
	 * @return the {@link OperationResult} with a {@link ResourceState}
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processPurchaseRead(
			@Scope
			final String scope,
			@ResourceId
			final String purchaseId,
			final ResourceOperation operation) {

		Command<ResourceState<PurchaseEntity>> command = readCommandProvider.get()
				.setScope(scope)
				.setPurchaseId(purchaseId)
				.build();

		ExecutionResult<ResourceState<PurchaseEntity>> result = command.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

}

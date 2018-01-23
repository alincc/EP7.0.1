/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.emailinfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.orders.emailinfo.EmailInfo;
import com.elasticpath.rest.resource.orders.emailinfo.ReadEmailInfoCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The operator for the {@link EmailInfoResourceOperatorImpl} which is used to process information for an email associated with an order.
 */
@Singleton
@Named("emailInfoResourceOperator")
@Path({SingleResourceUri.PATH_PART, EmailInfo.PATH_PART})
public class EmailInfoResourceOperatorImpl implements ResourceOperator {

	private final Provider<ReadEmailInfoCommand.Builder> readEmailInfoCommandBuilderProvider;

	/**
	 * Constructor.
	 *
	 * @param readEmailInfoCommandBuilderProviders the read email info command builder provider
	 */
	@Inject
	EmailInfoResourceOperatorImpl(
			@Named("readEmailInfoCommandBuilder")
			final Provider<ReadEmailInfoCommand.Builder> readEmailInfoCommandBuilderProviders) {

		this.readEmailInfoCommandBuilderProvider = readEmailInfoCommandBuilderProviders;
	}

	/**
	 * READ email info for order.
	 *
	 * @param orderResourceState the order resource state
	 * @param operation the resource operation
	 * @return the operation result from processing a read.
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadEmailInfo(
			@SingleResourceUri
			final ResourceState<OrderEntity> orderResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<InfoEntity>> result = readEmailInfoCommandBuilderProvider.get().setOrderResourceState(orderResourceState)
				.build().execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.impl;

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
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.resource.orders.deliveries.command.ReadOrderDeliveriesCommand;
import com.elasticpath.rest.resource.orders.deliveries.command.ReadOrderDeliveryCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The {@link DeliveriesResourceOperatorImpl}.
 */
@Singleton
@Named("deliveriesResourceOperator")
@Path({SingleResourceUri.PATH_PART, Deliveries.PATH_PART})
public final class DeliveriesResourceOperatorImpl implements ResourceOperator {

	private final Provider<ReadOrderDeliveriesCommand.Builder> readOrderDeliveriesCommandBuilderProvider;
	private final Provider<ReadOrderDeliveryCommand.Builder> readOrderDeliveryCommandBuilderProvider;

	/**
	 * Default constructor.
	 *
	 * @param readOrderDeliveriesCommandBuilderProvider the read order deliveries command builder provider
	 * @param readOrderDeliveryCommandBuilderProvider the read order delivery command builder provider
	 */
	@Inject
	public DeliveriesResourceOperatorImpl(
			@Named("readOrderDeliveriesCommandBuilder")
			final Provider<ReadOrderDeliveriesCommand.Builder> readOrderDeliveriesCommandBuilderProvider,
			@Named("readOrderDeliveryCommandBuilder")
			final Provider<ReadOrderDeliveryCommand.Builder> readOrderDeliveryCommandBuilderProvider) {

		this.readOrderDeliveriesCommandBuilderProvider = readOrderDeliveriesCommandBuilderProvider;
		this.readOrderDeliveryCommandBuilderProvider = readOrderDeliveryCommandBuilderProvider;
	}

	/**
	 * Process read on order to get list of deliveries.
	 *
	 * @param order the order {@link ResourceState}
	 * @param resourceOperation the resource operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadDeliveries(
			@SingleResourceUri
			final ResourceState<OrderEntity> order,
			final ResourceOperation resourceOperation) {

		Command<ResourceState<LinksEntity>> command = readOrderDeliveriesCommandBuilderProvider.get()
				.setOrder(order)
				.build();

		ExecutionResult<ResourceState<LinksEntity>> result = command.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, resourceOperation);
	}

	/**
	 * Process read to get a specific delivery.
	 *
	 * @param order the order resource state
	 * @param deliveryId the delivery id
	 * @param resourceOperation the resource operation
	 * @return the operation result
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadDelivery(
			@SingleResourceUri
			final ResourceState<OrderEntity> order,
			@ResourceId
			final String deliveryId,
			final ResourceOperation resourceOperation) {

		Command<ResourceState<DeliveryEntity>> command = readOrderDeliveryCommandBuilderProvider.get()
				.setDeliveryId(deliveryId)
				.setOrder(order)
				.build();

		ExecutionResult<ResourceState<DeliveryEntity>> result = command.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, resourceOperation);
	}
}

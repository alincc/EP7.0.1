/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.DestinationInfo;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.ReadDestinationInfoCommand;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.command.ReadDestinationInfoSelectorCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operation on Destination Info sub resource.
 */
@Singleton
@Named("destinationInfoResourceOperator")
@Path(ResourceName.PATH_PART)
public final class DestinationInfoResourceOperatorImpl implements ResourceOperator {

	private final Provider<ReadDestinationInfoCommand.Builder> readDestinationInfCommandBuilderProvider;
	private final Provider<ReadDestinationInfoSelectorCommand.Builder> readDestinationInfoSelectorCommandBuilderProvider;


	/**
	 * Default constructor.
	 * @param readDestinationInfCommandBuilderProvider the read destination info command builder provider
	 * @param readDestinationInfoSelectorCommandBuilderProvider the read destination info selector command builder provider
	 */
	@Inject
	public DestinationInfoResourceOperatorImpl(
			@Named("readDestinationInfoCommandBuilder")
			final Provider<ReadDestinationInfoCommand.Builder> readDestinationInfCommandBuilderProvider,
			@Named("readDestinationInfoSelectorCommandBuilder")
			final Provider<ReadDestinationInfoSelectorCommand.Builder> readDestinationInfoSelectorCommandBuilderProvider) {

		this.readDestinationInfCommandBuilderProvider = readDestinationInfCommandBuilderProvider;
		this.readDestinationInfoSelectorCommandBuilderProvider = readDestinationInfoSelectorCommandBuilderProvider;
	}

	/**
	 * Handles the READ/READ_NOLINKS operations for the Shipments resource.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, DestinationInfo.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadShipmentDestinationInfo(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<InfoEntity>> result = readDestinationInfCommandBuilderProvider.get()
				.setScope(scope)
				.setShipmentDetailsId(shipmentDetailsId)
				.build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}


	/**
	 * Process read shipment destination info selector.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, DestinationInfo.PATH_PART, Selector.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadShipmentDestinationInfoSelector(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<SelectorEntity>> result = readDestinationInfoSelectorCommandBuilderProvider.get()
				.setScope(scope)
				.setShipmentDetailsId(shipmentDetailsId)
				.build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

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
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOption;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionId;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOptionInfo;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionLookup;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.ReadShippingOptionInfoCommand;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.command.ReadShippingOptionSelectorCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operation on Shipping Option Info.
 */
@Singleton
@Named("shippingOptionInfoResourceOperator")
@Path(ResourceName.PATH_PART)
public final class ShippingOptionInfoResourceOperatorImpl implements ResourceOperator {

	private final ShippingOptionLookup shippingOptionLookup;
	private final Provider<ReadShippingOptionSelectorCommand.Builder> readShippingOptionSelectorCommandBuilderProvider;
	private final Provider<ReadShippingOptionInfoCommand.Builder> readShippingOptionInfoCommandBuilderProvider;


	/**
	 * Default constructor.
	 * @param readShippingOptionInfoCommandBuilderProvider the read shipping option info command builder provider
	 * @param shippingOptionLookup the shipping option lookup
	 * @param readShippingOptionSelectorCommandBuilderProvider the read shipping option selector command builder provider
	 */
	@Inject
	public ShippingOptionInfoResourceOperatorImpl(
			@Named("readShippingOptionInfoCommandBuilder")
			final Provider<ReadShippingOptionInfoCommand.Builder> readShippingOptionInfoCommandBuilderProvider,
			@Named("shippingOptionLookup")
			final ShippingOptionLookup shippingOptionLookup,
			@Named("readShippingOptionSelectorCommandBuilder")
			final Provider<ReadShippingOptionSelectorCommand.Builder> readShippingOptionSelectorCommandBuilderProvider) {

		this.readShippingOptionInfoCommandBuilderProvider = readShippingOptionInfoCommandBuilderProvider;
		this.shippingOptionLookup = shippingOptionLookup;
		this.readShippingOptionSelectorCommandBuilderProvider = readShippingOptionSelectorCommandBuilderProvider;
	}


	/**
	 * Process read shipping option info.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, ShippingOptionInfo.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadShippingOptionInfo(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<InfoEntity>> result = readShippingOptionInfoCommandBuilderProvider.get()
				.setScope(scope)
				.setShipmentDetailsId(shipmentDetailsId)
				.build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process read shipping option info selector.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, ShippingOptionInfo.PATH_PART, Selector.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadShippingOptionInfoSelector(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<SelectorEntity>> result = readShippingOptionSelectorCommandBuilderProvider.get()
				.setScope(scope)
				.setShipmentDetailsId(shipmentDetailsId)
				.build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}


	/**
	 * Process read shipping option.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @param shippingOptionId the shipping option id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, ResourceId.PATH_PART, ShippingOption.PATH_PART, ShippingOptionId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadShippingOption(
			@Scope
			final String scope,
			@ResourceId
			final String shipmentDetailsId,
			@ShippingOptionId
			final String shippingOptionId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ShippingOptionEntity>> result = shippingOptionLookup.getShippingOption(scope, shipmentDetailsId,
				shippingOptionId);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingAddressChoiceCommand;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingAddressSelectorCommand;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingInfoCommand;
import com.elasticpath.rest.resource.orders.billinginfo.command.SelectBillingInfoCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Operator for billing info sub-resource.
 */
@Singleton
@Named("billingInfoResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, ResourceId.PATH_PART, BillingAddressInfo.PATH_PART})
public final class BillingInfoResourceOperatorImpl implements ResourceOperator {

	private final Provider<ReadBillingAddressSelectorCommand.Builder> readBillingAddressSelectorCommandBuilder;
	private final Provider<SelectBillingInfoCommand.Builder> selectBillingInfoCommandBuilder;
	private final Provider<ReadBillingAddressChoiceCommand.Builder> readBillingAddressChoiceCommandBuilder;
	private final Provider<ReadBillingInfoCommand.Builder> readBillingInfoCommandBuilderProvider;


	/**
	 * Constructor.
	 *
	 * @param readBillingAddressSelectorCommandBuilder Read Billing Address List.
	 * @param readBillingInfoCommandBuilderProvider the read billing info command builder provider
	 * @param selectBillingInfoCommandBuilder Select Billing Info.
	 * @param readBillingAddressChoiceCommandBuilder the read billing address command builder
	 */
	@Inject
	public BillingInfoResourceOperatorImpl(
			@Named("readBillingInfoSelectorCommandBuilder")
			final Provider<ReadBillingAddressSelectorCommand.Builder> readBillingAddressSelectorCommandBuilder,
			@Named("readBillingInfoCommandBuilder")
			final Provider<ReadBillingInfoCommand.Builder> readBillingInfoCommandBuilderProvider,
			@Named("selectBillingInfoCommandBuilder")
			final Provider<SelectBillingInfoCommand.Builder> selectBillingInfoCommandBuilder,
			@Named("readBillingAddressChoiceCommandBuilder")
			final Provider<ReadBillingAddressChoiceCommand.Builder> readBillingAddressChoiceCommandBuilder) {

		this.readBillingAddressSelectorCommandBuilder = readBillingAddressSelectorCommandBuilder;
		this.readBillingInfoCommandBuilderProvider = readBillingInfoCommandBuilderProvider;
		this.selectBillingInfoCommandBuilder = selectBillingInfoCommandBuilder;
		this.readBillingAddressChoiceCommandBuilder = readBillingAddressChoiceCommandBuilder;
	}


	/**
	 * READ billing info for order.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @param operation the ResourceOperation
	 * @return the operation result with the order's billing info
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadBillingAddressInfo(
			@Scope
			final String scope,
			@ResourceId
			final String orderId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<InfoEntity>> result = readBillingInfoCommandBuilderProvider.get().setOrderId(orderId).setScope(scope).build()
				.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * READ billing info selector for order.
	 *
	 * @param scope the order scope
	 * @param orderId the order ID
	 * @param billingAddressLinks the (@link ResourceState<LinksEntity>} representing the billing address links
	 * @param operation the ResourceOperation
	 * @return the operation result with the order's billing info
	 */
	@Path({ Selector.PATH_PART, AnyResourceUri.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processReadBillingAddressSelector(
			@Scope
			final String scope,
			@ResourceId
			final String orderId,
			@AnyResourceUri
			final ResourceState<LinksEntity> billingAddressLinks,
			final ResourceOperation operation) {

		Command<ResourceState<SelectorEntity>> cmd = readBillingAddressSelectorCommandBuilder.get().setScope(scope).setOrderId(orderId)
				.setBillingAddressLinks(billingAddressLinks).build();

		ExecutionResult<ResourceState<SelectorEntity>> result = cmd.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process selection of order billing address link.
	 *
	 * @param scope the scope
	 * @param orderId the order ID
	 * @param billingAddress the billing address resource state
	 * @param operation the Resource Operation
	 * @return the operation result with the order's billing info
	 */
	@Path({Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processSelectBillingInfo(
			@Scope
			final String scope,
			@ResourceId
			final String orderId,
			@AnyResourceUri
			final ResourceState<AddressEntity> billingAddress,
			final ResourceOperation operation) {

		Command<ResourceState<ResourceEntity>> cmd = selectBillingInfoCommandBuilder.get().setOrderId(orderId).setScope(scope)
				.setBillingAddress(billingAddress)
				.build();
		ExecutionResult<ResourceState<ResourceEntity>> result = cmd.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process read of order billing address link.
	 *
	 * @param scope the scope
	 * @param billingAddress the billing address {@link ResourceState}
	 * @param orderId the order id
	 * @param operation the Resource Operation
	 * @return the operation result with the order's billing info
	 */
	@Path({Selector.PATH_PART, AnyResourceUri.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadAddress(
			@Scope
			final String scope,
			@AnyResourceUri
			final ResourceState<AddressEntity> billingAddress,
			@ResourceId
			final String orderId,
			final ResourceOperation operation) {

		Command<ResourceState<LinksEntity>> cmd = readBillingAddressChoiceCommandBuilder.get()
				.setScope(scope)
				.setBillingAddressResourceState(billingAddress)
				.setOrderId(orderId)
				.build();

		ExecutionResult<ResourceState<LinksEntity>> result = cmd.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

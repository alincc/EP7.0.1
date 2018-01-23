/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoWriter;
import com.elasticpath.rest.resource.orders.billinginfo.command.SelectBillingInfoCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory;
import com.elasticpath.rest.schema.uri.OrdersUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link SelectBillingInfoCommand}.
 */
@Named
public final class SelectBillingInfoCommandImpl implements SelectBillingInfoCommand {

	private final BillingInfoWriter billingInfoWriter;

	private final OrdersUriBuilder ordersUriBuilder;

	private final BillingAddressListUriBuilderFactory billingAddressUriBuilderFactory;

	private String orderId;

	private String orderScope;

	private ResourceState<AddressEntity> billingAddress;

	/**
	 * Constructor.
	 *
	 * @param billingInfoWriter billing info writer
	 * @param ordersUriBuilder orders URI builder
	 * @param billingAddressUriBuilderFactory the profiles billing address uri builder factory
	 */
	@Inject
	SelectBillingInfoCommandImpl(
			@Named("billingInfoWriter")
			final BillingInfoWriter billingInfoWriter,
			@Named("ordersUriBuilder")
			final OrdersUriBuilder ordersUriBuilder,
			@Named("billingAddressListUriBuilderFactory")
			final BillingAddressListUriBuilderFactory billingAddressUriBuilderFactory) {

		this.billingInfoWriter = billingInfoWriter;
		this.ordersUriBuilder = ordersUriBuilder;
		this.billingAddressUriBuilderFactory = billingAddressUriBuilderFactory;
	}

	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> execute() {

		String addressId = billingAddress.getEntity().getAddressId();
		boolean exists = Assign.ifSuccessful(billingInfoWriter.setAddressForOrder(orderScope, orderId, addressId));
		String orderUri = ordersUriBuilder.setOrderId(orderId).setScope(orderScope).build();
		String billingAddressListUri = billingAddressUriBuilderFactory.get().setScope(orderScope).build();
		String locationUri = URIUtil.format(orderUri, BillingAddressInfo.URI_PART, Selector.URI_PART, billingAddressListUri);
		ExecutionResult<ResourceState<ResourceEntity>> executionResult = ExecutionResultFactory.createCreateOK(locationUri, exists);

		Self self = executionResult.getData().getSelf();
		ResourceState<ResourceEntity> resourceState = ResourceState.builder().withSelf(self).build();

		return ExecutionResult.<ResourceState<ResourceEntity>>builder()
				.withMessage(executionResult.getErrorMessage())
				.withResourceStatus(executionResult.getResourceStatus())
				.withData(resourceState)
				.build();
	}

	/**
	 * Implementation of {@link SelectBillingInfoCommand}.Builder.
	 */
	@Named("selectBillingInfoCommandBuilder")
	public static class BuilderImpl implements SelectBillingInfoCommand.Builder {

		private final SelectBillingInfoCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command command instance.
		 */
		@Inject
		BuilderImpl(final SelectBillingInfoCommandImpl command) {
			this.command = command;
		}

		@Override
		public Builder setScope(final String scope) {
			command.orderScope = scope;
			return this;
		}

		@Override
		public BuilderImpl setOrderId(final String orderId) {
			command.orderId = orderId;
			return this;
		}

		@Override
		public BuilderImpl setBillingAddress(final ResourceState<AddressEntity> billingAddress) {
			command.billingAddress = billingAddress;
			return this;
		}

		@Override
		public SelectBillingInfoCommand build() {
			assert command.orderId != null : "orderId must be set.";
			assert command.orderScope != null : "orderScope must be set.";
			assert command.billingAddress != null : "ResourceState<AddressEntity> must be set.";
			return command;
		}
	}
}

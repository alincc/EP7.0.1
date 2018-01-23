/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command.impl;

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_INFO_NAME;
import static com.elasticpath.rest.schema.ResourceState.Builder.create;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingInfoCommand;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ReadBillingInfoCommand}.
 */
@Named
public final class ReadBillingInfoCommandImpl implements ReadBillingInfoCommand {

	private final OrdersUriBuilderFactory ordersUriBuilderFactory;

	private String orderId;
	private String scope;

	/**
	 * Constructor.
	 *
	 * @param ordersUriBuilderFactory the order URI builder
	 */
	@Inject
	public ReadBillingInfoCommandImpl(
			@Named("ordersUriBuilderFactory")
			final OrdersUriBuilderFactory ordersUriBuilderFactory) {

		this.ordersUriBuilderFactory = ordersUriBuilderFactory;
	}

	@Override
	public ExecutionResult<ResourceState<InfoEntity>> execute() {

		String selfUri = URIUtil.format(
				ordersUriBuilderFactory.get()
						.setOrderId(orderId)
						.setScope(scope)
						.build(),
				BillingAddressInfo.URI_PART
		);

		ResourceState.Builder<InfoEntity> infoResourceState = create(
				InfoEntity.builder()
						.withName(BILLING_ADDRESS_INFO_NAME)
						.withInfoId(orderId)
						.build()
		)
				.withSelf(createSelf(selfUri))
				.withScope(scope);

		return createReadOK(infoResourceState.build());
	}

	/**
	 * Builder.
	 */
	@Named("readBillingInfoCommandBuilder")
	static class BuilderImpl implements ReadBillingInfoCommand.Builder {

		private final ReadBillingInfoCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command the command
		 */
		@Inject
		BuilderImpl(final ReadBillingInfoCommandImpl command) {

			this.command = command;
		}

		@Override
		public ReadBillingInfoCommand build() {

			assert command.orderId != null : "Order ID required";
			assert command.scope != null : "Scope required";
			return command;
		}

		@Override
		public Builder setScope(final String scope) {

			command.scope = scope;
			return this;
		}

		@Override
		public Builder setOrderId(final String orderId) {

			command.orderId = orderId;
			return this;
		}
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command.impl;

import static com.elasticpath.rest.command.ExecutionResultFactory.createReadOK;
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_LIST_NAME;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingAddressChoiceCommand;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of {@link ReadBillingAddressChoiceCommand}.
 */
@Named
public final class ReadBillingAddressChoiceCommandImpl implements ReadBillingAddressChoiceCommand {

	private final ResourceOperationContext operationContext;
	private String scope;
	private String orderId;
	private ResourceState<AddressEntity> billingAddress;

	/**
	 * Default Constructor.
	 *
	 * @param operationContext the resource operation context
	 */
	@Inject
	public ReadBillingAddressChoiceCommandImpl(
			@Named("resourceOperationContext")
			final ResourceOperationContext operationContext) {

		this.operationContext = operationContext;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> execute() {

		LinksEntity linksEntity = LinksEntity.builder()
				.withName(BILLING_ADDRESS_LIST_NAME)
				.withElementListId(orderId)
				.build();
		ResourceState<LinksEntity> resourceState = ResourceState.Builder.create(linksEntity)
				.withScope(scope).withSelf(
						createSelf(operationContext.getResourceOperation().getUri())
				).build();

		return createReadOK(resourceState);
	}

	/**
	 * Read billing address command builder.
	 */
	@Named("readBillingAddressChoiceCommandBuilder")
	public static class BuilderImpl implements ReadBillingAddressChoiceCommand.Builder {

		private final ReadBillingAddressChoiceCommandImpl command;

		/**
		 * Constructor.
		 *
		 * @param command the command
		 */
		@Inject
		public BuilderImpl(final ReadBillingAddressChoiceCommandImpl command) {
			this.command = command;
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

		@Override
		public Builder setBillingAddressResourceState(final ResourceState<AddressEntity> billingAddress) {
			command.billingAddress = billingAddress;
			return this;
		}

		@Override
		public ReadBillingAddressChoiceCommand build() {
			assert command.scope != null : "Scope must be set.";
			assert command.billingAddress != null : "Billing address must be set.";
			assert command.orderId != null : "Order id must be set.";

			return command;
		}
	}
}

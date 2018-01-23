/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.billing.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.addresses.DefaultAddressLookup;
import com.elasticpath.rest.resource.addresses.alias.billing.command.ReadDefaultBillingAddressCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;


/**
 * ReadDefaultBillingAddressCommandImpl.
 */
@Named
final class ReadDefaultBillingAddressCommandImpl implements ReadDefaultBillingAddressCommand {

	private final AddressUriBuilder uriBuilder;
	private final DefaultAddressLookup defaultBillingAddressLookup;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param uriBuilder The uri builder.
	 * @param defaultBillingAddressLookup default billing address lookup
	 */
	@Inject
	ReadDefaultBillingAddressCommandImpl(
			@Named("addressUriBuilder")
			final AddressUriBuilder uriBuilder,
			@Named("defaultBillingAddressLookup")
			final DefaultAddressLookup defaultBillingAddressLookup) {

		this.defaultBillingAddressLookup = defaultBillingAddressLookup;
		this.uriBuilder = uriBuilder;
	}


	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> execute() {

		String defaultAddressId = Assign.ifSuccessful(defaultBillingAddressLookup.getDefaultAddressId(scope));
		String seeOtherUri = uriBuilder
				.setScope(scope)
				.setAddressId(defaultAddressId)
				.build();
		return ExecutionResultFactory.createSeeOther(seeOtherUri);

	}

	/**
	 * Default Billing Address Command builder.
	 */
	@Named("readDefaultBillingAddressCommandBuilder")
	public static class BuilderImpl implements ReadDefaultBillingAddressCommand.Builder {

		private final ReadDefaultBillingAddressCommandImpl command;


		/**
		 * Constructor.
		 *
		 * @param command command instance.
		 */
		@Inject
		BuilderImpl(final ReadDefaultBillingAddressCommandImpl command) {
			this.command = command;
		}


		@Override
		public Builder setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadDefaultBillingAddressCommand build() {
			assert command.scope != null : "scope is required.";
			return command;
		}
	}
}

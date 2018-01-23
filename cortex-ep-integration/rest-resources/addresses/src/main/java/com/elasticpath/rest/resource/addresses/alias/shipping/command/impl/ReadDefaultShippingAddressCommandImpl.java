/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.shipping.command.impl;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.addresses.DefaultAddressLookup;
import com.elasticpath.rest.resource.addresses.alias.shipping.command.ReadDefaultShippingAddressCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;

/**
 * Command for reading default shipping address.
 */
@Named
public final class ReadDefaultShippingAddressCommandImpl implements ReadDefaultShippingAddressCommand {

	private final AddressUriBuilder uriBuilder;
	private final DefaultAddressLookup defaultShippingAddressLoookup;

	private String scope;


	/**
	 * Constructor.
	 *
	 * @param uriBuilder the billing address uri builder
	 * @param defaultShippingAddressLookup default shipping address lookup
	 */
	@Inject
	ReadDefaultShippingAddressCommandImpl(
			@Named("addressUriBuilder")
			final AddressUriBuilder uriBuilder,
			@Named("defaultShippingAddressLookup")
			final DefaultAddressLookup defaultShippingAddressLookup) {

		this.defaultShippingAddressLoookup = defaultShippingAddressLookup;
		this.uriBuilder = uriBuilder;
	}


	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> execute() {

		String defaultAddressId = Assign.ifSuccessful(defaultShippingAddressLoookup.getDefaultAddressId(scope));
		String seeOtherUri = uriBuilder
				.setScope(scope)
				.setAddressId(defaultAddressId)
				.build();

		return ExecutionResultFactory.createSeeOther(seeOtherUri);
	}


	/**
	 * Default Shipping Address Command builder.
	 */
	@Named("readDefaultShippingAddressCommandBuilder")
	public static class BuilderImpl implements ReadDefaultShippingAddressCommand.Builder {

		private final ReadDefaultShippingAddressCommandImpl command;


		/**
		 * Constructor.
		 *
		 * @param command command instance.
		 */
		@Inject
		BuilderImpl(final ReadDefaultShippingAddressCommandImpl command) {
			this.command = command;
		}

		@Override
		public BuilderImpl setScope(final String scope) {
			command.scope = scope;
			return this;
		}

		@Override
		public ReadDefaultShippingAddressCommand build() {
			assert command.scope != null : "Scope must be set";
			return command;
		}
	}
}

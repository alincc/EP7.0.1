/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.addresses.DefaultAddressLookup;
import com.elasticpath.rest.resource.addresses.integration.addresses.alias.DefaultAddressLookupStrategy;

/**
 * Lookup for the default shipping address.
 */
@Singleton
@Named("defaultShippingAddressLookup")
public class DefaultShippingAddressLookupImpl implements DefaultAddressLookup {

	private final DefaultAddressLookupStrategy defaultShippingAddressLookupStrategy;
	private final ResourceOperationContext resourceOperationContext;


	/**
	 * Constructor.
	 *
	 * @param defaultShippingAddressLookupStrategy the default address lookup strategy
	 * @param resourceOperationContext to find default user ID.
	 */
	@Inject
	protected DefaultShippingAddressLookupImpl(
			@Named("defaultShippingAddressLookupStrategy")
			final DefaultAddressLookupStrategy defaultShippingAddressLookupStrategy,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {
		this.defaultShippingAddressLookupStrategy = defaultShippingAddressLookupStrategy;
		this.resourceOperationContext = resourceOperationContext;
	}


	@Override
	public ExecutionResult<String> getDefaultAddressId(final String scope) {
		String userIdentifier = resourceOperationContext.getUserIdentifier();
		String addressId = Assign.ifSuccessful(defaultShippingAddressLookupStrategy.findPreferredAddressId(scope, userIdentifier));

		return ExecutionResultFactory.createReadOK(Base32Util.encode(addressId));
	}
}

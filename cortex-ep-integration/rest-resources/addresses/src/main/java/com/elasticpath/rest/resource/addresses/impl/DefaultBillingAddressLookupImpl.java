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
 * Lookup for the default billing address.
 */
@Singleton
@Named("defaultBillingAddressLookup")
public class DefaultBillingAddressLookupImpl implements DefaultAddressLookup {

	private final DefaultAddressLookupStrategy defaultBillingAddressLookupStrategy;
	private final ResourceOperationContext resourceOperationContext;


	/**
	 * Constructor.
	 *  @param defaultBillingAddressLookupStrategy the default address lookup strategy
	 * @param resourceOperationContext to find user ID.
	 */
	@Inject
	protected DefaultBillingAddressLookupImpl(
			@Named("defaultBillingAddressLookupStrategy")
			final DefaultAddressLookupStrategy defaultBillingAddressLookupStrategy,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext) {
		this.defaultBillingAddressLookupStrategy = defaultBillingAddressLookupStrategy;
		this.resourceOperationContext = resourceOperationContext;
	}


	@Override
	public ExecutionResult<String> getDefaultAddressId(final String scope) {
		String userIdentifier = resourceOperationContext.getUserIdentifier();
		String addressId = Assign.ifSuccessful(defaultBillingAddressLookupStrategy.findPreferredAddressId(scope, userIdentifier));

		return ExecutionResultFactory.createReadOK(Base32Util.encode(addressId));
	}
}

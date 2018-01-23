/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.billing.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.addresses.DefaultAddressLookup;
import com.elasticpath.rest.resource.addresses.billing.Billing;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;

/**
 * Resource Operator for default billing address.
 */
@Singleton
@Named("defaultBillingAddressOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Billing.PATH_PART, Default.PATH_PART})
public class DefaultBillingAddressOperatorImpl implements ResourceOperator {

	private final AddressUriBuilder uriBuilder;
	private final DefaultAddressLookup defaultBillingAddressLookup;

	/**
	 * Constructor.
	 *
	 * @param uriBuilder The uri builder.
	 * @param defaultBillingAddressLookup default billing address lookup
	 */
	@Inject
	DefaultBillingAddressOperatorImpl(
			@Named("addressUriBuilder")
			final AddressUriBuilder uriBuilder,
			@Named("defaultBillingAddressLookup")
			final DefaultAddressLookup defaultBillingAddressLookup) {

		this.defaultBillingAddressLookup = defaultBillingAddressLookup;
		this.uriBuilder = uriBuilder;
	}


	/**
	 * Process read operator for default address.
	 *
	 * @param scope the scope.
	 * @param operation the Resource Operation.
	 * @return the operation result.
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		String defaultAddressId = Assign.ifSuccessful(defaultBillingAddressLookup.getDefaultAddressId(scope));
		String seeOtherUri = uriBuilder
				.setScope(scope)
				.setAddressId(defaultAddressId)
				.build();
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(ExecutionResultFactory.createSeeOther(seeOtherUri), operation);
	}
}

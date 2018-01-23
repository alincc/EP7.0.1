/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.shipping.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.addresses.DefaultAddressLookup;
import com.elasticpath.rest.resource.addresses.shipping.Shipping;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;


/**
 * Resource Operator for default shipping address.
 */
@Singleton
@Named("defaultShippingAddressOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Shipping.PATH_PART, Default.PATH_PART})
public class DefaultShippingAddressOperatorImpl implements ResourceOperator {

	private final AddressUriBuilder uriBuilder;
	private final DefaultAddressLookup defaultShippingAddressLoookup;


	/**
	 * Constructor.
	 *
	 * @param uriBuilder the billing address uri builder
	 * @param defaultShippingAddressLookup default shipping address lookup
	 */
	@Inject
	DefaultShippingAddressOperatorImpl(
			@Named("addressUriBuilder")
			final AddressUriBuilder uriBuilder,
			@Named("defaultShippingAddressLookup")
			final DefaultAddressLookup defaultShippingAddressLookup) {

		this.defaultShippingAddressLoookup = defaultShippingAddressLookup;
		this.uriBuilder = uriBuilder;
	}


	/**
	 * Process read operator for default shipping address.
	 *
	 * @param scope the scope
	 * @param operation the Resource Operation.
	 * @return the operation result.
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		String defaultAddressId = Assign.ifSuccessful(defaultShippingAddressLoookup.getDefaultAddressId(scope));
		String seeOtherUri = uriBuilder
				.setScope(scope)
				.setAddressId(defaultAddressId)
				.build();
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(ExecutionResultFactory.createSeeOther(seeOtherUri), operation);
	}
}

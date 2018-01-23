/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.carts.alias.DefaultCartLookup;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Operator for default cart.
 */
@Singleton
@Named("defaultCartsResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Default.PATH_PART})
public final class DefaultCartResourceOperatorImpl implements ResourceOperator {

	private final DefaultCartLookup defaultCartLookup;

	/**
	 * Constructor.
	 *
	 * @param defaultCartLookup default cart lookup
	 */
	@Inject
	DefaultCartResourceOperatorImpl(
			@Named("defaultCartLookup")
			final DefaultCartLookup defaultCartLookup) {

		this.defaultCartLookup = defaultCartLookup;
	}

	/**
	 * Handles READ operations for default cart.
	 *
	 * @param scope the scope
	 * @param operation the resource operation.
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processCartRead(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<ResourceEntity>> result = defaultCartLookup.getDefaultCartSeeOtherRepresentation(scope);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

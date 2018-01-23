/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.navigations.NavigationLookup;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Processes the resource operation on Navigations.
 */
@Singleton
@Named("navigationsResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART})
public final class NavigationsResourceOperatorImpl implements ResourceOperator {

	private final NavigationLookup navigationLookup;


	/**
	 * Constructor.
	 *
	 * @param navigationLookup the navigation lookup
	 */
	@Inject
	NavigationsResourceOperatorImpl(
			@Named("navigationLookup")
			final NavigationLookup navigationLookup) {

		this.navigationLookup = navigationLookup;
	}


	/**
	 * Handles the READ operation to get root navigation nodes for a given scope.
	 *
	 * @param scope the scope
	 * @param operation the Resource Operation.
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadRootNavigationNodes(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> result = navigationLookup.getRootNavigationNodes(scope);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles the READ operation to get a navigation node.
	 *
	 * @param scope the scope
	 * @param navigationId the navigation id
	 * @param operation the Resource Operation
	 * @return the operation result
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadNavigationNode(
			@Scope
			final String scope,
			@ResourceId
			final String navigationId,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<NavigationEntity>> result = navigationLookup.getNavigationNode(scope, navigationId);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

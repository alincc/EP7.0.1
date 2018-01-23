/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Queries for navigation information.
 */
public interface NavigationLookup {

	/**
	 * Gets the root navigation nodes.
	 *
	 * @param scope the scope.
	 * @return a collection of encoded root navigation nodes.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getRootNavigationNodes(String scope);

	/**
	 * Gets a navigation node.
	 *
	 * @param scope the scope
	 * @param navigationId the navigation id
	 * @return the navigation node representation
	 */
	ExecutionResult<ResourceState<NavigationEntity>> getNavigationNode(String scope, String navigationId);
}

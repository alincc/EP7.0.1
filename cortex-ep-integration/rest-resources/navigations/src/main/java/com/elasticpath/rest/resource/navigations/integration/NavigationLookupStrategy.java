/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.navigations.integration.dto.NavigationDto;

/**
 * Service that provides lookup of navigation data from external systems.
 */
public interface NavigationLookupStrategy {

	/**
	 * Finds all root navigation nodes for a scope.
	 *
	 * @param scope the scope.
	 * @return a collection of navigation node IDs.
	 */
	ExecutionResult<Collection<String>> findRootNodeIds(String scope);

	/**
	 * Find the navigation node with the given ID.
	 *
	 * @param scope the scope.
	 * @param decodedNavigationId the decoded navigation id.
	 * @return the execution result with navigation data.
	 */
	ExecutionResult<NavigationDto> find(String scope, String decodedNavigationId);
}

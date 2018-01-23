/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for default cart.
 */
public interface DefaultCartLookup {

	/**
	 * Find the default cart of the currently logged-in user.
	 *
	 *
	 * @param scope the scope.
	 * @return the representation for the default cart.
	 */
	ExecutionResult<ResourceState<ResourceEntity>> getDefaultCartSeeOtherRepresentation(String scope);
}

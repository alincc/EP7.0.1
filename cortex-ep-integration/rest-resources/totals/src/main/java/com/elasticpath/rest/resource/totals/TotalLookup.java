/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Queries for total information.
 * 
 * @param <E> the resource entity to support.
 */
public interface TotalLookup<E extends ResourceEntity> {
	/**
	 * Gets the total associated with the given representation.
	 * 
	 * @param representation the representation.
	 * @return the associated total information for the given representation
	 */
	ExecutionResult<ResourceState<TotalEntity>> getTotal(ResourceState<E> representation);

}
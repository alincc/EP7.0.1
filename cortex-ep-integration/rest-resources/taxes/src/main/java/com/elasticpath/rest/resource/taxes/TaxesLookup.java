/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Queries for taxes information.
 * 
 * @param <T> the resource type on which to operate
 */
public interface TaxesLookup<T extends ResourceEntity> {

	/**
	 * Get tax information associated with some resource.
	 * 
	 * @param resource the resource for which to look up taxes
	 * @return the result of the taxes lookup
	 */
	ExecutionResult<ResourceState<TaxesEntity>> getTaxes(ResourceState<T> resource);

}

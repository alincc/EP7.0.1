/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Builds the resource links for totals from other resources.
 */
public interface TotalResourceLinkCreator {

	/**
	 * Creates a resource link to link a resource to totals.
	 *
	 *
	 * @param resourceUri the resource URI.
	 * @param rev the rev to the resource.
	 * @return the resource link
	 */
	Collection<ResourceLink> createLinkToOtherResource(String resourceUri, String rev);


	/**
	 *
	 * Creates a resource link to link a resource to totals.
	 *
	 *
	 * @param resourceUri the resource URI.
	 * @param totalsResult the totals lookup execution results.
	 * @param rev the rev to the resource.
	 * @return the resource link
	 *
	 */
	Collection<ResourceLink> createLinkToOtherResource(String resourceUri, ExecutionResult<ResourceState<TotalEntity>> totalsResult, String rev);

}

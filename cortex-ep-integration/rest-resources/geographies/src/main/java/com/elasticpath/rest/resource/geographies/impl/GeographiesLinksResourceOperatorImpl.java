/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Handles LINK operations for Geographies.
 */
@Singleton
@Named("geographiesLinksResourceOperator")
@Path(ResourceName.PATH_PART)
public final class GeographiesLinksResourceOperatorImpl implements ResourceOperator {

	private final LinkRegionsToCountryHandler linkRegionsHandler;

	/**
	 * Constructor.
	 *
	 * @param linkRegionsHandler link Regions handler
	 */
	@Inject
	public GeographiesLinksResourceOperatorImpl(
			@Named("linkRegionsToCountryHandler")
			final LinkRegionsToCountryHandler linkRegionsHandler) {

		this.linkRegionsHandler = linkRegionsHandler;
	}


	/**
	 * Process LINK operations.
	 *
	 * @param operation the LINK operation to process.
	 * @return The operation result.
	 */
	@Path("{path:.*}")
	@OperationType(Operation.LINK)
	public OperationResult processLink(
			final ResourceOperation operation) {

		ResourceState<?> rep = operation.getResourceState();
		ResourceState<LinksEntity> links = ResourceState.Builder.create(LinksEntity.builder().build())
				.withLinks(linkRegionsHandler.createLinks(rep))
				.build();
		return OperationResultFactory.createReadOK(links, operation);
	}
}

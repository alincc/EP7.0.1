/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.link.impl;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.availabilities.AvailabilitiesMediaTypes;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test utility for creating availability responses.
 */
public final class TestAvailabilityResponseFactory {

	private TestAvailabilityResponseFactory() {
		//static
	}

	/**
	 * Setup an ExecutionResult with availability.
	 *
	 * @return ExecutionResult.
	 */
	static ExecutionResult<ResourceState<AvailabilityEntity>> buildExecutionResultWithAvailability() {
		ResourceState<AvailabilityEntity> available = ResourceState.Builder.create(AvailabilityEntity.builder().build())
				.withSelf(SelfFactory.createSelf("mockAvailabilitySelfUri", AvailabilitiesMediaTypes.AVAILABILITY.id()))
				.build();
		return ExecutionResultFactory.createReadOK(available);
	}

	/**
	 * Setup an ExecutionResult with no availability.
	 *
	 * @return ExecutionResult.
	 */
	static ExecutionResult<ResourceState<AvailabilityEntity>> buildExecutionResultWithNoAvailability() {
		return ExecutionResultFactory.createNotFound("availability not found.");
	}
}

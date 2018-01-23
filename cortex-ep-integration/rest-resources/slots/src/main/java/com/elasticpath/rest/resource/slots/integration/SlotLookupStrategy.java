/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;

/**
 * Service that provides lookup of slots data from external systems.
 */
public interface SlotLookupStrategy {

	/**
	 * Finds all slot ids.
	 *
	 * @param scope the scope
	 * @return a collection of slot ids.
	 */
	ExecutionResult<Collection<String>> findAllSlotIds(String scope);

	/**
	 * Gets a slot with the given decoded id.
	 *
	 * @param scope the scope
	 * @param decodedSlotId the decoded slot id.
	 * @return the execution result with the slot Dto.
	 */
	ExecutionResult<SlotDto> getSlot(String scope, String decodedSlotId);
}

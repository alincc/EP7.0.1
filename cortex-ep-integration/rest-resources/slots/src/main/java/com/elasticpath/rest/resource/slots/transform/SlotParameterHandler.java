/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.transform;

import java.util.Collection;

import com.elasticpath.rest.definition.slots.SlotEntity;
import com.elasticpath.rest.resource.slots.integration.dto.SlotDto;
import com.elasticpath.rest.schema.ResourceLink;


/**
 * The SlotParameterHandler interface to attach dynamic content parameters to slot representation.
 */
public interface SlotParameterHandler {

	/**
	 * Checks for a specific slot parameter, populates the slot entity and links with appropriate values.
	 *
	 * @param scope the scope
	 * @param entityBuilder the slot entity builder
	 * @param links the links collection to add to
	 * @param slotDto the slot dto
	 */
	void handle(String scope, SlotEntity.Builder entityBuilder, Collection<ResourceLink> links, SlotDto slotDto);
}

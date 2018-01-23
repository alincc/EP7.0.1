/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.dto;

import java.util.Map;

import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Data transfer object for slots.
 */
public interface SlotDto extends ResourceEntity {

	/**
	 * Gets the targetId.
	 *
	 * @return the target Id of the slot.
	 */
	String getTargetId();

	/**
	 * Sets the targetId.
	 *
	 * @param targetId the targetId.
	 * @return this instance.
	 */
	SlotDto setTargetId(String targetId);

	/**
	 * Gets the correlation id.
	 *
	 * @return the correlation id of the slot.
	 */
	String getCorrelationId();

	/**
	 * Sets the correlation id.
	 *
	 * @param correlationId the correlation id
	 * @return this instance.
	 */
	SlotDto setCorrelationId(String correlationId);

	/**
	 * Gets the type of content wrapper.
	 *
	 * @return the type of the slot content wrapper.
	 */
	String getType();

	/**
	 * Sets the type of content wrapper.
	 *
	 * @param type the content wrapper type.
	 * @return this instance.
	 */
	SlotDto setType(String type);

	/**
	 * Gets the parameter map.
	 *
	 * @return the map of parameter ID to ParameterDto.
	 */
	Map<String, SlotParameterDto> getParameters();

	/**
	 * Sets the targetId.
	 *
	 * @param parameters the parameters for the slot.
	 * @return this instance.
	 */
	SlotDto setParameters(Map<String, SlotParameterDto> parameters);
}

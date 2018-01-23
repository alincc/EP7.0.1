/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.integration.dto;

import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Data transfer object for slot parameters.
 */
public interface SlotParameterDto extends ResourceEntity {

	/**
	 * Gets the parameter type.
	 *
	 * @return the parameter type
	 */
	String getType();

	/**
	 * Sets the parameter type.
	 *
	 * @param type the type of parameter
	 * @return this instance
	 */
	SlotParameterDto setType(String type);

	/**
	 * Gets the parameter value.
	 *
	 * @return the parameter value
	 */
	String getValue();

	/**
	 * Sets the parameter value.
	 *
	 * @param value the value of parameter
	 * @return this instance
	 */
	SlotParameterDto setValue(String value);
}

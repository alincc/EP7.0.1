/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.integration.dto;

import java.util.Collection;

import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Data transfer object for navigation data.
 */
public interface NavigationDto extends ResourceEntity {

	/**
	 * Gets the name of the navigation element.
	 *
	 * @return the name of the navigation element.
	 */
	String getName();

	/**
	 * Sets the name of the navigation element.
	 *
	 * @param name the name of the navigation element.
	 * @return the navigation dto.
	 */
	NavigationDto setName(String name);

	/**
	 * Gets the parent navigation correlation id.
	 *
	 * @return the parent navigation correlation id
	 */
	String getParentNavigationCorrelationId();

	/**
	 * Sets the parent navigation correlation id.
	 *
	 * @param parentNavigationCorrelationId the parent navigation correlation id
	 * @return the navigation dto
	 */
	NavigationDto setParentNavigationCorrelationId(String parentNavigationCorrelationId);

	/**
	 * Gets the child navigation correlation ids.
	 *
	 * @return the child navigation correlation ids
	 */
	Collection<String> getChildNavigationCorrelationIds();

	/**
	 * Sets the child navigation correlation ids.
	 *
	 * @param childNavigationCorrelationIds the children navigation correlation ids
	 * @return the navigation dto
	 */
	NavigationDto setChildNavigationCorrelationIds(Collection<String> childNavigationCorrelationIds);

	/**
	 * Sets the navigation correlation id.
	 *
	 * @param navigationCorrelationId the navigation correlation id
	 * @return the navigation dto
	 */
	NavigationDto setNavigationCorrelationId(String navigationCorrelationId);

	/**
	 * Gets the navigation correlation id.
	 *
	 * @return the navigation correlation id
	 */
	String getNavigationCorrelationId();

	/**
	 * Sets the navigation attributes list.
	 *
	 * @param attributes the attributes to set
	 * @return This instance.
	 */
	NavigationDto setAttributes(Collection<DetailsEntity> attributes);

	/**
	 * Gets the navigation attributes collection.
	 *
	 * @return the attributes list.
	 */
	Collection<DetailsEntity> getAttributes();

	/**
	 * Sets the display name.
	 *
	 * @param displayName the display name
	 * @return the navigation dto
	 */
	NavigationDto setDisplayName(String displayName);

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	String getDisplayName();
}

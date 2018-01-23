/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.mapper;

/**
 * Map recommendations group values from epcommerce to Cortex, and vise versa.
 *
 * @param <T> the commerce integration recommendations group type
 */
public interface RecommendationsGroupMapper<T> {
	/**
	 * Convert from a commerce integration group type to a Cortex group name.
	 *
	 * @param commerceGroupType the commerce integration group type
	 * @return the cortex group name
	 */
	String fromCommerceToCortex(T commerceGroupType);

	/**
	 * Convert from a Cortex group name to a commerce integration group type.
	 *
	 * @param cortexGroupName the cortex group name
	 * @return the commerce integration group type
	 */
	T fromCortexToCommerce(String cortexGroupName);
}

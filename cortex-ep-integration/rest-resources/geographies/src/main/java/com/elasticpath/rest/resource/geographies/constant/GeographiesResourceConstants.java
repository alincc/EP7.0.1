/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.constant;

import com.elasticpath.rest.ResourceInfo;

/**
 * Constants for geographies resource.
 */
public final class GeographiesResourceConstants {

	/**
	 * Max age.
	 */
	public static final int MAX_AGE = 600;

	/** Resource Info with max-age set. */
	public static final ResourceInfo RESOURCE_INFO = ResourceInfo.builder()
			.withMaxAge(MAX_AGE)
			.build();
}

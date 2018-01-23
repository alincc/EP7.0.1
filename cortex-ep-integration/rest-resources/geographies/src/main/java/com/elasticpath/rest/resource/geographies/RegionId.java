/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elasticpath.rest.resource.dispatch.operator.annotation.PathParam;
import com.elasticpath.rest.resource.dispatch.operator.annotation.patterns.UriPatterns;

/**
 * Annotates a path segment to be the region ID.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@PathParam("regionId")
public @interface RegionId {

	/**
	 * {@link com.elasticpath.rest.resource.dispatch.operator.annotation.Path} pattern to identify the regionId segment.
	 */
	String PATH_PART = "{regionId:" + UriPatterns.RESOURCE_ID_PATTERN + '}';
}

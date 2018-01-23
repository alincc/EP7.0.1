/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */


package com.elasticpath.rest.resource.recommendations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elasticpath.rest.resource.dispatch.operator.annotation.PathParam;
import com.elasticpath.rest.resource.dispatch.operator.annotation.patterns.UriPatterns;

/**
 * Annotates a method parameter to be the recommendation group.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@PathParam("recommendationGroup")
public @interface RecommendationGroup {

	/**
	 * {@link com.elasticpath.rest.resource.dispatch.operator.annotation.Path} pattern to capture the Recommendation group name.
	 */
	String PATH_PART = "{recommendationGroup:" + UriPatterns.RESOURCE_NAME_PATTERN + '}';
}


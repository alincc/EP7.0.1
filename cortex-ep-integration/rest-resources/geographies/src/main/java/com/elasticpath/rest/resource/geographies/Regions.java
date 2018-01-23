/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elasticpath.rest.resource.dispatch.operator.annotation.PathParam;

/**
 * Annotates a method parameter to be the option name. <br>
 * The parameter should be also annotated with {@link PathParam}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@PathParam("regions")
public @interface Regions {

	/**
	 * Name of uri part, useful for constructing URIs.
	 */
	String URI_PART = "regions";

	/**
	 * {@link com.elasticpath.rest.resource.dispatch.operator.annotation.Path} part.
	 */
	String PATH_PART = URI_PART;
}
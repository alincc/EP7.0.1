/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.items;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elasticpath.rest.resource.dispatch.operator.annotation.PathParam;

/**
 * Annotates a path segment to be the item.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@PathParam("items")
public @interface Items {

	/**
	 * Name of uri part, useful for constructing URIs.
	 */
	String URI_PART = "items";

	/**
	 * {@link com.elasticpath.rest.resource.dispatch.operator.annotation.Path} part.
	 */
	String PATH_PART = URI_PART;
	
}

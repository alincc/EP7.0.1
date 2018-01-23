/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elasticpath.rest.resource.dispatch.operator.annotation.PathParam;

/**
 * Annotates a path segment to be shipping address.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@PathParam(ShippingAddress.URI_PART)
public @interface ShippingAddress {

	/**
	 * The string name of the uri part, useful for constructing URIs.
	 */
	String URI_PART = "shippingaddress";

	/**
	 * {@link com.elasticpath.rest.resource.dispatch.operator.annotation.Path} pattern to identify the shipping address.
	 */
	String PATH_PART = URI_PART;
}

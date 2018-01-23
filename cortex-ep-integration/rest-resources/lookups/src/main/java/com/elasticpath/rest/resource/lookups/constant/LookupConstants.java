/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.constant;

import java.util.concurrent.TimeUnit;

import com.elasticpath.rest.ResourceInfo;

/**
 * Constants for coupons.
 */
public final class LookupConstants {

	/** MAX AGE for lookups caching. **/
	@SuppressWarnings("checkstyle:magicnumber")
	public static final ResourceInfo TEN_MINUTES = ResourceInfo.builder()
		.withMaxAge((int) TimeUnit.MINUTES.toSeconds(10))
		.build();
}

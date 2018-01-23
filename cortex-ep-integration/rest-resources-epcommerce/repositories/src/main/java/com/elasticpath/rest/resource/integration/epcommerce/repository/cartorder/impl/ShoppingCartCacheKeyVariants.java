/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import java.util.Arrays;
import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.ArrayUtils;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.cache.CacheKeyVariants;

/**
 * Generates cache keys from class name and parameters.
 */
@Singleton
@Named("shoppingCartCacheKeyVariants")
public class ShoppingCartCacheKeyVariants implements CacheKeyVariants<ShoppingCart> {

	@Override
	public Collection<Object[]> get(final ShoppingCart objectToCache) {
		return Arrays.asList(
				ArrayUtils.EMPTY_OBJECT_ARRAY,
				new Object[] { objectToCache.getGuid() }
		);
	}

	@Override
	public Class<ShoppingCart> getType() {
		return ShoppingCart.class;
	}
}

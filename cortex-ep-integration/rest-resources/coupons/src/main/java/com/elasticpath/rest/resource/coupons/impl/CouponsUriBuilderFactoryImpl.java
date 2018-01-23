/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.CouponsUriBuilder;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

/**
 * Factory for creating CouponsUriBuilder.
 */
@Singleton
@Named("couponsUriBuilderFactory")
public final class CouponsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<CouponsUriBuilder>
		implements CouponsUriBuilderFactory {

	/**
	 * Default constructor.
	 *
	 * @param couponsUriBuilderProvider the coupons URI builder provider.
	 */
	@Inject
	public CouponsUriBuilderFactoryImpl(
			@Named("couponsUriBuilder")
			final Provider<CouponsUriBuilder> couponsUriBuilderProvider) {
		super(couponsUriBuilderProvider);
	}

}

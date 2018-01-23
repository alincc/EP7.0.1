/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilder;
import com.elasticpath.rest.schema.uri.DiscountsUriBuilderFactory;

/**
 * Uri builder factory.
 */
@Singleton
@Named("discountsUriBuilderFactory")
public final class DiscountsUriBuilderFactoryImpl
		extends AbstractProviderDecoratorImpl<DiscountsUriBuilder>
		implements DiscountsUriBuilderFactory {
	/**
	 * Constructor.
	 * @param discountsUriBuilderProvider the discounts URI builder provider.
	 */
	@Inject
	public DiscountsUriBuilderFactoryImpl(
			@Named("discountsUriBuilder")
			final Provider<DiscountsUriBuilder> discountsUriBuilderProvider) {
		super(discountsUriBuilderProvider);
	}
}
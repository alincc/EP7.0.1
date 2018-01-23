/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.CartsUriBuilder;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * A factory for creating CartsUriBuilder objects.
 */
@Singleton
@Named("cartsUriBuilderFactory")
public final class CartsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<CartsUriBuilder>
		implements CartsUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider carts URI Builder Provider.
	 */
	@Inject
	CartsUriBuilderFactoryImpl(
			@Named("cartsUriBuilder")
			final Provider<CartsUriBuilder> provider) {

		super(provider);
	}

}

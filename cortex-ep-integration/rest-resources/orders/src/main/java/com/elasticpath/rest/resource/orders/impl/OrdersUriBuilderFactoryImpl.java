/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.OrdersUriBuilder;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;

/**
 * Factory for creating {@link OrdersUriBuilder}s.
 */
@Singleton
@Named("ordersUriBuilderFactory")
public final class OrdersUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<OrdersUriBuilder>
		implements OrdersUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider orders uri builder.
	 */
	@Inject
	OrdersUriBuilderFactoryImpl(
			@Named("ordersUriBuilder")
			final Provider<OrdersUriBuilder> provider) {

		super(provider);
	}

}

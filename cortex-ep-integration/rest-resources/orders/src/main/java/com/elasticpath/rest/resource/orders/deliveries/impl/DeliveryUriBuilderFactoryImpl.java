/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilder;
import com.elasticpath.rest.schema.uri.DeliveryUriBuilderFactory;

/**
 * A factory for creating DeliveryListUriBuilder objects.
 */
@Singleton
@Named("deliveryUriBuilderFactory")
public final class DeliveryUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<DeliveryUriBuilder>
		implements DeliveryUriBuilderFactory {

	/**
	 * Default constructor.
	 *
	 * @param provider the provider
	 */
	@Inject
	public DeliveryUriBuilderFactoryImpl(
			@Named("deliveryUriBuilder")
			final Provider<DeliveryUriBuilder> provider) {

		super(provider);
	}

}

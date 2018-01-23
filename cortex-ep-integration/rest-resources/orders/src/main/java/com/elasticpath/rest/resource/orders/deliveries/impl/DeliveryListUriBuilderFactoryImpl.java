/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.DeliveryListUriBuilder;
import com.elasticpath.rest.schema.uri.DeliveryListUriBuilderFactory;

/**
 * Factory providing a delivery list URI builder.
 */
@Singleton
@Named("deliveryListUriBuilderFactory")
public final class DeliveryListUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<DeliveryListUriBuilder>
		implements DeliveryListUriBuilderFactory {

	/**
	 * Constructor that has dependencies injected.
	 *
	 * @param provider the delivery list uri builder provider
	 */
	@Inject
	public DeliveryListUriBuilderFactoryImpl(
			@Named("deliveryListUriBuilder")
			final Provider<DeliveryListUriBuilder> provider) {

		super(provider);
	}

}

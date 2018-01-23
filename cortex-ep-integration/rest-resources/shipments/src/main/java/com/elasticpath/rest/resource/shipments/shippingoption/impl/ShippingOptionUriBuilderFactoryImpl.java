/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.shippingoption.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingOptionUriBuilderFactory;

/**
 * Default implementation of {@link com.elasticpath.rest.schema.uri.ShippingOptionUriBuilderFactory}.
 */
@Named("shippingOptionUriBuilderFactory")
public final class ShippingOptionUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ShippingOptionUriBuilder>
		implements ShippingOptionUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider the {@link Provider} of the {@link com.elasticpath.rest.schema.uri.ShippingOptionUriBuilder}
	 */
	@Inject
	protected ShippingOptionUriBuilderFactoryImpl(
			@Named("shippingOptionUriBuilder")
			final Provider<ShippingOptionUriBuilder> provider) {
		super(provider);
	}

}

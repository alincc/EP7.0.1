/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShippingAddressUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingAddressUriBuilderFactory;

/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.ShippingAddressUriBuilderFactory}.
 */
@Singleton
@Named("shippingAddressUriBuilderFactory")
public final class ShippingAddressUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ShippingAddressUriBuilder>
		implements ShippingAddressUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider the {@link javax.inject.Provider}
	 */
	@Inject
	ShippingAddressUriBuilderFactoryImpl(
			@Named("shippingAddressUriBuilder")
			final Provider<ShippingAddressUriBuilder> provider) {
		super(provider);
	}

}

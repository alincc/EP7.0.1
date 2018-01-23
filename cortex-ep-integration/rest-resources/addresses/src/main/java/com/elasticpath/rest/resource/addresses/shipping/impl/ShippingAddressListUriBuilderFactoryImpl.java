/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.shipping.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.ShippingAddressListUriBuilder}.
 */
@Singleton
@Named("shippingAddressListUriBuilderFactory")
public final class ShippingAddressListUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ShippingAddressListUriBuilder>
		implements ShippingAddressListUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider provider instance.
	 */
	@Inject
	ShippingAddressListUriBuilderFactoryImpl(
			@Named("shippingAddressListUriBuilder")
			final Provider<ShippingAddressListUriBuilder> provider) {

		super(provider);
	}

}

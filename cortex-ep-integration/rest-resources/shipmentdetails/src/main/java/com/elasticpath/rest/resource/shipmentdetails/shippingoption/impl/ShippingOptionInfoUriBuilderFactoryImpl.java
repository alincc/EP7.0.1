/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilder;
import com.elasticpath.rest.schema.uri.ShippingOptionInfoUriBuilderFactory;

/**
 * Implementation of {@link ShippingOptionInfoUriBuilderFactory}.
 */
@Named("shippingOptionInfoUriBuilderFactory")
public class ShippingOptionInfoUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ShippingOptionInfoUriBuilder>
		implements ShippingOptionInfoUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param shippingOptionInfoUriBuilderProvider the {@link ShippingOptionInfoUriBuilder} provider
	 */
	@Inject
	public ShippingOptionInfoUriBuilderFactoryImpl(
			@Named("shippingOptionInfoUriBuilder")
			final Provider<ShippingOptionInfoUriBuilder> shippingOptionInfoUriBuilderProvider) {
		super(shippingOptionInfoUriBuilderProvider);
	}
}

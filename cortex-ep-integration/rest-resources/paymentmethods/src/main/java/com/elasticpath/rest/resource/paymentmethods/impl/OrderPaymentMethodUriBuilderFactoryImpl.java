/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory;

/**
 * Implementation of {@link OrderPaymentMethodUriBuilderFactory}.
 */
@Singleton
@Named("orderPaymentMethodUriBuilderFactory")
public class OrderPaymentMethodUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<OrderPaymentMethodUriBuilder>
		implements OrderPaymentMethodUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider the {@link javax.inject.Provider}
	 */
	@Inject
	OrderPaymentMethodUriBuilderFactoryImpl(
			@Named("orderPaymentMethodUriBuilder")
			final Provider<OrderPaymentMethodUriBuilder> provider) {
		super(provider);
	}

}

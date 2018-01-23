/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilderFactory;

/**
 * Factory for {@link PaymentMethodListUriBuilder}.
 */
@Singleton
@Named("paymentMethodListUriBuilderFactory")
public final class PaymentMethodListUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<PaymentMethodListUriBuilder>
		implements PaymentMethodListUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider payment methods list uri builders
	 */
	@Inject
	PaymentMethodListUriBuilderFactoryImpl(
			@Named("paymentMethodListUriBuilder")
			final Provider<PaymentMethodListUriBuilder> provider) {

		super(provider);
	}

}

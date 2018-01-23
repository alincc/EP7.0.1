/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;

/**
 * Factory for {@link PaymentMethodUriBuilder}.
 */
@Singleton
@Named("paymentMethodUriBuilderFactory")
public final class PaymentMethodUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<PaymentMethodUriBuilder>
		implements PaymentMethodUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider provider for payment method uri builders.
	 */
	@Inject
	PaymentMethodUriBuilderFactoryImpl(
			@Named("paymentMethodUriBuilder")
			final Provider<PaymentMethodUriBuilder> provider) {

		super(provider);
	}

}

/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;

/**
 * Implements {@link PaymentMethodInfoUriBuilderFactory}.
 */
@Named("paymentMethodInfoUriBuilderFactory")
public class PaymentMethodInfoUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<PaymentMethodInfoUriBuilder>
		implements PaymentMethodInfoUriBuilderFactory {

	/**
	 * Default constructor.
	 *
	 * @param provider {@link PaymentMethodInfoUriBuilder}s
	 */
	@Inject
	PaymentMethodInfoUriBuilderFactoryImpl(
			@Named("paymentMethodInfoUriBuilder")
			final Provider<PaymentMethodInfoUriBuilder> provider) {
		super(provider);
	}

}

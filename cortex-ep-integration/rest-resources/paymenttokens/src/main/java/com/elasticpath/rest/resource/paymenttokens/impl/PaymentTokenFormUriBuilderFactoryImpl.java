/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory;

/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.PaymentTokenFormUriBuilderFactory}.
 */
@Singleton
@Named("paymentTokenFormUriBuilderFactory")
public class PaymentTokenFormUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<PaymentTokenFormUriBuilder>
		implements PaymentTokenFormUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider the {@link javax.inject.Provider}
	 */
	@Inject
	PaymentTokenFormUriBuilderFactoryImpl(
			@Named("paymentTokenFormUriBuilder")
			final Provider<PaymentTokenFormUriBuilder> provider) {
		super(provider);
	}

}

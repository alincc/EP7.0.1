/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilder;
import com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilderFactory;

/**
 * Implementation of {@link com.elasticpath.rest.schema.uri.CreatePaymentTokenUriBuilderFactory}.
 */
@Singleton
@Named("createPaymentTokenUriBuilderFactory")
public class CreatePaymentTokenUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<CreatePaymentTokenUriBuilder>
		implements CreatePaymentTokenUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider the {@link javax.inject.Provider}
	 */
	@Inject
	CreatePaymentTokenUriBuilderFactoryImpl(
			@Named("createPaymentTokenUriBuilder")
			final Provider<CreatePaymentTokenUriBuilder> provider) {
		super(provider);
	}

}

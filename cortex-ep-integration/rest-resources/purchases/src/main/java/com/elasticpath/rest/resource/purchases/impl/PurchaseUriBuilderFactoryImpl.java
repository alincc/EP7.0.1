/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilder;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;

/**
 * Default implementation of {@link PurchaseUriBuilderFactory}.
 */
@Singleton
@Named("purchaseUriBuilderFactory")
public final class PurchaseUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<PurchaseUriBuilder> 
		implements PurchaseUriBuilderFactory {

	/**
	 * Constructor for PurchaseUriBuilderFactoryImpl.
	 * 
	 * @param provider {@link PurchaseUriBuilder} provider
	 */
	@Inject
	protected PurchaseUriBuilderFactoryImpl(
			@Named("purchaseUriBuilder")
			final Provider<PurchaseUriBuilder> provider) {
		super(provider);
	}

}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.PricesUriBuilder;
import com.elasticpath.rest.schema.uri.PricesUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.PricesUriBuilderFactory}s.
 */
@Singleton
@Named("pricesUriBuilderFactory")
public final class PricesUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<PricesUriBuilder>
		implements PricesUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider Provider for {@link com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder}s.
	 */
	@Inject
	PricesUriBuilderFactoryImpl(
			@Named("pricesUriBuilder")
			final Provider<PricesUriBuilder> provider) {
		super(provider);
	}
}

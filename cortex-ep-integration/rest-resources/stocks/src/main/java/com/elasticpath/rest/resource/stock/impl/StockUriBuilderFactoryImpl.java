/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.StockUriBuilder;
import com.elasticpath.rest.schema.uri.StockUriBuilderFactory;

/**
 * Default implementation of {@link StockUriBuilderFactory}.
 */
@Singleton
@Named("stockUriBuilderFactory")
public class StockUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<StockUriBuilder> implements StockUriBuilderFactory {

	/**
	 * Constructor that accepts a provider. 
	 * 
	 * @param provider the {@link Provider} of the {@link StockUriBuilder}
	 */
	@Inject
	protected StockUriBuilderFactoryImpl(@Named("stockUriBuilder") final Provider<StockUriBuilder> provider) {
		super(provider);
	}

}

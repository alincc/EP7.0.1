/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.TaxesUriBuilder;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;

/**
 * Default implementation of {@link TaxesUriBuilderFactory}.
 */
@Singleton
@Named("taxesUriBuilderFactory")
public class TaxesUriBuilderFactoryImpl 
			extends AbstractProviderDecoratorImpl<TaxesUriBuilder> 
			implements TaxesUriBuilderFactory {

	/**
	 * Constructor.
	 * 
	 * @param taxesUriBuilderProvider the taxes URI builder provider.
	 */
	@Inject
	public TaxesUriBuilderFactoryImpl(
			@Named("taxesUriBuilder") 
			final Provider<TaxesUriBuilder> taxesUriBuilderProvider) {
		super(taxesUriBuilderProvider);
	}

}

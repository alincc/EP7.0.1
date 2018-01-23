/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.schema.uri.ItemLookupUriBuilder;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilderFactory;

/**
 * A factory for creating ItemLookupUriBuilder objects.
 */
@Singleton
@Named("itemLookupUriBuilderFactory")
public final class ItemLookupUriBuilderFactoryImpl implements
		ItemLookupUriBuilderFactory {

	private final Provider<ItemLookupUriBuilder> provider;
	
	/**
	 * Constructor.
	 *
	 * @param provider item lookups uri builder provider.
	 */
	@Inject
	ItemLookupUriBuilderFactoryImpl(
			@Named("itemLookupUriBuilder")
			final Provider<ItemLookupUriBuilder> provider) {

		this.provider = provider;
	}
	
	@Override
	public ItemLookupUriBuilder get() {
		return provider.get();
	}

}

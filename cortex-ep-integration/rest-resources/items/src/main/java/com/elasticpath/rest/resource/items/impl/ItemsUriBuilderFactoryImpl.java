/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Factory for {@link ItemsUriBuilder}.
 */
@Singleton
@Named("itemsUriBuilderFactory")
public final class ItemsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ItemsUriBuilder>
		implements ItemsUriBuilderFactory {

	/**
	 * Construct an ItemsUriBuilderFactory.
	 *
	 * @param provider Provider for ItemsUriBuilder instances.
	 */
	@Inject
	ItemsUriBuilderFactoryImpl(
			@Named("itemsUriBuilder")
			final Provider<ItemsUriBuilder> provider) {

		super(provider);
	}

}

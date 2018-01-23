/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionValueUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionValueUriBuilderFactory;

/**
 * Factory for {@link ItemDefinitionsOptionValueUriBuilder}.
 */
@Singleton
@Named("itemDefinitionsOptionValueUriBuilderFactory")
public final class ItemDefinitionsOptionValueUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ItemDefinitionsOptionValueUriBuilder>
		implements ItemDefinitionsOptionValueUriBuilderFactory {

	/**
	 * Construct an {@link ItemDefinitionsOptionValueUriBuilderFactory}.
	 *
	 * @param provider Provider for ItemsUriBuilder instances.
	 */
	@Inject
	ItemDefinitionsOptionValueUriBuilderFactoryImpl(
			@Named("itemDefinitionsOptionValueUriBuilder")
			final Provider<ItemDefinitionsOptionValueUriBuilder> provider) {

		super(provider);
	}

}

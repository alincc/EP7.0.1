/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionUriBuilderFactory;

/**
 * Factory for {@link ItemDefinitionsOptionUriBuilder}.
 */
@Singleton
@Named("itemDefinitionsOptionUriBuilderFactory")
public final class ItemDefinitionsOptionUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ItemDefinitionsOptionUriBuilder>
		implements ItemDefinitionsOptionUriBuilderFactory {

	/**
	 * Construct an ItemsUriBuilderFactory.
	 *
	 * @param provider Provider for ItemsUriBuilder instances.
	 */
	@Inject
	ItemDefinitionsOptionUriBuilderFactoryImpl(
			@Named("itemDefinitionsOptionUriBuilder")
			final Provider<ItemDefinitionsOptionUriBuilder> provider) {

		super(provider);
	}

}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder}s.
 */
@Singleton
@Named("itemDefinitionsUriBuilderFactory")
public final class ItemDefinitionsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ItemDefinitionsUriBuilder>
		implements ItemDefinitionsUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider Provider for {@link com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilder}s.
	 */
	@Inject
	ItemDefinitionsUriBuilderFactoryImpl(
			@Named("itemDefinitionsUriBuilder")
			final Provider<ItemDefinitionsUriBuilder> provider) {
		super(provider);
	}
}

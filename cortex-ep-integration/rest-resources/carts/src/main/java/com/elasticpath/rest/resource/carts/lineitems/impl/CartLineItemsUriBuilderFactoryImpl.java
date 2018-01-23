/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;

/**
 * A factory for creating CartLineItemsUriBuilder objects.
 */
@Singleton
@Named("cartLineItemsUriBuilderFactory")
public final class CartLineItemsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<CartLineItemsUriBuilder>
		implements CartLineItemsUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider cart line items URI Builder Provider.
	 */
	@Inject
	CartLineItemsUriBuilderFactoryImpl(
			@Named("cartLineItemsUriBuilder")
			final Provider<CartLineItemsUriBuilder> provider) {

		super(provider);
	}

}

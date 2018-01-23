/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.PurchaseListUriBuilder;
import com.elasticpath.rest.schema.uri.PurchaseListUriBuilderFactory;

/**
 * Factory for {@link PurchaseListUriBuilder}.
 */
@Singleton
@Named("purchaseListUriBuilderFactory")
public final class PurchaseListUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<PurchaseListUriBuilder>
		implements PurchaseListUriBuilderFactory {

	/**
	 * Construct an PurchaseListUriBuilderFactory.
	 *
	 * @param provider Provider for PurchaseListUriBuilder instances.
	 */
	@Inject
	PurchaseListUriBuilderFactoryImpl(
			@Named("purchaseListUriBuilder")
			final Provider<PurchaseListUriBuilder> provider) {

		super(provider);
	}

}

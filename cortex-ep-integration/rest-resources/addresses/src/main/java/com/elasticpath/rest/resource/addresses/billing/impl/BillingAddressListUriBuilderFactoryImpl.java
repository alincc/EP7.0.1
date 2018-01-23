/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.billing.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder}.
 */
@Singleton
@Named("billingAddressListUriBuilderFactory")
public final class BillingAddressListUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<BillingAddressListUriBuilder>
		implements BillingAddressListUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider provider instance.
	 */
	@Inject
	BillingAddressListUriBuilderFactoryImpl(
			@Named("billingAddressListUriBuilder")
			final Provider<BillingAddressListUriBuilder> provider) {

		super(provider);
	}

}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilder;
import com.elasticpath.rest.schema.uri.AddressFormUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.AddressFormUriBuilder}.
 */
@Singleton
@Named("addressFormUriBuilderFactory")
public final class AddressFormUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<AddressFormUriBuilder>
		implements AddressFormUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider provider instance.
	 */
	@Inject
	AddressFormUriBuilderFactoryImpl(
			@Named("addressFormUriBuilder")
			final Provider<AddressFormUriBuilder> provider) {

		super(provider);
	}

}

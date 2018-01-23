/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.AddressUriBuilder;
import com.elasticpath.rest.schema.uri.AddressUriBuilderFactory;

/**
 * Factory for {@link com.elasticpath.rest.schema.uri.AddressUriBuilder}.
 */
@Singleton
@Named("addressUriBuilderFactory")
public final class AddressUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<AddressUriBuilder>
		implements AddressUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider provider instance.
	 */
	@Inject
	AddressUriBuilderFactoryImpl(
			@Named("addressUriBuilder")
			final Provider<AddressUriBuilder> provider) {

		super(provider);
	}

}

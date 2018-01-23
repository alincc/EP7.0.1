/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.TotalsUriBuilder;
import com.elasticpath.rest.schema.uri.TotalsUriBuilderFactory;

/**
 * A factory for creating TotalsUriBuilder objects.
 */
@Singleton
@Named("totalsUriBuilderFactory")
public final class TotalsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<TotalsUriBuilder>
		implements TotalsUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param provider totals uri builder provider.
	 */
	@Inject
	TotalsUriBuilderFactoryImpl(
			@Named("totalsUriBuilder")
			final Provider<TotalsUriBuilder> provider) {

		super(provider);
	}

}

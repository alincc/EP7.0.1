/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.NavigationsUriBuilder;
import com.elasticpath.rest.schema.uri.NavigationsUriBuilderFactory;

/**
 * Implements the {@link NavigationsUriBuilderFactory}.
 */
@Singleton
@Named("navigationsUriBuilderFactory")
public final class NavigationsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<NavigationsUriBuilder>
		implements NavigationsUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param navigationsUriBuilderProvider the builder provider.
	 */
	@Inject
	public NavigationsUriBuilderFactoryImpl(
			@Named("navigationsUriBuilder")
			final Provider<NavigationsUriBuilder> navigationsUriBuilderProvider) {
		super(navigationsUriBuilderProvider);
	}

}

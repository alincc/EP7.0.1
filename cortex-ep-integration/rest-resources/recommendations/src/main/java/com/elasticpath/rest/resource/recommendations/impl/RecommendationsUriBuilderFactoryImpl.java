/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilder;
import com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory;

/**
 * Implements the {@link com.elasticpath.rest.schema.uri.RecommendationsUriBuilderFactory}.
 */
@Singleton
@Named("recommendationsUriBuilderFactory")
public final class RecommendationsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<RecommendationsUriBuilder>
		implements RecommendationsUriBuilderFactory {

	/**
	 * Constructor.
	 *
	 * @param recommendationsUriBuilderProvider the builder provider.
	 */
	@Inject
	public RecommendationsUriBuilderFactoryImpl(
			@Named("recommendationsUriBuilder")
			final Provider<RecommendationsUriBuilder> recommendationsUriBuilderProvider) {
		super(recommendationsUriBuilderProvider);
	}
}

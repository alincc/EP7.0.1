/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilder;
import com.elasticpath.rest.schema.uri.PromotionsUriBuilderFactory;

/**
 * Factory for creating PromotionsUriBuilder.
 */
@Singleton
@Named("promotionsUriBuilderFactory")
public final class PromotionsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<PromotionsUriBuilder>
		implements PromotionsUriBuilderFactory {

	/**
	 * Default constructor.
	 *
	 * @param promotionsUriBuilderProvider the promotions URI builder provider.
	 */
	@Inject
	public PromotionsUriBuilderFactoryImpl(
			@Named("promotionsUriBuilder")
			final Provider<PromotionsUriBuilder> promotionsUriBuilderProvider) {
		super(promotionsUriBuilderProvider);
	}

}

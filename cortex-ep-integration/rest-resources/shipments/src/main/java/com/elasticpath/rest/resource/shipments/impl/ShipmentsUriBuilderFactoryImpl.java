/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.resource.commons.provider.AbstractProviderDecoratorImpl;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilder;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;

/**
 * Factory for creating ShipmentsUriBuilder.
 */
@Singleton
@Named("shipmentsUriBuilderFactory")
public final class ShipmentsUriBuilderFactoryImpl extends AbstractProviderDecoratorImpl<ShipmentsUriBuilder>
		implements ShipmentsUriBuilderFactory {

	/**
	 * Default constructor.
	 *
	 * @param shipmentsUriBuilderProvider the shipments URI builder provider.
	 */
	@Inject
	public ShipmentsUriBuilderFactoryImpl(
			@Named("shipmentsUriBuilder")
			final Provider<ShipmentsUriBuilder> shipmentsUriBuilderProvider) {
		super(shipmentsUriBuilderProvider);
	}

}

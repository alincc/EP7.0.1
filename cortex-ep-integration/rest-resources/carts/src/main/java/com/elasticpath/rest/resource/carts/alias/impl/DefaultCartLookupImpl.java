/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.alias.DefaultCartLookup;
import com.elasticpath.rest.resource.carts.alias.integration.DefaultCartLookupStrategy;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;

/**
 * Default cart lookup.
 */
@Singleton
@Named("defaultCartLookup")
public final class DefaultCartLookupImpl implements DefaultCartLookup {

	private final DefaultCartLookupStrategy defaultCartLookupStrategy;
	private final CartsUriBuilderFactory cartsUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param defaultCartLookupStrategy default cart lookup strategy
	 * @param cartsUriBuilderFactory the carts uri builder
	 */
	@Inject
	DefaultCartLookupImpl(
			@Named("defaultCartLookupStrategy")
			final DefaultCartLookupStrategy defaultCartLookupStrategy,
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory) {

		this.defaultCartLookupStrategy = defaultCartLookupStrategy;
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
	}


	@Override
	public ExecutionResult<ResourceState<ResourceEntity>> getDefaultCartSeeOtherRepresentation(final String scope) {

		String encodedCartId = getEncodedCartIDFromStrategy(scope);
		String seeOtherUri = cartsUriBuilderFactory.get()
				.setScope(scope)
				.setCartId(encodedCartId)
				.build();
		ResourceState<ResourceEntity> redirectRepresentation = ResourceState.builder()
				.withSelf(SelfFactory.createSelf(seeOtherUri))
				.build();
		return ExecutionResultFactory.create(null, ResourceStatus.SEE_OTHER, redirectRepresentation);
	}

	private String getEncodedCartIDFromStrategy(final String scope) {
		String cartId = Assign.ifSuccessful(defaultCartLookupStrategy.getDefaultCartId(scope));
		return Base32Util.encode(cartId);
	}
}

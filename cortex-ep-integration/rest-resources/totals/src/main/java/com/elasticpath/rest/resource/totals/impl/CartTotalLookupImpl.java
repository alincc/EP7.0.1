/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.TotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.transform.TotalTransformer;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implements {@link TotalLookup} for {@link CartEntity} by connecting to core.
 */
@Singleton
@Named("cartTotalLookup")
public class CartTotalLookupImpl implements TotalLookup<CartEntity> {

	private final TotalLookupStrategy totalLookupStrategy;

	private final TotalTransformer totalTransformer;

	/**
	 * Constructor.
	 *
	 * @param totalLookupStrategy the total lookup strategy
	 * @param totalTransformer    the total transformer
	 */
	@Inject
	CartTotalLookupImpl(
			@Named("totalLookupStrategy")
			final TotalLookupStrategy totalLookupStrategy,
			@Named("totalTransformer")
			final TotalTransformer totalTransformer) {

		this.totalLookupStrategy = totalLookupStrategy;
		this.totalTransformer = totalTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<TotalEntity>> getTotal(final ResourceState<CartEntity> cartEntityResourceState) {
		String scope = cartEntityResourceState.getScope();
		String cartId = cartEntityResourceState.getEntity().getCartId();
		String decodedCartId = Base32Util.decode(cartId);

		ExecutionResult<TotalEntity> cartTotalResult = totalLookupStrategy.getCartTotal(scope, decodedCartId);
		return processResult(cartEntityResourceState, TotalResourceRels.CART_REL, cartTotalResult);
	}

	private ExecutionResult<ResourceState<TotalEntity>> processResult(
			final ResourceState<?> resourceState,
			final String resourceRel,
			final ExecutionResult<TotalEntity> totalResult) {

		TotalEntity totalEntity = Assign.ifSuccessful(totalResult);
		ResourceState<TotalEntity> total = totalTransformer.transform(totalEntity, resourceState, resourceRel);

		return ExecutionResultFactory.createReadOK(total);
	}
}
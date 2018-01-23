/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.Lists;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.CartLookup;
import com.elasticpath.rest.resource.carts.integration.CartLookupStrategy;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Looks up carts.
 */
@Singleton
@Named("cartLookup")
public class CartLookupImpl implements CartLookup {

	private final CartsUriBuilderFactory cartsUriBuilderFactory;
	private final CartLookupStrategy cartLookupStrategy;
	private final TransformToResourceState<CartEntity, CartEntity> cartTransformer;

	/**
	 * Constructor.
	 *
	 * @param cartLookupStrategy cart lookup strategy
	 * @param cartTransformer the cart transformer
	 * @param cartsUriBuilderFactory the cart uri builder factory
	 */
	@Inject
	CartLookupImpl(
			@Named("cartLookupStrategy")
			final CartLookupStrategy cartLookupStrategy,
			@Named("cartTransformer")
			final TransformToResourceState<CartEntity, CartEntity> cartTransformer,
			@Named("cartsUriBuilderFactory")
			final CartsUriBuilderFactory cartsUriBuilderFactory) {

		this.cartLookupStrategy = cartLookupStrategy;
		this.cartTransformer = cartTransformer;
		this.cartsUriBuilderFactory = cartsUriBuilderFactory;
	}

	@Override
	public ExecutionResult<ResourceState<CartEntity>> findCart(final String scope, final String cartId) {

		String decodedCartId = Base32Util.decode(cartId);
		CartEntity cartEntity = Assign.ifSuccessful(cartLookupStrategy.getCart(scope, decodedCartId));
		ResourceState<CartEntity> cart = cartTransformer.transform(scope, cartEntity);
		return ExecutionResultFactory.createReadOK(cart);
	}

	@Override
	public ExecutionResult<Collection<String>> findCartIds(final String scope, final String userId) {

		Collection<String> cartIds;
		try {
			cartIds = Assign.ifSuccessful(cartLookupStrategy.getCartIds(scope, userId));
		} catch (BrokenChainException bce) {
			cartIds = Assign.ifBrokenChainExceptionStatus(bce, ResourceStatus.NOT_FOUND, Collections.<String>emptyList());
		}
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(cartIds));
	}

	@Override
	public ExecutionResult<Collection<ResourceLink>> getCartMemberships(final String scope, final String itemId) {

		Collection<String> cartIds = Assign.ifSuccessful(cartLookupStrategy.findContainingItem(itemId));
		Collection<ResourceLink> resourceLinks = buildResourceLinksForIds(scope, Base32Util.encodeAll(cartIds));

		return ExecutionResultFactory.createReadOK(resourceLinks);
	}

	/**
	 * Builds a collection of resource links for the given cart ids.
	 * @param scope the scope
	 * @param cartIds encoded cart ids
	 * @return a set of links
	 */
	protected Collection<ResourceLink> buildResourceLinksForIds(final String scope, final Collection<String> cartIds) {
		Collection<ResourceLink> resourceLinks = Lists.newArrayListWithCapacity(cartIds.size());

		for (String cartId : cartIds) {
			String uri = cartsUriBuilderFactory.get()
				.setScope(scope)
				.setCartId(cartId)
				.build();

			String type = CartsMediaTypes.CART.id();

			ResourceLink link = ElementListFactory.createElementOfList(uri, type);
			resourceLinks.add(link);
		}

		return resourceLinks;
	}
}

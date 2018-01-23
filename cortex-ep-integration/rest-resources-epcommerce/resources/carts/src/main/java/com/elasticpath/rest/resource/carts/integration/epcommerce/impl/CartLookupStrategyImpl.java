/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.carts.integration.CartLookupStrategy;
import com.elasticpath.rest.resource.carts.integration.epcommerce.transform.ShoppingCartTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * EP Commerce implementation of the cart lookup strategy.
 */
@Singleton
@Named("cartLookupStrategy")
public class CartLookupStrategyImpl implements CartLookupStrategy {

	private final ShoppingCartRepository shoppingCartRepository;
	private final ShoppingCartTransformer shoppingCartTransformer;
	private final ItemRepository itemRepository;

	/**
	 * The constructor for injection.
	 *
	 * @param shoppingCartRepository shopping cart repository
	 * @param shoppingCartTransformer the shopping cart transformer
	 * @param itemRepository the item repository
	 */
	@Inject
	CartLookupStrategyImpl(
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository,
			@Named("shoppingCartTransformer")
			final ShoppingCartTransformer shoppingCartTransformer,
			@Named("itemRepository")
			final ItemRepository itemRepository) {

		this.shoppingCartRepository = shoppingCartRepository;
		this.shoppingCartTransformer = shoppingCartTransformer;
		this.itemRepository = itemRepository;
	}

	@Override
	public ExecutionResult<CartEntity> getCart(final String scope, final String cartGuid) {
		ShoppingCart cart = Assign.ifSuccessful(shoppingCartRepository.getShoppingCart(cartGuid));
		CartEntity cartEntity = shoppingCartTransformer.transformToEntity(cart);
		return ExecutionResultFactory.createReadOK(cartEntity);
	}

	@Override
	public ExecutionResult<Collection<String>> getCartIds(final String storeCode, final String customerGuid) {
		return shoppingCartRepository.findAllCarts(customerGuid, storeCode);
	}

	@Override
	public ExecutionResult<Collection<String>> findContainingItem(final String itemId) {
		ShoppingCart cart = Assign.ifSuccessful(shoppingCartRepository.getDefaultShoppingCart());
		ProductSku productSku = Assign.ifSuccessful(itemRepository.getSkuForItemId(itemId));

		Collection<String> cartGuids;
		if (cart == null || productSku == null || cart.getCartItem(productSku.getSkuCode()) == null) {
			cartGuids = Collections.emptySet();
		} else {
			cartGuids = Collections.singleton(cart.getGuid());
		}
		return ExecutionResultFactory.createReadOK(cartGuids);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias.integration.epcommerce.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.carts.alias.integration.DefaultCartLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

/**
 * EP Commerce implementation of the default cart lookup strategy.
 */
@Singleton
@Named("defaultCartLookupStrategy")
public class DefaultCartLookupStrategyImpl implements DefaultCartLookupStrategy {

	private final ShoppingCartRepository shoppingCartRepository;

	/**
	 * Default constructor.
	 *
	 * @param shoppingCartRepository shopping cart repository
	 */
	@Inject
	public DefaultCartLookupStrategyImpl(
			@Named("shoppingCartRepository")
			final ShoppingCartRepository shoppingCartRepository) {

		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Override
	public ExecutionResult<String> getDefaultCartId(final String storeCode) {
		String defaultGuid;

		ExecutionResult<String> defaultShoppingCartGuidResult = shoppingCartRepository.getDefaultShoppingCartGuid(storeCode);
		if (defaultShoppingCartGuidResult.getResourceStatus() == ResourceStatus.NOT_FOUND) {
			ShoppingCart cart = Assign.ifSuccessful(shoppingCartRepository.getDefaultShoppingCart());
			defaultGuid = cart.getGuid();
		} else {
			defaultGuid = Assign.ifSuccessful(defaultShoppingCartGuidResult);
		}
		return ExecutionResultFactory.createReadOK(defaultGuid);
	}
}

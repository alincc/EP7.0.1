/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for cart promotions.
 */
public interface CartPromotionsLookup {

	/**
	 * Gets the promotions for the given item in cart.
	 *
	 *
	 * @param cartLineItemRepresentation the cart line item representation to read promotions for
	 * @return the links representation with promotions links.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForItemInCart(ResourceState<LineItemEntity> cartLineItemRepresentation);

	/**
	 * Gets the promotions applied to a given  cart.
	 *
	 *
	 * @param cartRepresentation the cart line representation to read promotions
	 * @return the links representation with promotions links.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForCart(ResourceState<CartEntity> cartRepresentation);

	/**
	 * Gets the possible promotions for the given cart.
	 *
	 *
	 * @param cartRepresentation the cart representation to read for possible promotions.
	 * @return the links representation with possible promotions links.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getPossiblePromotionsForCart(ResourceState<CartEntity> cartRepresentation);


	/**
	 * See if given cart has possible promotions.
	 *
	 *
	 * @param cartRepresentation the cart representation to check for possible promotions.
	 * @return true if cart contains possible promotions.
	 */
	ExecutionResult<Boolean> cartHasPossiblePromotions(ResourceState<CartEntity> cartRepresentation);

}

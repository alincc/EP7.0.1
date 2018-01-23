/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup class for item promotions.
 */
public interface ItemPromotionsLookup {

	/**
	 * Gets the promotions for the given item.
	 *
	 *
	 * @param itemRepresentation the item representation to read promotions for
	 * @return the links representation with promotions links.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForItem(ResourceState<ItemEntity> itemRepresentation);

	/**
	 * Gets the possible promotions for the given item.
	 *
	 *
	 * @param itemRepresentation the item representation to read possible promotions for
	 * @return the links representation with possible promotions links.
	 */
	ExecutionResult<ResourceState<LinksEntity>> getPossiblePromotionsForItem(ResourceState<ItemEntity> itemRepresentation);

	/**
	 * See if given item has possible promotions.
	 *
	 *
	 * @param itemRepresentation the item representation to check for possible promotions
	 * @return true if item contains possible promotions
	 */
	ExecutionResult<Boolean> itemHasPossiblePromotions(ResourceState<ItemEntity> itemRepresentation);

}

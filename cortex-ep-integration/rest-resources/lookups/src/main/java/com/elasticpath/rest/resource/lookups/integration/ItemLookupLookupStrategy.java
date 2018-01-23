/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.integration;

import java.util.List;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * The Interface ItemLookupLookupStrategy.
 */
public interface ItemLookupLookupStrategy {

	/**
	 * Gets the item lookup by item .
	 *
	 * @param itemId the item id
	 * @return the lookup for an item
	 */
	ExecutionResult<CodeEntity> getItemLookupByItem(String itemId);

	/**
	 * Gets the item by code.
	 *
	 * @param skuCode the sku code
	 * @return the lookup for an item
	 */
	ExecutionResult<String> getItemIdByCode(String skuCode);

	/**
	 * Retrieves a batch id for a given list of skuCodes.
	 *
	 * @param skuCodes the codes to create a batch id for
	 * @return the batch id that references the skuCodes
	 */
	IdentifierPart<List<String>> getBatchIdForCodes(Iterable<String> skuCodes);

	/**
	 * Retrieves the item Ids referenced by a batch id.
	 *
	 * @param batchItemsId the batch items identifier
	 * @return the related item ids
	 */
	Iterable<ItemIdentifier> getItemIdsForBatchId(BatchItemsIdentifier batchItemsId);
}

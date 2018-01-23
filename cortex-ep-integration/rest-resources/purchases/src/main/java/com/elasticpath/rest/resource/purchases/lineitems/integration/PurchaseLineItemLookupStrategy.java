/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;

/**
 * The Interface PurchaseLineItemLookupStrategy.
 */
public interface PurchaseLineItemLookupStrategy {

	/**
	 * Find line item ids.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findLineItemIds(String scope, String decodedPurchaseId);

	/**
	 * Gets the line item.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedLineItemId the decoded line item id
	 * @param decodedParentLineItemId the decoded parent line item id
	 * @return the line item
	 */
	ExecutionResult<PurchaseLineItemEntity> getLineItem(String scope, String decodedPurchaseId,
			String decodedLineItemId, String decodedParentLineItemId);

	/**
	 * Checks if line item is a bundle.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedLineItemId the decoded line item id
	 * @return the execution result
	 */
	ExecutionResult<Boolean> isLineItemBundle(String scope, String decodedPurchaseId, String decodedLineItemId);

	/**
	 * Gets the component ids for line item.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedLineItemId the decoded line item id
	 * @return the component ids for line item
	 */
	ExecutionResult<Collection<String>> getComponentIdsForLineItemId(String scope, String decodedPurchaseId, String decodedLineItemId);
}

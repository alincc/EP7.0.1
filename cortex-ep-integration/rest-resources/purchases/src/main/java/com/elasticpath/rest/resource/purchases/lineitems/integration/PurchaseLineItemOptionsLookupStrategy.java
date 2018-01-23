/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;

/**
 * Lookup strategy for line item options and values.
 */
public interface PurchaseLineItemOptionsLookupStrategy {

	/**
	 * Find option IDs for the line item.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedLineItemId the decoded line item id
	 * @return the execution result
	 */
	ExecutionResult<Collection<String>> findOptionIds(String scope, String decodedPurchaseId, String decodedLineItemId);

	/**
	 * Find an option of the given line item.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedLineItemId the decoded line item id
	 * @param decodedOptionId the decoded option id
	 * @return the execution result
	 */
	ExecutionResult<PurchaseLineItemOptionEntity> findOption(
			String scope, String decodedPurchaseId, String decodedLineItemId, String decodedOptionId);

	/**
	 * Find an option value of the given line item.
	 *
	 * @param scope the scope
	 * @param decodedPurchaseId the decoded purchase id
	 * @param decodedLineItemId the decoded line item id
	 * @param decodedOptionId the decoded option id
	 * @param decodedValueId the decoded value id
	 * @return the execution result
	 */
	ExecutionResult<PurchaseLineItemOptionValueEntity> findOptionValue(String scope, String decodedPurchaseId, String decodedLineItemId,
			String decodedOptionId, String decodedValueId);
}

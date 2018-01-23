/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup service for promotions data.
 */
public interface PromotionsLookup {

	/**
	 * Gets the promotion details for a given scope and promotion ID.
	 *
	 * @param scope the scope.
	 * @param promotionId the promotion ID.
	 * @return the promotion representation for this promotion ID.
	 */
	ExecutionResult<ResourceState<PromotionEntity>> getPromotionDetails(String scope, String promotionId);

	/**
	 * Gets the promotion details for a purchase within a given scope.
	 *
	 * @param scope               scope
	 * @param promotionId         the promotion ID.
	 * @param decodedPurchaseId   decodedPurchaseId
	 * @param otherRepresentation the other representation.
	 * @return the promotion representation for this promotion applied to the purchase.
	 */
	ExecutionResult<ResourceState<PromotionEntity>> getPurchasePromotionDetails(String scope, String promotionId,
			String decodedPurchaseId, ResourceState otherRepresentation);
}

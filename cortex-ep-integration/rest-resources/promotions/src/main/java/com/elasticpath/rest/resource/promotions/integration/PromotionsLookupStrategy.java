/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.promotions.PromotionEntity;

/**
 * Service that provides lookup of promotions data from external systems.
 */
public interface PromotionsLookupStrategy {

	/**
	 * Gets a promotion by Id.
	 *
	 * @param scope the scope.
	 * @param decodedPromotionId the id of the promotion required.
	 * @return the promotionDto.
	 */
	ExecutionResult<PromotionEntity> getPromotionById(String scope, String decodedPromotionId);

	/**
	 * Returns the promotion with the promotion id used in purchase with purchase id.
	 * @param scope the scope
	 * @param decodedPromotionId decoded promotion id
	 * @param decodedPurchaseId decoded purchase id
	 * @return the promotion with the promotion id used in purchase with purchase id.
	 */
	ExecutionResult<PromotionEntity> getPromotionForPurchase(String scope, String decodedPromotionId, String decodedPurchaseId);

}

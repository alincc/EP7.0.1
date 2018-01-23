/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.integration;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Service that provides lookup of applied shipping option promotions data from external systems.
 */
public interface AppliedShippingOptionPromotionsLookupStrategy {

	/**
	 * Gets the applied promotions for the given shipping option.
	 *
	 * @param scope the scope.
	 * @param shipmentDetailsId the parent shipment details id.
	 * @param shippingOptionId the shipping option id.
	 * @return a collection of applied promotion IDs.
	 */
	ExecutionResult<Collection<String>> getAppliedPromotionsForShippingOption(String scope, String shipmentDetailsId, String shippingOptionId);
}

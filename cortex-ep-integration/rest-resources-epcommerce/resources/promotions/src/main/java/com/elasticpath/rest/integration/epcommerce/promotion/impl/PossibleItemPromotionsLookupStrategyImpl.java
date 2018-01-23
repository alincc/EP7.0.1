/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.promotions.integration.PossibleItemPromotionsLookupStrategy;

/**
 * Service that provides lookup of possible item promotions data from external systems.
 */
@Singleton
@Named("possibleItemPromotionsLookupStrategy")
public class PossibleItemPromotionsLookupStrategyImpl implements PossibleItemPromotionsLookupStrategy {

	@Override
	public ExecutionResult<Collection<String>> getPossiblePromotionsForItem(final String scope, final String itemId) {
		return ExecutionResultFactory.createNotImplemented();
	}


	@Override
	public ExecutionResult<Boolean> itemHasPossiblePromotions(final String scope, final String itemId) {
		return ExecutionResultFactory.createNotImplemented();
	}
}
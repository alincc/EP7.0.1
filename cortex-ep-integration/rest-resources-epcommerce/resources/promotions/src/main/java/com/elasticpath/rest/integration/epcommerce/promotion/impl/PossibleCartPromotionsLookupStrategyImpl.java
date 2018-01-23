/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.promotions.integration.PossibleCartPromotionsLookupStrategy;

/**
 * Service that provides lookup of possible line item promotions data from external systems.
 */
@Singleton
@Named("possibleCartPromotionsLookupStrategy")
public class PossibleCartPromotionsLookupStrategyImpl implements PossibleCartPromotionsLookupStrategy {

	@Override
	public ExecutionResult<Collection<String>> getPossiblePromotionsForCart(final String scope, final String decodedCartId) {
		return ExecutionResultFactory.createNotImplemented();
	}

	@Override
	public ExecutionResult<Boolean> cartHasPossiblePromotions(final String scope, final String decodedCartId) {
		return ExecutionResultFactory.createNotImplemented();
	}
}
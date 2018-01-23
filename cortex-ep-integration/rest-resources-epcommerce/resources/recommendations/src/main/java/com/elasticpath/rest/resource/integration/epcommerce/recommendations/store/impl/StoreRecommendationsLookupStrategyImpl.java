/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.store.impl;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.recommendations.integration.StoreRecommendationsLookupStrategy;

/**
 * Lookup strategy for recommendations through CE.
 */
@Singleton
@Named("storeRecommendationsLookupStrategy")
public class StoreRecommendationsLookupStrategyImpl implements StoreRecommendationsLookupStrategy {
	
	@Override
	public ExecutionResult<Collection<RecommendationsEntity>> getRecommendations(final String scope) {
		return ExecutionResultFactory.createNotImplemented();
	}


	@Override
	public ExecutionResult<PaginationDto> getRecommendedItemsFromGroup(final String scope, final String recommendationGroup, final int pageNumber) {
		return ExecutionResultFactory.createNotImplemented();
	}
}

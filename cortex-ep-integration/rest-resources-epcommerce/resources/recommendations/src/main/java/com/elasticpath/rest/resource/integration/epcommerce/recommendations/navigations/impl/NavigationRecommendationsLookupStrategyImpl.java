/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.recommendations.navigations.impl;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.recommendations.RecommendationsEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.recommendations.integration.NavigationRecommendationsLookupStrategy;

/**
 * Lookup strategy for navigation recommendations through CE.
 */
@Singleton
@Named("navigationRecommendationsLookupStrategy")
public class NavigationRecommendationsLookupStrategyImpl implements NavigationRecommendationsLookupStrategy {

	@Override
	public ExecutionResult<Collection<RecommendationsEntity>> getRecommendations(final String scope, final String decodedNavigationId) {
		return ExecutionResultFactory.createNotImplemented();
	}

	@Override
	public ExecutionResult<PaginationDto> getRecommendedItemsFromGroup(final String scope, final String decodedNavigationId,
			final String recommendationGroup, final int pageNumber) {
		return ExecutionResultFactory.createNotImplemented();
	}

	@Override
	public ExecutionResult<Boolean> hasRecommendations(final String scope, final String decodedNavigationId) {
		return ExecutionResultFactory.createNotImplemented();
	}

}

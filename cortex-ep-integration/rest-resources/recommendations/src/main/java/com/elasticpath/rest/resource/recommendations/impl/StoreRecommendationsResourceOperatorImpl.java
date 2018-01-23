/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.recommendations.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.pagination.constant.PaginationResourceConstants;
import com.elasticpath.rest.resource.recommendations.RecommendationGroup;
import com.elasticpath.rest.resource.recommendations.StoreRecommendationsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Class for handling Store Recommendations Resource.
 */
@Singleton
@Named("storeRecommendationsResourceOperator")
@Path({ResourceName.PATH_PART})
public final class StoreRecommendationsResourceOperatorImpl implements ResourceOperator {

	private final StoreRecommendationsLookup storeRecommendationsLookup;

	/**
	 * Constructor.
	 *
	 * @param storeRecommendationsLookup the recommended items lookup
	 */
	@Inject
	StoreRecommendationsResourceOperatorImpl(
			@Named("storeRecommendationsLookup")
			final StoreRecommendationsLookup storeRecommendationsLookup) {
		this.storeRecommendationsLookup = storeRecommendationsLookup;
	}

	/**
	 * Handles READ operations on retrieving store recommendations.
	 *
	 * @param scope the scope
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(Scope.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadStoreRecommendations(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> result = storeRecommendationsLookup.getRecommendations(scope);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}


	/**
	 * Process a READ for recommended items by recommendation group.
	 *
	 * @param scope the scope
	 * @param recommendationGroup the recommendation group
	 * @param operation the resource operation
	 * @return the operation result containing the first page of recommended items
	 */
	@Path({Scope.PATH_PART, RecommendationGroup.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadRecommendedItemsForStore(
			@Scope
			final String scope,
			@RecommendationGroup
			final String recommendationGroup,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				storeRecommendationsLookup.getRecommendedItemsFromGroup(scope, recommendationGroup, PaginationResourceConstants.FIRST_PAGE);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process a paged READ for recommended items by recommendation group.
	 *
	 * @param scope the scope
	 * @param recommendationGroup the recommendation group
	 * @param operation the resource operation
	 * @param pageNumber the page number
	 * @return the operation result containing the requested page of recommended items
	 */
	@Path({Scope.PATH_PART, RecommendationGroup.PATH_PART, PageNumber.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processPagedReadRecommendedItemsForStore(
			@Scope
			final String scope,
			@RecommendationGroup
			final String recommendationGroup,
			@PageNumber
			final String pageNumber,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				storeRecommendationsLookup.getRecommendedItemsFromGroup(scope, recommendationGroup, Integer.parseInt(pageNumber));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

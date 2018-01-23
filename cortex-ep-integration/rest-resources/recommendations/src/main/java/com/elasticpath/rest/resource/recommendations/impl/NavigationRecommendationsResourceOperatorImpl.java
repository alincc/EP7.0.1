/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
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
import com.elasticpath.rest.definition.navigations.NavigationEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.pagination.constant.PaginationResourceConstants;
import com.elasticpath.rest.resource.recommendations.NavigationRecommendationsLookup;
import com.elasticpath.rest.resource.recommendations.RecommendationGroup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Class for handling Navigation Recommendations Resource.
 */
@Singleton
@Named("navigationRecommendationsResourceOperator")
@Path({ResourceName.PATH_PART})
public final class NavigationRecommendationsResourceOperatorImpl implements ResourceOperator {

	private final NavigationRecommendationsLookup navigationRecommendationsLookup;

	/**
	 * Constructor.
	 *
	 * @param navigationRecommendationsLookup the navigation recommendations lookup
	 */
	@Inject
	NavigationRecommendationsResourceOperatorImpl(
			@Named("navigationRecommendationsLookup")
			final NavigationRecommendationsLookup navigationRecommendationsLookup) {

		this.navigationRecommendationsLookup = navigationRecommendationsLookup;
	}


	/**
	 * Handle read operations for recommendations of navigation resources.
	 *
	 * @param navigationEntityResourceState the navigation representation to be used in the read.
	 * @param operation the resource operation.
	 * @return the result.
	 */
	@Path(SingleResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadNavigationRecommendations(
			@SingleResourceUri
			final ResourceState<NavigationEntity> navigationEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> result =
				navigationRecommendationsLookup.getRecommendations(navigationEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process a READ for recommended items by recommendations ID.
	 *
	 * @param navigationResourceState the navigation representation to be used in the read.
	 * @param recommendationGroup the group name of the recommendations
	 * @param operation the resource operation
	 * @return the operation result containing the first page of recommended items
	 */
	@Path({SingleResourceUri.PATH_PART, RecommendationGroup.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadRecommendedItemsForNavigation(
			@SingleResourceUri
			final ResourceState<NavigationEntity> navigationResourceState,
			@RecommendationGroup
			final String recommendationGroup,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				navigationRecommendationsLookup.getRecommendedItemsFromGroup(
						navigationResourceState, recommendationGroup, PaginationResourceConstants.FIRST_PAGE);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process a paged READ for recommended items by recommendations ID.
	 *
	 * @param navigationResourceState the navigation representation to be used in the read.
	 * @param recommendationGroup the group name of the recommendations
	 * @param operation the resource operation
	 * @param pageNumber the page number
	 * @return the operation result containing the requested page of recommended items
	 */
	@Path({SingleResourceUri.PATH_PART, RecommendationGroup.PATH_PART, PageNumber.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processPagedReadRecommendedItemsForNavigation(
			@SingleResourceUri
			final ResourceState<NavigationEntity> navigationResourceState,
			@RecommendationGroup
			final String recommendationGroup,
			@PageNumber
			final String pageNumber,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				navigationRecommendationsLookup.getRecommendedItemsFromGroup(navigationResourceState, recommendationGroup,
						Integer.parseInt(pageNumber));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

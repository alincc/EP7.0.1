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
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.pagination.constant.PaginationResourceConstants;
import com.elasticpath.rest.resource.recommendations.ItemRecommendationsLookup;
import com.elasticpath.rest.resource.recommendations.RecommendationGroup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Class for handling Item Recommendations Resource.
 */
@Singleton
@Named("itemRecommendationsResourceOperator")
@Path({ResourceName.PATH_PART})
public final class ItemRecommendationsResourceOperatorImpl implements ResourceOperator {

	private final ItemRecommendationsLookup itemRecommendationsLookup;

	/**
	 * Constructor.
	 *
	 * @param itemRecommendationsLookup the item recommendations lookup
	 */
	@Inject
	ItemRecommendationsResourceOperatorImpl(
			@Named("itemRecommendationsLookup")
			final ItemRecommendationsLookup itemRecommendationsLookup) {

		this.itemRecommendationsLookup = itemRecommendationsLookup;
	}


	/**
	 * Handle read operations for recommendations of item resources.
	 *
	 * @param itemEntityResourceState the item representation to be used in the read.
	 * @param operation the resource operation.
	 * @return the result.
	 */
	@Path(SingleResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadItemRecommendations(
			@SingleResourceUri
			final ResourceState<ItemEntity> itemEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> result =
				itemRecommendationsLookup.getRecommendations(itemEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process a READ for recommended items by recommendations ID.
	 *
	 * @param itemEntityResourceState the item representation to be used in the read.
	 * @param recommendationGroup the group name of the recommendations
	 * @param operation the resource operation
	 * @return the operation result containing the first page of recommended items
	 */
	@Path({SingleResourceUri.PATH_PART, RecommendationGroup.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadRecommendedItemsForItem(
			@SingleResourceUri
			final ResourceState<ItemEntity> itemEntityResourceState,
			@RecommendationGroup
			final String recommendationGroup,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				itemRecommendationsLookup.getRecommendedItemsFromGroup(itemEntityResourceState, recommendationGroup,
						PaginationResourceConstants.FIRST_PAGE);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process a paged READ for recommended items by recommendations ID.
	 *
	 * @param itemEntityResourceState the item representation to be used in the read.
	 * @param recommendationGroup the group name of the recommendations
	 * @param operation the resource operation
	 * @param pageNumber the page number
	 * @return the operation result containing the requested page of recommended items
	 */
	@Path({SingleResourceUri.PATH_PART, RecommendationGroup.PATH_PART, PageNumber.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processPagedReadRecommendedItemsForItem(
			@SingleResourceUri
			final ResourceState<ItemEntity> itemEntityResourceState,
			@RecommendationGroup
			final String recommendationGroup,
			@PageNumber
			final String pageNumber,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				itemRecommendationsLookup.getRecommendedItemsFromGroup(itemEntityResourceState, recommendationGroup,
						Integer.parseInt(pageNumber));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

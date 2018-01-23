/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.navigations.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.pagination.constant.PaginationResourceConstants;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.navigations.Navigations;
import com.elasticpath.rest.resource.searches.navigations.lookup.NavigationsItemsSearchesLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Resource operator for navigations item search.
 */
@Singleton
@Named("navigationsSearchesResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Navigations.PATH_PART, Items.PATH_PART, ResourceId.PATH_PART})
public final class NavigationsSearchesResourceOperatorImpl implements ResourceOperator {

	private final NavigationsItemsSearchesLookup navigationsItemsSearchesLookup;
	private final String resourceName;


	/**
	 * Default Constructor.
	 *
	 * @param resourceName the resource server name
	 * @param navigationsItemsSearchesLookup for reading items of item navigations.
	 */
	@Inject
	public NavigationsSearchesResourceOperatorImpl(
			@Named("resourceServerName")
			final String resourceName,
			@Named("navigationsItemsSearchesLookup")
			final NavigationsItemsSearchesLookup navigationsItemsSearchesLookup) {

		this.resourceName = resourceName;
		this.navigationsItemsSearchesLookup = navigationsItemsSearchesLookup;
	}


	/**
	 * Process read.
	 *
	 * @param scope the scope
	 * @param encodedSearchString the encoded search string
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@Scope
			final String scope,
			@ResourceId
			final String encodedSearchString,
			final ResourceOperation operation) {

		String baseUri = URIUtil.format(resourceName, scope, Navigations.URI_PART,
				Items.URI_PART, encodedSearchString);

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				navigationsItemsSearchesLookup.find(scope, baseUri, encodedSearchString, PaginationResourceConstants.FIRST_PAGE);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process read with page number.
	 *
	 * @param scope the scope
	 * @param encodedSearchString the encoded search string
	 * @param pageNumber the page number
	 * @param operation the Resource Operation
	 * @return the operation result
	 */
	@Path(PageNumber.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadWithPageNumber(
			@Scope
			final String scope,
			@ResourceId
			final String encodedSearchString,
			@PageNumber
			final String pageNumber,
			final ResourceOperation operation) {

		String baseUri = URIUtil.format(resourceName, scope, Navigations.URI_PART,
				Items.URI_PART, encodedSearchString);

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				navigationsItemsSearchesLookup.find(scope, baseUri, encodedSearchString, Integer.parseInt(pageNumber));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

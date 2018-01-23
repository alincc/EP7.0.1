/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.keywords.impl;

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
import com.elasticpath.rest.resource.searches.keywords.Keywords;
import com.elasticpath.rest.resource.searches.keywords.lookup.KeywordsSearchesLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Resource operator for keywords search.
 */
@Singleton
@Named("keywordsSearchesResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Keywords.PATH_PART, Items.PATH_PART, ResourceId.PATH_PART})
public final class KeywordsSearchesResourceOperatorImpl implements ResourceOperator {


	private final KeywordsSearchesLookup keywordsSearchesLookup;
	private final String resourceServerName;

	/**
	 * Constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param keywordsSearchesLookup for reading items by keyword search.
	 */
	@Inject
	public KeywordsSearchesResourceOperatorImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("keywordsSearchesLookup")
			final KeywordsSearchesLookup keywordsSearchesLookup) {

		this.resourceServerName = resourceServerName;
		this.keywordsSearchesLookup = keywordsSearchesLookup;
	}

	/**
	 * Process READ operation on Search.
	 *
	 * @param scope the scope
	 * @param searchKeywords the search keywords
	 * @param operation The Resource Operation
	 * @return the operation result containing the search representation
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@Scope
			final String scope,
			@ResourceId
			final String searchKeywords,
			final ResourceOperation operation) {

		String baseUri = URIUtil.format(resourceServerName, scope, Keywords.URI_PART, Items.URI_PART, searchKeywords);

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				keywordsSearchesLookup.findItemsByKeywords(scope, baseUri, searchKeywords, PaginationResourceConstants.FIRST_PAGE);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process READ operation on Search.
	 *
	 * @param scope the scope
	 * @param searchKeywords the search keywords
	 * @param pageNumber the page number
	 * @param operation the Resource Operation
	 * @return the operation result containing the search representation
	 */
	@Path(PageNumber.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processPagedRead(
			@Scope
			final String scope,
			@ResourceId
			final String searchKeywords,
			@PageNumber
			final String pageNumber,
			final ResourceOperation operation) {

		String baseUri = URIUtil.format(resourceServerName, scope, Keywords.URI_PART, Items.URI_PART, searchKeywords);

		ExecutionResult<ResourceState<PaginatedLinksEntity>> result =
				keywordsSearchesLookup.findItemsByKeywords(scope, baseUri, searchKeywords, Integer.parseInt(pageNumber));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

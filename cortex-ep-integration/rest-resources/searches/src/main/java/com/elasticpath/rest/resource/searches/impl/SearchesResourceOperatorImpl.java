/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.searches.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.Command;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.searches.SearchKeywordsEntity;
import com.elasticpath.rest.definition.searches.SearchesMediaTypes;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.searches.command.CreateSearchUriCommand;
import com.elasticpath.rest.resource.searches.command.ReadKeywordsFormCommand;
import com.elasticpath.rest.resource.searches.keywords.Items;
import com.elasticpath.rest.resource.searches.keywords.Keywords;
import com.elasticpath.rest.resource.searches.rel.SearchesResourceRels;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Processes the resource operation on Search.
 */
@Singleton
@Named("searchesResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART})
public final class SearchesResourceOperatorImpl implements ResourceOperator {

	private final String resourceServerName;
	private final Provider<ReadKeywordsFormCommand.Builder> readKeywordsFormCommandBuilder;
	private final Provider<CreateSearchUriCommand.Builder> createSearchUriCommandBuilder;
	private final Validator<SearchKeywordsEntity> keywordsFormValidator;

	/**
	 * Constructor.
	 * @param resourceServerName the resource server name.
	 * @param readKeywordsFormCommandBuilder readKeywordsFormCommandBuilder provider.
	 * @param createSearchUriCommandBuilder createSearchUriCommandBuilder provider.
	 * @param keywordsFormValidator the form validator.
	 */
	@Inject
	SearchesResourceOperatorImpl(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("readKeywordsFormCommandBuilder")
			final Provider<ReadKeywordsFormCommand.Builder> readKeywordsFormCommandBuilder,
			@Named("createSearchUriCommandBuilder")
			final Provider<CreateSearchUriCommand.Builder> createSearchUriCommandBuilder,
			@Named("keywordsFormValidator")
			final Validator<SearchKeywordsEntity> keywordsFormValidator) {
		this.resourceServerName = resourceServerName;
		this.readKeywordsFormCommandBuilder = readKeywordsFormCommandBuilder;
		this.createSearchUriCommandBuilder = createSearchUriCommandBuilder;
		this.keywordsFormValidator = keywordsFormValidator;
	}


	/**
	 * Process READ operation on Search.
	 *
	 * @param scope the scope
	 * @param operation the Resource Operation.
	 * @return the operation result containing the search representation
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		Self self = SelfFactory.createSelf(URIUtil.format(resourceServerName, scope));
		String formUri = URIUtil.format(resourceServerName, scope, Keywords.URI_PART, Form.URI_PART);
		ResourceLink formLink = ResourceLinkFactory.createNoRev(formUri, SearchesMediaTypes.SEARCH_KEYWORDS.id(), SearchesResourceRels.SEARCH_REL);
		ResourceState<LinksEntity> searchResourceState = ResourceState.Builder
				.create(LinksEntity.builder().build())
				.withSelf(self)
				.addingLinks(formLink)
				.build();

		return OperationResultFactory.createReadOK(searchResourceState, operation);
	}

	/**
	 * Process read on keywords form.
	 *
	 * @param scope the scope
	 * @param operation the Resource Operation.
	 * @return the operation result
	 */
	@Path({Keywords.PATH_PART, Form.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processKeywordsFormRead(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		Command<ResourceState<SearchKeywordsEntity>> readKeywordsFormCommand = readKeywordsFormCommandBuilder.get()
				.setScope(scope)
				.build();

		ExecutionResult<ResourceState<SearchKeywordsEntity>> result = readKeywordsFormCommand.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process post on items keywords search.
	 *
	 * @param scope the scope
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({Keywords.PATH_PART, Items.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processSearchItemKeywords(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		SearchKeywordsEntity searchForm = getPostedEntity(operation);

		CreateSearchUriCommand createSearchUriCommand = createSearchUriCommandBuilder.get()
				.setScope(scope)
				.setSearchPath(Items.URI_PART)
				.setSearchForm(searchForm)
				.build();

		ExecutionResult<ResourceState<ResourceEntity>> result = createSearchUriCommand.execute();

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	private SearchKeywordsEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> resourceState = Assign.ifNotNull(operation.getResourceState(),
				OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		SearchKeywordsEntity searchForm = ResourceTypeFactory.adaptResourceEntity(resourceState.getEntity(), SearchKeywordsEntity.class);
		Ensure.successful(keywordsFormValidator.validate(searchForm));
		return searchForm;
	}
}

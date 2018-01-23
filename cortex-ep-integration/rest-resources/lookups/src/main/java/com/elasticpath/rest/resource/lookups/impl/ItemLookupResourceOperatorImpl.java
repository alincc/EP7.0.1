/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import static com.elasticpath.rest.ResourceTypeFactory.adaptResourceEntity;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.definition.lookups.LookupsMediaTypes;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.lookups.Items;
import com.elasticpath.rest.resource.lookups.constant.LookupConstants;
import com.elasticpath.rest.resource.lookups.integration.ItemLookupLookupStrategy;
import com.elasticpath.rest.resource.lookups.rels.LookupResourceRels;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilder;
import com.elasticpath.rest.schema.uri.ItemLookupUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Operator for item lookup.
 */
@Singleton
@Named("itemLookupResourceOperator")
@Path({ ResourceName.PATH_PART })
public final class ItemLookupResourceOperatorImpl implements ResourceOperator {

	private final ItemLookupLookupStrategy itemLookupLookupStrategy;
	private final ItemLookupUriBuilderFactory itemLookupUriBuilderFactory;
	private final ItemsUriBuilderFactory itemsUriBuilderFactory;
	private final Validator<CodeEntity> itemCodeFormValidator;
	private final TransformRfoToResourceState<CodeEntity, CodeEntity, ItemEntity> codeEntityTransformer;

	/**
	 * Constructor.
	 * @param itemsUriBuilderFactory the item URI builder factory.
	 * @param itemLookupUriBuilderFactory the item lookup URI builder factory.
	 * @param codeEntityTransformer the code entity transformer.
	 * @param itemLookupLookupStrategy the lookup strategy for item lookups.
	 * @param itemCodeFormValidator the form validator.
	 */
	@Inject
	ItemLookupResourceOperatorImpl(
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilderFactory,
			@Named("itemLookupUriBuilderFactory")
			final ItemLookupUriBuilderFactory itemLookupUriBuilderFactory,
			@Named("codeEntityTransformer")
			final TransformRfoToResourceState<CodeEntity, CodeEntity, ItemEntity> codeEntityTransformer,
			@Named("itemLookupLookupStrategy")
			final ItemLookupLookupStrategy itemLookupLookupStrategy,
			@Named("itemCodeFormValidator")
			final Validator<CodeEntity> itemCodeFormValidator) {

		this.itemsUriBuilderFactory = itemsUriBuilderFactory;
		this.itemLookupUriBuilderFactory = itemLookupUriBuilderFactory;
		this.itemLookupLookupStrategy = itemLookupLookupStrategy;
		this.itemCodeFormValidator = itemCodeFormValidator;
		this.codeEntityTransformer = codeEntityTransformer;
	}

	/**
	 * Handles the READ operations for root lookups.
	 *
	 * @param scope the scope
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(Scope.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadLookups(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		LinksEntity linksEntity = LinksEntity.builder().build();
		ItemLookupUriBuilder uriBuilder = itemLookupUriBuilderFactory.get();
		String selfUri = uriBuilder.setScope(scope).build();
		String formUri = uriBuilder.setFormPart().build();

		Self self = SelfFactory.createSelf(selfUri);
		ResourceLink formLink =
			ResourceLinkFactory.createNoRev(formUri, LookupsMediaTypes.CODE.id(), LookupResourceRels.ITEM_LOOKUP_FORM_REL);

		ResourceState<LinksEntity> lookupLinks = ResourceState.Builder.create(linksEntity)
			.withSelf(self)
			.withResourceInfo(LookupConstants.TEN_MINUTES)
			.addingLinks(formLink)
			.build();

		return OperationResultFactory.createReadOK(lookupLinks, operation);
	}

	/**
	 * Handles the READ operations for item code lookup.
	 *
	 * @param item the item entity
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadCodeForItem(
			@AnyResourceUri
			final ResourceState<ItemEntity> item,
			final ResourceOperation operation) {

		String itemId = item.getEntity().getItemId();
		CodeEntity codeEntity = Assign.ifSuccessful(itemLookupLookupStrategy.getItemLookupByItem(itemId));
		ExecutionResult<ResourceState<CodeEntity>> result =
				ExecutionResultFactory.createReadOK(codeEntityTransformer.transform(codeEntity, item));

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Process read on item code search form.
	 *
	 * @param scope the scope
	 * @param operation the Resource Operation.
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, Items.PATH_PART, Form.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processItemLookupFormRead(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		ItemLookupUriBuilder uriBuilder = itemLookupUriBuilderFactory.get();
		String submitSearchUri = uriBuilder.setScope(scope).setItemsPart().build();
		String formUri = uriBuilder.setFormPart().build();
		Self self = SelfFactory.createSelf(formUri);
		ResourceLink searchActionLink = ResourceLinkFactory.createNoRev(
			submitSearchUri,
			LookupsMediaTypes.CODE.id(),
			LookupResourceRels.ITEM_LOOKUP_ACTION_REL);
		ResourceState<CodeEntity> lookupForm = ResourceState.Builder
			.create(CodeEntity.builder()
				.withCode(StringUtils.EMPTY)
				.build())
			.withSelf(self)
			.withResourceInfo(LookupConstants.TEN_MINUTES)
			.addingLinks(searchActionLink)
				.build();

		return OperationResultFactory.createReadOK(lookupForm, operation);
	}

	/**
	 * Process post on item code search.
	 *
	 * @param scope the scope
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({Scope.PATH_PART, Items.PATH_PART})
	@OperationType(Operation.CREATE)
	public OperationResult processItemCodeSearch(
			@Scope
			final String scope,
			final ResourceOperation operation) {

		CodeEntity codeEntity = getPostedEntity(operation);
		String itemId = Assign.ifSuccessful(itemLookupLookupStrategy.getItemIdByCode(codeEntity.getCode()));

		String itemUri = itemsUriBuilderFactory.get().setScope(scope).setItemId(itemId).build();

		ExecutionResult<ResourceState<ResourceEntity>> result = ExecutionResultFactory.createCreateOK(itemUri, false);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	private CodeEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> resourceState = Assign.ifNotNull(operation.getResourceState(),
				OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		CodeEntity codeEntity = adaptResourceEntity(resourceState.getEntity(), CodeEntity.class);
		Ensure.successful(itemCodeFormValidator.validate(codeEntity));
		return codeEntity;
	}
}

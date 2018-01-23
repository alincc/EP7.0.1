/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import static com.elasticpath.rest.ResourceTypeFactory.adaptResourceEntity;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.Ensure;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.lookups.BatchItemsActionIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsFormIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.definition.lookups.CodesEntity;
import com.elasticpath.rest.definition.lookups.LookupsMediaTypes;
import com.elasticpath.rest.definitions.validator.Validator;
import com.elasticpath.rest.definitions.validator.constants.ValidationMessages;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.transform.ResourceIdentifierTransformer;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Scope;
import com.elasticpath.rest.resource.lookups.Batches;
import com.elasticpath.rest.resource.lookups.Items;
import com.elasticpath.rest.resource.lookups.constant.LookupConstants;
import com.elasticpath.rest.resource.lookups.integration.ItemLookupLookupStrategy;
import com.elasticpath.rest.resource.lookups.rels.LookupResourceRels;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;

/**
 * Operator for Batch items lookup.
 */
@Singleton
@Named("batchItemsLookupResourceOperator")
@Path({ResourceName.PATH_PART, Scope.PATH_PART, Batches.PATH_PART, Items.PATH_PART})
public class BatchItemsLookupResourceOperatorImpl implements ResourceOperator {

	private final ItemLookupLookupStrategy itemLookupLookupStrategy;
	private final Validator<CodesEntity> itemCodesFormValidator;
	private final IdentifierTransformerProvider idTransformerProvider;

	/**
	 * Constructor.
	 *
	 * @param itemLookupLookupStrategy lookup lookups. heh heh.
	 * @param itemCodesFormValidator   validate the batch form
	 * @param idTransformerProvider    identifier Transformer provider
	 */
	@Inject
	public BatchItemsLookupResourceOperatorImpl(
			@Named("itemLookupLookupStrategy")
			final ItemLookupLookupStrategy itemLookupLookupStrategy,
			@Named("itemCodesFormValidator")
			final Validator<CodesEntity> itemCodesFormValidator,
			@Named("identifierTransformerProvider")
			final IdentifierTransformerProvider idTransformerProvider) {

		this.itemLookupLookupStrategy = itemLookupLookupStrategy;
		this.itemCodesFormValidator = itemCodesFormValidator;
		this.idTransformerProvider = idTransformerProvider;
	}


	/**
	 * Read the batch items form.
	 *
	 * @param operation resource operation
	 * @return operation result
	 */
	@Path(Form.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadBatchItemsForm(final ResourceOperation operation) {

		BatchItemsFormIdentifier formId = idTransformerProvider.forClass(BatchItemsFormIdentifier.class)
				.uriToIdentifier(operation.getUri());
		BatchItemsActionIdentifier actionId = BatchItemsActionIdentifier.builder()
				.withLookups(formId.getBatchItemsAction().getLookups())
				.build();
		String actionUri = idTransformerProvider.forClass(BatchItemsActionIdentifier.class)
				.identifierToUri(actionId);
		ResourceLink actionLink = ResourceLinkFactory.createNoRev(
				actionUri, LookupsMediaTypes.CODES.id(), LookupResourceRels.BATCH_ITEMS_LOOKUP_ACTION_REL);

		CodesEntity form = CodesEntity.builder()
				.withCodes(ImmutableList.of(StringUtils.EMPTY))
				.build();

		Self self = SelfFactory.createSelf(operation.getUri());
		ResourceState<?> state = ResourceState.builder()
				.withEntity(form)
				.addingLinks(actionLink)
				.withSelf(self)
				.withResourceInfo(LookupConstants.TEN_MINUTES)
				.build();

		return OperationResultFactory.createReadOK(state, operation);
	}


	/**
	 * Process the submitted batch form and return a uri that points to the batch.
	 *
	 * @param operation resource operation
	 * @return operation result
	 */
	@Path
	@OperationType(Operation.CREATE)
	public OperationResult processBatchItemsFormSubmission(final ResourceOperation operation) {

		CodesEntity codes = getPostedEntity(operation);

		IdentifierPart<List<String>> batchId = itemLookupLookupStrategy.getBatchIdForCodes(codes.getCodes());

		BatchItemsActionIdentifier actionId = idTransformerProvider
				.forClass(BatchItemsActionIdentifier.class)
				.uriToIdentifier(operation.getUri());

		BatchItemsIdentifier batchItemsId = BatchItemsIdentifier.builder()
				.withBatchItemsAction(BatchItemsActionIdentifier.builder().withLookups(actionId.getLookups()).build())
				.withBatchId(batchId)
				.build();

		String batchUri = idTransformerProvider.forClass(BatchItemsIdentifier.class)
				.identifierToUri(batchItemsId);

		Self self = SelfFactory.createSelf(batchUri);
		ResourceState<?> state = ResourceState.builder()
				.withSelf(self)
				.build();

		return OperationResultFactory.create(operation, ResourceStatus.CREATE_OK, state);
	}

	/**
	 * Read a batch of items.
	 *
	 * @param operation resource operation
	 * @return operation result
	 */
	@Path(ResourceId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadBatchItems(final ResourceOperation operation) {

		BatchItemsIdentifier batchItemsid = idTransformerProvider.forClass(BatchItemsIdentifier.class)
				.uriToIdentifier(operation.getUri());

		Iterable<ItemIdentifier> itemIdsForBatchId = itemLookupLookupStrategy.getItemIdsForBatchId(batchItemsid);
		ResourceIdentifierTransformer<ItemIdentifier> transformer = idTransformerProvider.forClass(ItemIdentifier.class);

		FluentIterable<ResourceLink> itemLinks = FluentIterable.from(itemIdsForBatchId)
				.transform(transformer::identifierToUri)
				.transform(uri -> ElementListFactory.createElement(uri, ItemsMediaTypes.ITEM.id()));

		Self self = SelfFactory.createSelf(operation.getUri());
		ResourceState<?> state = ResourceState.builder()
				.withSelf(self)
				.withEntity(LinksEntity.builder().build())
				.addingLinks(itemLinks)
				.build();

		return OperationResultFactory.create(operation, ResourceStatus.READ_OK, state);
	}

	private CodesEntity getPostedEntity(final ResourceOperation operation) {
		ResourceState<?> resourceState = Assign.ifNotNull(operation.getResourceState(),
				OnFailure.returnBadRequestBody(ValidationMessages.MISSING_REQUIRED_REQUEST_BODY));
		CodesEntity codes = adaptResourceEntity(resourceState.getEntity(), CodesEntity.class);
		Ensure.successful(itemCodesFormValidator.validate(codes));
		return codes;
	}
}

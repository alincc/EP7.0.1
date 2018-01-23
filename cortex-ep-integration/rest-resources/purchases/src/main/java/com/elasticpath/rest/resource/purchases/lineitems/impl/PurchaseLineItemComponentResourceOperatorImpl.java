/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Reads a component of a bundled purchase line item.
 */
@Singleton
@Named("purchaseLineItemComponentResourceOperatorImpl")
@Path({AnyResourceUri.PATH_PART, Components.PATH_PART, ResourceId.PATH_PART})
public final class PurchaseLineItemComponentResourceOperatorImpl implements ResourceOperator {

	private final PurchaseLineItemLookup purchaseLineItemLookup;

	/**
	 * Default constructor.
	 *
	 * @param purchaseLineItemLookup the purchase line item lookup
	 */
	@Inject
	PurchaseLineItemComponentResourceOperatorImpl(
			@Named("purchaseLineItemLookup")
			final PurchaseLineItemLookup purchaseLineItemLookup) {

		this.purchaseLineItemLookup = purchaseLineItemLookup;
	}


	/**
	 * Process READ operation on a single line item element for a purchase.
	 *
	 * @param parentLineItem the parent line item
	 * @param componentId the component id
	 * @param operation the Resource Operation
	 * @return the {@link com.elasticpath.rest.OperationResult} with a links {@link ResourceState}
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadLineItemComponent(
			@AnyResourceUri
			final ResourceState<PurchaseLineItemEntity> parentLineItem,
			@ResourceId
			final String componentId,
			final ResourceOperation operation) {

		String parentUri = ResourceStateUtil.getSelfUri(parentLineItem);
		PurchaseLineItemEntity entity = parentLineItem.getEntity();
		ExecutionResult<ResourceState<PurchaseLineItemEntity>> result =
				purchaseLineItemLookup.getPurchaseLineItem(parentLineItem.getScope(), entity.getPurchaseId(),
						componentId, parentUri, entity.getLineItemId());
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

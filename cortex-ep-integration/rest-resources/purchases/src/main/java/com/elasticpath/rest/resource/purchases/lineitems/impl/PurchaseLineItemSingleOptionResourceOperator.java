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
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OptionId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Read a single option of a purchase line item.
 */
@Singleton
@Named("purchaseLineItemSingleOptionResourceOperator")
@Path({AnyResourceUri.PATH_PART, Options.PATH_PART})
public final class PurchaseLineItemSingleOptionResourceOperator implements ResourceOperator {

	private final PurchaseLineItemOptionsLookup purchaseLineItemOptionsLookup;


	/**
	 * Default constructor.
	 *
	 * @param purchaseLineItemOptionsLookup the purchase line item options lookup
	 */
	@Inject
	PurchaseLineItemSingleOptionResourceOperator(
			@Named("purchaseLineItemOptionsLookup")
			final PurchaseLineItemOptionsLookup purchaseLineItemOptionsLookup) {

		this.purchaseLineItemOptionsLookup = purchaseLineItemOptionsLookup;
	}


	/**
	 * Process READ operation on line item option.
	 *
	 * @param purchaseLineItem the purchase line item
	 * @param optionId the option id
	 * @param operation the Resource Operation
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path(OptionId.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadOption(
			@AnyResourceUri
			final ResourceState<PurchaseLineItemEntity> purchaseLineItem,
			@OptionId
			final String optionId,
			final ResourceOperation operation) {

		PurchaseLineItemEntity entity = purchaseLineItem.getEntity();
		String scope = purchaseLineItem.getScope();
		String purchaseId = entity.getPurchaseId();
		String lineItemId = entity.getLineItemId();
		String lineItemUri = ResourceStateUtil.getSelfUri(purchaseLineItem);

		ExecutionResult<ResourceState<PurchaseLineItemOptionEntity>> result =
				purchaseLineItemOptionsLookup.findOption(scope, purchaseId, lineItemId, optionId, lineItemUri);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

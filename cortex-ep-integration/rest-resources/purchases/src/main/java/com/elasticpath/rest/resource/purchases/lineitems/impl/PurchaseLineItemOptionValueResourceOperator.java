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
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionValueEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OptionId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ValueId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemOptionsLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Command for reading a purchase line item option value.
 */
@Singleton
@Named("purchaseLineItemOptionValueResourceOperator")
@Path({AnyResourceUri.PATH_PART, Options.PATH_PART})
public final class PurchaseLineItemOptionValueResourceOperator implements ResourceOperator {

	private final PurchaseLineItemOptionsLookup purchaseLineItemOptionsLookup;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemOptionsLookup the purchase line item options lookup
	 */
	@Inject
	PurchaseLineItemOptionValueResourceOperator(
			@Named("purchaseLineItemOptionsLookup")
			final PurchaseLineItemOptionsLookup purchaseLineItemOptionsLookup) {

		this.purchaseLineItemOptionsLookup = purchaseLineItemOptionsLookup;
	}


	/**
	 * Process READ operation on line item option.
	 *
	 * @param purchaseLineItem the purchase line item
	 * @param optionId the option id
	 * @param valueId the value id
	 * @param operation the Resource Operation
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path({OptionId.PATH_PART, Values.PATH_PART, ValueId.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processReadOptionValue(
			@AnyResourceUri
			final ResourceState<PurchaseLineItemEntity> purchaseLineItem,
			@OptionId
			final String optionId,
			@ValueId
			final String valueId,
			final ResourceOperation operation) {

		PurchaseLineItemEntity entity = purchaseLineItem.getEntity();
		String scope = purchaseLineItem.getScope();
		String purchaseId = entity.getPurchaseId();
		String lineItemId = entity.getLineItemId();
		String lineitemUri = ResourceStateUtil.getSelfUri(purchaseLineItem);

		ExecutionResult<ResourceState<PurchaseLineItemOptionValueEntity>> result =
				purchaseLineItemOptionsLookup.findOptionValueForLineItem(scope, purchaseId, lineItemId, optionId, valueId, lineitemUri);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}
}

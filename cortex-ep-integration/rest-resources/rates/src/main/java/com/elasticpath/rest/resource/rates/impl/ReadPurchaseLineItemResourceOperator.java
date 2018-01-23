/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.rates.integration.PurchaseLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * For reading purchase line item rates.
 */
@Singleton
@Named("readPurchaseLineItemResourceOperator")
@Path(ResourceName.PATH_PART)
public class ReadPurchaseLineItemResourceOperator implements ResourceOperator {

	private final PurchaseLineItemRateLookupStrategy purchaseLineItemRateLookupStrategy;

	/**
	 * Constructor.
	 *
	 * @param purchaseLineItemRateLookupStrategy purchase line item price lookup
	 */
	@Inject
	ReadPurchaseLineItemResourceOperator(
			@Named("purchaseLineItemRateLookupStrategy")
			final PurchaseLineItemRateLookupStrategy purchaseLineItemRateLookupStrategy) {

		this.purchaseLineItemRateLookupStrategy = purchaseLineItemRateLookupStrategy;
	}

	/**
	 * Handles the READ operations for rates on purchase line items.
	 *
	 * @param lineItem the purchase line item
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<PurchaseLineItemEntity> lineItem,
			final ResourceOperation operation) {

		String purchaseId = Base32Util.decode(lineItem.getEntity().getPurchaseId());
		String lineItemId = Base32Util.decode(lineItem.getEntity().getLineItemId());
		RateEntity rateEntity = Assign.ifSuccessful(purchaseLineItemRateLookupStrategy.getLineItemRate(lineItem.getScope(),	purchaseId, lineItemId));

		ResourceLink linkToLineItem = ResourceLinkFactory.createFromSelf(lineItem.getSelf(), RateRepresentationRels.LINE_ITEM_REL,
				RateRepresentationRels.RATE_REV);

		ResourceState<RateEntity> rate = ResourceState.Builder.create(rateEntity)
				.withSelf(SelfFactory.createSelf(operation.getUri()))
				.addingLinks(linkToLineItem)
				.build();
		return OperationResultFactory.createReadOK(rate, operation);
	}
}

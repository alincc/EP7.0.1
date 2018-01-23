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
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.rates.integration.CartLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * For reading cart line item rates.
 */
@Singleton
@Named("readCartLineItemResourceOperator")
@Path(ResourceName.PATH_PART)
final class ReadCartLineItemResourceOperator implements ResourceOperator {

	private final CartLineItemRateLookupStrategy cartLineItemRateLookupStrategy;


	/**
	 * Constructor.
	 *
	 * @param cartLineItemRateLookupStrategy cart line item price lookup
	 */
	@Inject
	ReadCartLineItemResourceOperator(
			@Named("cartLineItemRateLookupStrategy")
			final CartLineItemRateLookupStrategy cartLineItemRateLookupStrategy) {

		this.cartLineItemRateLookupStrategy = cartLineItemRateLookupStrategy;
	}


	/**
	 * Handles the READ operations for rates on cart line items.
	 *
	 * @param lineItem the line item
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<LineItemEntity> lineItem,
			final ResourceOperation operation) {

		String cartId = Base32Util.decode(lineItem.getEntity().getCartId());
		String lineItemId = Base32Util.decode(lineItem.getEntity().getLineItemId());
		RateEntity rateEntity = Assign.ifSuccessful(cartLineItemRateLookupStrategy.getLineItemRate(lineItem.getScope(),	cartId, lineItemId));

		ResourceLink linkToLineItem = ResourceLinkFactory.createFromSelf(lineItem.getSelf(), RateRepresentationRels.LINE_ITEM_REL,
				RateRepresentationRels.RATE_REV);

		ResourceState<RateEntity> rate = ResourceState.Builder.create(rateEntity)
				.withSelf(SelfFactory.createSelf(operation.getUri()))
				.addingLinks(linkToLineItem)
				.build();
		return OperationResultFactory.createReadOK(rate, operation);

	}
}
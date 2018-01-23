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
import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.rates.integration.ItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * For reading item rates.
 */
@Singleton
@Named("readItemRatesResourceOperator")
@Path(ResourceName.PATH_PART)
final class ReadItemRatesResourceOperator implements ResourceOperator {

	private final ItemRateLookupStrategy itemRateLookupStrategy;


	/**
	 * Constructor.
	 *
	 * @param itemRateLookupStrategy item rate lookup
	 */
	@Inject
	ReadItemRatesResourceOperator(
			@Named("itemRateLookupStrategy")
			final ItemRateLookupStrategy itemRateLookupStrategy) {

		this.itemRateLookupStrategy = itemRateLookupStrategy;
	}


	/**
	 * Handles the READ operations for rates on items.
	 *
	 * @param item the item
	 * @param operation the resource operation
	 * @return the result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@AnyResourceUri
			final ResourceState<ItemEntity> item,
			final ResourceOperation operation) {

		String itemId = Base32Util.decode(item.getEntity().getItemId());
		RateEntity rateEntity = Assign.ifSuccessful(itemRateLookupStrategy.getItemRate(item.getScope(), itemId));

		ResourceLink linkToItem = ResourceLinkFactory.createFromSelf(item.getSelf(), RateRepresentationRels.ITEM_REL,
				RateRepresentationRels.RATE_REV);

		Self self = SelfFactory.createSelf(operation.getUri());
		ResourceState<RateEntity> rate = ResourceState.Builder.create(rateEntity)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(RateRepresentationRels.MAX_AGE)
						.build())
				.addingLinks(linkToItem)
				.build();
		return OperationResultFactory.createReadOK(rate, operation);

	}
}

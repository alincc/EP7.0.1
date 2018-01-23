/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.other.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.promotions.AppliedPromotions;
import com.elasticpath.rest.resource.promotions.ShippingOptionPromotionsLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Implementation of ResourceOperator for applied ShippingOption Promotions.
 */
@Singleton
@Named("appliedPromotionsForShippingOptionResourceOperator")
@Path(ResourceName.PATH_PART)
public final class AppliedPromotionsForShippingOptionResourceOperator implements ResourceOperator {

	private final ShippingOptionPromotionsLookup shippingOptionPromotionsLookup;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionPromotionsLookup the promotions lookup.
	 */
	@Inject
	AppliedPromotionsForShippingOptionResourceOperator(
			@Named("shippingOptionPromotionsLookup")
			final ShippingOptionPromotionsLookup shippingOptionPromotionsLookup) {
		this.shippingOptionPromotionsLookup = shippingOptionPromotionsLookup;
	}


	/**
	 * Handles reading applied promotions for shipping option.
	 *
	 * @param shippingOptionEntityResourceState the shipping option representation
	 * @param operation the {@link com.elasticpath.rest.ResourceOperation}
	 * @return the {@link com.elasticpath.rest.OperationResult}
	 */
	@Path({AnyResourceUri.PATH_PART, AppliedPromotions.PATH_PART})
	@OperationType(Operation.READ)
	public OperationResult processAppliedPromotionsForShippingOption(
			@AnyResourceUri
			final ResourceState<ShippingOptionEntity> shippingOptionEntityResourceState,
			final ResourceOperation operation) {

		ExecutionResult<ResourceState<LinksEntity>> appliedPromotionsForShippingOption =
				shippingOptionPromotionsLookup.getAppliedPromotionsForShippingOption(shippingOptionEntityResourceState);

		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory
				.create(appliedPromotionsForShippingOption, operation);
	}
}

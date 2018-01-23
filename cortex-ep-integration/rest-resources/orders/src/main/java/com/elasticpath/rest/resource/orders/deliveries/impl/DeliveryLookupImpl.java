/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.orders.deliveries.DeliveryLookup;
import com.elasticpath.rest.resource.orders.deliveries.transformer.DeliveryTransformer;
import com.elasticpath.rest.resource.orders.integration.deliveries.DeliveryLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Lookup for order delivery.
 */
@Singleton
@Named("deliveryLookup")
public final class DeliveryLookupImpl implements DeliveryLookup {

	private final DeliveryLookupStrategy deliveryLookupStrategy;
	private final DeliveryTransformer deliveryTransformer;

	/**
	 * Default constructor.
	 *
	 * @param deliveryLookupStrategy the delivery lookup strategy
	 * @param deliveryTransformer the delivery transformer
	 */
	@Inject
	public DeliveryLookupImpl(
			@Named("deliveryLookupStrategy")
			final DeliveryLookupStrategy deliveryLookupStrategy,
			@Named("deliveryTransformer")
			final DeliveryTransformer deliveryTransformer) {

		this.deliveryLookupStrategy = deliveryLookupStrategy;
		this.deliveryTransformer = deliveryTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<DeliveryEntity>> findByIdAndOrderId(
			final String scope,
			final String orderId,
			final String deliveryId) {

		String decodedOrderId = Base32Util.decode(orderId);
		String decodedDeliveryId = Base32Util.decode(deliveryId);

		DeliveryEntity deliveryEntity = Assign.ifSuccessful(
				deliveryLookupStrategy.findByIdAndOrderId(scope, decodedOrderId, decodedDeliveryId));

		ResourceState<DeliveryEntity> deliveryRepresentation = deliveryTransformer.transformToRepresentation(scope, deliveryEntity, orderId);

		return ExecutionResultFactory.createReadOK(deliveryRepresentation);
	}

	@Override
	public ExecutionResult<Collection<String>> getDeliveryIds(final String scope, final String orderId) {
		String decodedOrderId = Base32Util.decode(orderId);
		Collection<String> deliveryIds = Assign.ifSuccessful(deliveryLookupStrategy.getDeliveryIds(scope, decodedOrderId));
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(deliveryIds));
	}
}

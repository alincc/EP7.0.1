/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.deliveries.transformer;

import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.orders.deliveries.Deliveries;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The transformer to convert an {@link DeliveryEntity} to {@link ResourceState}.
 */
@Singleton
@Named("deliveryTransformer")
public class DeliveryTransformer {

	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public DeliveryTransformer(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	/**
	 * Transforms an entity to a representation.
	 *
	 * @param scope          the scope
	 * @param deliveryEntity the delivery entity
	 * @param orderId        the order ID
	 * @return the representation.
	 */
	public ResourceState<DeliveryEntity> transformToRepresentation(final String scope,
																	final DeliveryEntity deliveryEntity,
																	final String orderId) {

		String deliveryId = Base32Util.encode(deliveryEntity.getDeliveryId());
		String orderUri = URIUtil.format(resourceServerName, scope, orderId);
		String deliveriesUri = URIUtil.format(orderUri, Deliveries.URI_PART);
		String selfUri = URIUtil.format(deliveriesUri, deliveryId);

		DeliveryEntity newDeliveryEntity = DeliveryEntity.builder()
				.withDeliveryType(deliveryEntity.getDeliveryType())
				.withOrderId(orderId)
				.withDeliveryId(deliveryId)
				.build();
		return ResourceState.Builder
				.create(newDeliveryEntity)
				.withSelf(createSelf(selfUri))
				.withScope(scope)
				.build();
	}
}

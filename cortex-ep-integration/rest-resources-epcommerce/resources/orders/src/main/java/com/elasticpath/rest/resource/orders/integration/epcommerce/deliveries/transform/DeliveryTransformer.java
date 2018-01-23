/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.wrapper.DeliveryWrapper;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms between {@link DeliveryWrapper} and {@link DeliveryEntity}, and vice versa.
 */
@Singleton
@Named("deliveryTransformer")
public class DeliveryTransformer extends AbstractDomainTransformer<DeliveryWrapper, DeliveryEntity> {

	@Override
	public DeliveryWrapper transformToDomain(final DeliveryEntity deliveryEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public DeliveryEntity transformToEntity(final DeliveryWrapper deliveryWrapper, final Locale locale) {
		return DeliveryEntity.builder()
				.withDeliveryId(deliveryWrapper.getDeliveryCode())
				.withDeliveryType(deliveryWrapper.getShipmentType())
				.build();
	}
}

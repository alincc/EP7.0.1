/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.transform;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.orders.DeliveryEntity;
import com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.wrapper.DeliveryWrapper;

/**
 * Tests the {@link DeliveryTransformer}.
 */
public class DeliveryTransformerTest {

	private static final String DELIVERY_CODE = "DELIVERY_CODE";
	private static final String SHIPMENT_TYPE = "SHIPMENT_TYPE";
	private final DeliveryTransformer deliveryTransformer = new DeliveryTransformer();

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		deliveryTransformer.transformToDomain(null);
	}

	/**
	 * Test internal transform to entity delivery core wrapper locale.
	 */
	@Test
	public void testInternalTransformToEntityDeliveryCoreWrapperLocale() {
		DeliveryWrapper deliveryWrapper = ResourceTypeFactory.createResourceEntity(DeliveryWrapper.class);
		deliveryWrapper.setDeliveryCode(DELIVERY_CODE)
				.setShipmentType(SHIPMENT_TYPE);

		DeliveryEntity deliveryEntity = deliveryTransformer.transformToEntity(deliveryWrapper, Locale.ENGLISH);

		DeliveryEntity expectedDeliveryEntity = DeliveryEntity.builder()
				.withDeliveryId(DELIVERY_CODE)
				.withDeliveryType(SHIPMENT_TYPE)
				.build();

		assertEquals("The transformed delivery dto must have the same properties as the expected.", expectedDeliveryEntity, deliveryEntity);
	}

}

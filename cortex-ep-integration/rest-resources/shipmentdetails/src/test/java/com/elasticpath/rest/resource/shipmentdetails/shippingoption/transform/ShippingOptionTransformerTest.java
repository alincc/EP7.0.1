/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.transform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test the behaviour of {@link ShippingOptionTransformer}.
 */
public final class ShippingOptionTransformerTest {

	private static final String DECODED_SHIPPING_OPTION_ID = "decoded_shipping_option_id";
	private static final String SHIPPING_OPTION_NAME = "shipping option name";
	private static final String DISPLAY_NAME = "Shipping Option";
	private static final String CARRIER = "FedEx";
	private static final String SHIPPING_OPTION_URI = "uri";
	private static final String SHIPPING_OPTION_ID = Base32Util.encode(DECODED_SHIPPING_OPTION_ID);

	private final ShippingOptionTransformer shippingOptionTransformer = new ShippingOptionTransformer();

	/**
	 * Test transform to representation.
	 */
	@Test
	public void testTransformToRepresentation() {
		CostEntity costEntity = ResourceTypeFactory.createResourceEntity(CostEntity.class);

		ShippingOptionDto shippingOptionDto = ResourceTypeFactory.createResourceEntity(ShippingOptionDto.class)
				.setCorrelationId(DECODED_SHIPPING_OPTION_ID)
				.setName(SHIPPING_OPTION_NAME)
				.setDisplayName(DISPLAY_NAME)
				.setCarrier(CARRIER)
				.setCosts(Collections.singleton(costEntity));

		ResourceState<ShippingOptionEntity> shippingOptionRepresentation = shippingOptionTransformer.transformToRepresentation(shippingOptionDto,
				SHIPPING_OPTION_URI);

		ShippingOptionEntity entity = shippingOptionRepresentation.getEntity();
		assertEquals("The representation should include the option id", SHIPPING_OPTION_ID, entity.getShippingOptionId());
		assertEquals("The representation should include the name", SHIPPING_OPTION_NAME, entity.getName());
		assertEquals("The representation should include the display name", DISPLAY_NAME, entity.getDisplayName());
		assertEquals("The representation should include the carrier", CARRIER, entity.getCarrier());
		assertThat("The representation should include the cost", entity.getCost(), Matchers.contains(costEntity));

		Self expectedSelf = SelfFactory.createSelf(SHIPPING_OPTION_URI);
		assertEquals("The representation self link should be valid", expectedSelf, shippingOptionRepresentation.getSelf());
	}
}

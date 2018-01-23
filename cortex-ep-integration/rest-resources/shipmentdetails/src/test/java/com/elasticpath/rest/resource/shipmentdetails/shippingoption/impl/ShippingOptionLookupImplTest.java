/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOption;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.ShippingOptionLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.transform.ShippingOptionTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Tests for {@link ShippingOptionLookupImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ShippingOptionTransformer.class)
public final class ShippingOptionLookupImplTest {

	private static final String RESOURCE_NAME = "shipmentdetails";
	private static final String SHIPMENT_DETAILS_ID = "SHIPMENT_DETAILS_ID";
	private static final String SCOPE = "scope";
	private static final String DECODED_SHIPPING_OPTION_ID = "OC3FC8BD-72D1-BABA-8950-B5480719038B";
	private static final String SHIPPING_OPTION_ID = Base32Util.encode(DECODED_SHIPPING_OPTION_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShippingOptionLookupStrategy mockStrategy;
	@Mock
	private ShippingOptionTransformer mockTransformer;

	@InjectMocks
	private ShippingOptionLookupImpl lookup;


	/**
	 * Test get selected delivery method.
	 */
	@Test
	public void testGetSelectedDeliveryMethod() {
		when(mockStrategy.getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(DECODED_SHIPPING_OPTION_ID));

		ExecutionResult<String> result = lookup.getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID);

		assertTrue(result.isSuccessful());
		assertEquals(SHIPPING_OPTION_ID, result.getData());
	}

	/**
	 * Test get selected delivery method with no delivery method found.
	 */
	@Test
	public void testGetSelectedDeliveryMethodWithNoDeliveryMethodFound() {
		when(mockStrategy.getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID))
				.thenReturn(ExecutionResultFactory.createNotFound("notFound"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getSelectedShipmentOptionIdForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID);
	}

	/**
	 * Test get shipping option.
	 */
	@Test
	public void testGetShippingOption() {

		ResourceState<ShippingOptionEntity> mockRepresentation = ResourceState.Builder.create(ShippingOptionEntity.builder().build())
				.build();
		ShippingOptionDto mockDto = Mockito.mock(ShippingOptionDto.class);

		when(mockStrategy.getShippingOptionForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID, DECODED_SHIPPING_OPTION_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockDto));

		String shippingOptionUri = URIUtil.format(RESOURCE_NAME, SCOPE, SHIPMENT_DETAILS_ID, ShippingOption.URI_PART, SHIPPING_OPTION_ID);
		when(mockTransformer.transformToRepresentation(mockDto, shippingOptionUri)).thenReturn(mockRepresentation);

		ExecutionResult<ResourceState<ShippingOptionEntity>> result =
				lookup.getShippingOption(SCOPE, SHIPMENT_DETAILS_ID, SHIPPING_OPTION_ID);
		assertTrue(result.isSuccessful());
	}

	/**
	 * Test get shipping option with failure getting option.
	 */
	@Test
	public void testGetShippingOptionWithFailureGettingOption() {
		when(mockStrategy.getShippingOptionForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID, DECODED_SHIPPING_OPTION_ID))
				.thenReturn(ExecutionResultFactory.<ShippingOptionDto>createNotFound("notFound"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getShippingOption(SCOPE, SHIPMENT_DETAILS_ID, SHIPPING_OPTION_ID);
	}


	/**
	 * Test getting shipping option ids.
	 */
	@Test
	public void testGettingShippingOptionIds() {
		when(mockStrategy.getShippingOptionIdsForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Arrays.asList(DECODED_SHIPPING_OPTION_ID)));

		ExecutionResult<Collection<String>> result = lookup.getShippingOptionIdsForShipmentDetail(SCOPE, SHIPMENT_DETAILS_ID);
		assertTrue(result.isSuccessful());
		assertTrue(CollectionUtil.areSame(Arrays.asList(SHIPPING_OPTION_ID), result.getData()));
	}

	/**
	 * Test getting shipping option ids with failure getting ids.
	 */
	@Test
	public void testGettingShippingOptionIdsWithFailureGettingIds() {
		when(mockStrategy.getShippingOptionIdsForShipmentDetails(SCOPE, SHIPMENT_DETAILS_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound("notFound"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getShippingOptionIdsForShipmentDetail(SCOPE, SHIPMENT_DETAILS_ID);
	}
}

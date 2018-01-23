/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.mockito.Mockito.when;

import com.elasticpath.rest.ResourceStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.resource.prices.integration.ShipmentLineItemPriceLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Unit test.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemPriceLookupImplTest {

	private static final String PURCHASE_ID = "purchase-id";
	private static final String SHIPMENT_ID = "shipment-id";
	private static final String LINEITEM_ID = "lineitem-id";
	private static final String TEST_SCOPE = "test-scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShipmentLineItemEntity shipmentLineItemEntity;

	@Mock
	private ShipmentLineItemPriceEntity shipmentLineItemPriceEntity;

	@Mock
	private ShipmentLineItemPriceLookupStrategy shipmentLineItemPriceLookupStrategy;

	@InjectMocks
	private ShipmentLineItemPriceLookupImpl shipmentLineItemPriceLookup;

	private ResourceState<ShipmentLineItemEntity> shipmentLineItemRep;

	@Before
	public void setUp() {
		when(shipmentLineItemEntity.getPurchaseId()).thenReturn(PURCHASE_ID);
		when(shipmentLineItemEntity.getShipmentId()).thenReturn(SHIPMENT_ID);
		when(shipmentLineItemEntity.getLineItemId()).thenReturn(LINEITEM_ID);
		shipmentLineItemRep
			= ResourceState.Builder.create(shipmentLineItemEntity)
				.withScope(TEST_SCOPE)
				.build();
	}

	@Test
	public void testGetPrice() throws Exception {
		when(shipmentLineItemPriceLookupStrategy.getPrice(TEST_SCOPE, PURCHASE_ID, SHIPMENT_ID, LINEITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(shipmentLineItemPriceEntity));
		ExecutionResult<ShipmentLineItemPriceEntity> price
				= shipmentLineItemPriceLookup.getPrice(shipmentLineItemRep);
		AssertExecutionResult.assertExecutionResult(price).isSuccessful();
	}

	@Test
	public void testGetPriceFailure() throws Exception {
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		when(shipmentLineItemPriceLookupStrategy.getPrice(TEST_SCOPE, PURCHASE_ID, SHIPMENT_ID, LINEITEM_ID))
				.thenReturn(ExecutionResultFactory.<ShipmentLineItemPriceEntity>createNotFound("Test"));
		shipmentLineItemPriceLookup.getPrice(shipmentLineItemRep);
	}
}

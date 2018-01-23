/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.ShipmentLineItemTotalLookupStrategy;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test class for {@link ShipmentLineItemTotalLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class ShipmentLineItemTotalLookupImplTest extends AbstractTotalLookupContractTest<ShipmentLineItemEntity> {

	private static final String SCOPE = "scope";
	private static final String SHIPMENT_ID = "TEST-SHIPMENT=";
	private static final String PURCHASE_ID = "TEST-PURCHASE=";
	private static final String LINE_ITEM_ID = "TEST-LINE-ITEM-1=";

	@Mock
	private ShipmentLineItemTotalLookupStrategy mockLookupStrategy;

	@InjectMocks
	private ShipmentLineItemTotalLookupImpl shipmentLineItemTotalLookup;


	@Override
	TotalLookup<ShipmentLineItemEntity> createTotalLookupUnderTest() {
		return shipmentLineItemTotalLookup;
	}

	@Override
	ResourceState<ShipmentLineItemEntity> createRepresentation() {
		ShipmentLineItemEntity entity = ShipmentLineItemEntity.builder()
				.withLineItemId(LINE_ITEM_ID)
				.withPurchaseId(PURCHASE_ID)
				.withShipmentId(SHIPMENT_ID)
				.build();

		return ResourceState.Builder.create(entity)
				.withSelf(resourceSelf)
				.withScope(SCOPE)
				.build();
	}

	@Override
	void arrangeLookupToReturnTotals(final TotalEntity totalDto) {
		when(mockLookupStrategy.getTotal(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(ExecutionResultFactory.createReadOK(totalDto));
	}

	@Override
	void arrangeLookupToReturnNotFound() {
		when(mockLookupStrategy.getTotal(anyString(), anyString(), anyString(), anyString()))
				.thenReturn(ExecutionResultFactory.<TotalEntity>createNotFound());
	}

	@Override
	String getRel() {
		return TotalResourceRels.LINE_ITEM_REL;
	}
}

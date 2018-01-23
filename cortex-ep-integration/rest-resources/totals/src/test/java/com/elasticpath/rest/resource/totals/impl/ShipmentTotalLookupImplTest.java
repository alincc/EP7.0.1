/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.impl;

import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.integration.ShipmentTotalLookupStrategy;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Test class for {@link ShipmentTotalLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class ShipmentTotalLookupImplTest extends AbstractTotalLookupContractTest<ShipmentEntity> {

	private static final String SCOPE = "scope";

	private static final String SHIPMENT_ID = "xyz=";
	private static final String PURCHASE_ID = "xyssz=";

	@Mock
	private ShipmentTotalLookupStrategy mockStrategy;

	@InjectMocks
	private ShipmentTotalLookupImpl totalLookup;

	@Override
	TotalLookup<ShipmentEntity> createTotalLookupUnderTest() {
		return totalLookup;
	}

	@Override
	ResourceState<ShipmentEntity> createRepresentation() {
		ShipmentEntity entity = ShipmentEntity.builder()
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
		when(mockStrategy.getTotal(PURCHASE_ID, SHIPMENT_ID)).thenReturn(ExecutionResultFactory.createReadOK(totalDto));
	}

	@Override
	void arrangeLookupToReturnNotFound() {
		when(mockStrategy.getTotal(PURCHASE_ID, SHIPMENT_ID)).thenReturn(ExecutionResultFactory.<TotalEntity>
				createNotFound());
	}

	@Override
	String getRel() {
		return TotalResourceRels.SHIPMENT_REL;
	}
}

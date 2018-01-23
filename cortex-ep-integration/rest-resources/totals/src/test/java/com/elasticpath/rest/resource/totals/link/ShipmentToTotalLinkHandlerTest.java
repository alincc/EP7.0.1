/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.link;

import static org.mockito.Mockito.when;

import java.util.Collections;

import org.mockito.Mock;

import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.totals.TotalLookup;
import com.elasticpath.rest.resource.totals.rel.TotalResourceRels;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Contract tests for {@link ShipmentToTotalLinkHandler}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class ShipmentToTotalLinkHandlerTest extends AbstractLinkToTotalsContractTest<ShipmentEntity> {

	@Mock
	private TotalLookup<ShipmentEntity> shipmentTotalsLookup;

	@Override
	ResourceState<ShipmentEntity> createRepresentationUnderTest() {
		ShipmentEntity entity = ShipmentEntity.builder().build();
		return ResourceState.Builder.create(entity)
				.withSelf(self)
				.build();
	}

	@Override
	ResourceStateLinkHandler<ShipmentEntity> createLinkCommandStrategyUnderTest() {
		return new ShipmentToTotalLinkHandler(totalResourceLinkCreator, shipmentTotalsLookup);
	}

	@Override
	void arrangeTotalLookupToReturnTotals() {
		when(shipmentTotalsLookup.getTotal(testRepresentation)).thenReturn(executionResult);
	}

	@Override
	void arrangeTotalResourceLinkHelperToReturnResourceLink() {
		when(totalResourceLinkCreator.createLinkToOtherResource(RESOURCE_URI, executionResult, TotalResourceRels.SHIPMENT_REV)).thenReturn(
				Collections.singleton(resourceLink));
	}
}

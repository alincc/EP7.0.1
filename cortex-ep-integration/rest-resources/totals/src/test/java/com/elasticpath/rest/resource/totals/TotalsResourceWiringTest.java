/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.totals.integration.ShipmentLineItemTotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.ShipmentTotalLookupStrategy;
import com.elasticpath.rest.resource.totals.integration.TotalLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;

/**
 * Tests totals resource wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class TotalsResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "totalLookupStrategy")
	private TotalLookupStrategy totalLookupStrategy;

	@ReplaceWithMock(beanName = "shipmentTotalLookupStrategy")
	private ShipmentTotalLookupStrategy shipmentTotalLookupStrategy;

	@ReplaceWithMock(beanName = "shipmentLineItemTotalLookupStrategy")
	private ShipmentLineItemTotalLookupStrategy shipmentLineItemTotalLookupStrategy;
}

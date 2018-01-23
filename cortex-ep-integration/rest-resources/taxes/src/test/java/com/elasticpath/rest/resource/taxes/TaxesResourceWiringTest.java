/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.taxes.integration.ShippingCostTaxesLookupStrategy;
import com.elasticpath.rest.resource.taxes.order.integration.OrderTaxesLookupStrategy;
import com.elasticpath.rest.resource.taxes.shipment.integration.ShipmentTaxesLookupStrategy;
import com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.ShipmentLineItemTaxesLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;

/**
 * Tests taxes resource wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class TaxesResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "orderTaxesLookupStrategy")
	private OrderTaxesLookupStrategy orderTaxesLookupStrategy;

	@ReplaceWithMock(beanName = "shipmentTaxesLookupStrategy")
	private ShipmentTaxesLookupStrategy shipmentTaxesLookupStrategy;

	@ReplaceWithMock(beanName = "shippingCostTaxesLookupStrategy")
	private ShippingCostTaxesLookupStrategy shippingCostTaxesLookupStrategy;
	
	@ReplaceWithMock(beanName = "shipmentLineItemTaxesLookupStrategy")
	private ShipmentLineItemTaxesLookupStrategy shipmentLineItemTaxesLookupStrategy;
}

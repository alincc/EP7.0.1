/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.ResourceServerKernel;
import com.elasticpath.rest.resource.shipments.addresses.integration.ShippingAddressLookupStrategy;
import com.elasticpath.rest.resource.shipments.integration.ShipmentLookupStrategy;
import com.elasticpath.rest.resource.shipments.lineitems.integration.ShipmentLineItemsLookupStrategy;
import com.elasticpath.rest.resource.shipments.lineitems.option.integration.ShipmentLineItemOptionsLookupStrategy;
import com.elasticpath.rest.resource.shipments.shippingoption.integration.ShippingOptionLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;

/**
 * Tests shipments bean wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class ShipmentsResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "resourceOperationContext")
	private ResourceOperationContext resourceOperationContext;

	@ReplaceWithMock(beanName = "resourceServerKernel")
	private ResourceServerKernel resourceServerKernel;

	@ReplaceWithMock(beanName = "shipmentLookupStrategy")
	private ShipmentLookupStrategy shipmentLookupStrategy;

	@ReplaceWithMock(beanName = "shipmentLineItemOptionsLookupStrategy")
	private ShipmentLineItemOptionsLookupStrategy shipmentLineItemOptionsLookupStrategy;

	@ReplaceWithMock(beanName = "shipmentLineItemsLookupStrategy")
	private ShipmentLineItemsLookupStrategy shipmentLineItemsLookupStrategy;

	@ReplaceWithMock(beanName = "shippingOptionLookupStrategy")
	private ShippingOptionLookupStrategy shippingOptionLookupStrategy;

	@ReplaceWithMock(beanName = "purchaseUriBuilderFactory")
	private PurchaseUriBuilderFactory purchaseUriBuilderFactory;

	@ReplaceWithMock(beanName = "shippingAddressLookupStrategy")
	private ShippingAddressLookupStrategy shippingAddressLookupStrategy;
}

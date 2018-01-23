/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.ResourceServerKernel;
import com.elasticpath.rest.resource.mediatype.MediaTypeRepository;
import com.elasticpath.rest.resource.prices.integration.CartLineItemPriceLookupStrategy;
import com.elasticpath.rest.resource.prices.integration.ItemPriceLookupStrategy;
import com.elasticpath.rest.resource.prices.integration.ShipmentLineItemPriceLookupStrategy;
import com.elasticpath.rest.resource.wiring.AbstractResourceWiringTest;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.CartsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ItemDefinitionsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PurchaseUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentLineItemUriBuilderFactory;
import com.elasticpath.rest.schema.uri.ShipmentsUriBuilderFactory;

/**
 * Tests shipments bean wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class PricesResourceWiringTest extends AbstractResourceWiringTest {

	@ReplaceWithMock(beanName = "resourceOperationContext")
	private ResourceOperationContext resourceOperationContext;

	@ReplaceWithMock(beanName = "resourceServerKernel")
	private ResourceServerKernel resourceServerKernel;

	@ReplaceWithMock(beanName = "itemPriceLookupStrategy")
	private ItemPriceLookupStrategy itemPriceLookupStrategy;

	@ReplaceWithMock(beanName = "cartLineItemPriceLookupStrategy")
	private CartLineItemPriceLookupStrategy cartLineItemPriceLookupStrategy;

	@ReplaceWithMock(beanName = "shipmentLineItemPriceLookupStrategy")
	private ShipmentLineItemPriceLookupStrategy shipmentLineItemPriceLookupStrategy;

	@ReplaceWithMock(beanName = "mediaTypeRepository")
	private MediaTypeRepository mediaTypeRepository;

	@ReplaceWithMock(beanName = "itemsUriBuilderFactory")
	private ItemsUriBuilderFactory itemsUriBuilderFactory;

	@ReplaceWithMock(beanName = "itemDefinitionsUriBuilderFactory")
	private ItemDefinitionsUriBuilderFactory itemDefinitionsUriBuilderFactory;

	@ReplaceWithMock(beanName = "cartsUriBuilderFactory")
	private CartsUriBuilderFactory cartsUriBuilderFactory;

	@ReplaceWithMock(beanName = "cartLineItemsUriBuilderFactory")
	private CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;

	@ReplaceWithMock(beanName = "purchaseUriBuilderFactory")
	private PurchaseUriBuilderFactory purchaseUriBuilderFactory;

	@ReplaceWithMock(beanName = "shipmentLineItemUriBuilderFactory")
	private ShipmentLineItemUriBuilderFactory shipmentLineItemUriBuilderFactory;

	@ReplaceWithMock(beanName = "shipmentsUriBuilderFactory")
	private ShipmentsUriBuilderFactory shipmentsUriBuilderFactory;
}

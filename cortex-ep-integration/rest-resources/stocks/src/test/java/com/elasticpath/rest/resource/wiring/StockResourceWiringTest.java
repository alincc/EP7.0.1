/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wiring;

import org.kubek2k.springockito.annotations.ReplaceWithMock;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.rest.resource.ResourceServerKernel;
import com.elasticpath.rest.resource.stock.integration.StockLookupStrategy;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * Tests stock resource wiring.
 */
@ContextConfiguration
@SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.TestClassWithoutTestCases" })
public class StockResourceWiringTest extends AbstractResourceWiringTest {
	
	@ReplaceWithMock(beanName = "resourceKernel")
	private ResourceServerKernel resourceKernel;
	
	@ReplaceWithMock(beanName = "stockLookupStrategy")
	private StockLookupStrategy stockLookupStrategy;
	
	@ReplaceWithMock(beanName = "itemsUriBuilderFactory")
	private ItemsUriBuilderFactory itemsUriBuilderFactory;
	
}

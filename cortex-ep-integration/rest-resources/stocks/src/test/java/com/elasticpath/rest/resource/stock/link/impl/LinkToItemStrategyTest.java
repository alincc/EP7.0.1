/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.link.impl;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.stocks.StocksMediaTypes;
import com.elasticpath.rest.resource.stock.integration.StockLookupStrategy;
import com.elasticpath.rest.resource.stock.rel.StockResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.StockUriBuilder;

/**
 * Tests for {@link com.elasticpath.rest.resource.stock.link.impl.LinkToItemStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LinkToItemStrategyTest {

	private static final String SCOPE = "testScope";
	private static final String ITEM_ID = "testItemId";
	private static final String STOCK_URI = "testStockUri";
	private static final ResourceState<ItemEntity> ITEM = ResourceState.Builder
			.create(ItemEntity.builder()
					.withItemId(ITEM_ID)
					.build())
			.withScope(SCOPE)
			.build();
	@Mock
	private StockLookupStrategy stockLookupStrategy;
	@Mock
	private StockUriBuilder stockUriBuilder;
	@InjectMocks
	private LinkToItemStrategy linkToItemStrategy;


	/**
	 * Test that a stock link is correctly created for an item that is tangible (i.e. has finite stock).
	 */
	@Test
	public void testCreateLinksSuccess() {
		Collection<ResourceLink> links = runCreateLinksSuccessfully();

		Mockito.verify(stockUriBuilder).setItemId(Mockito.eq(ITEM_ID));
		Mockito.verify(stockUriBuilder).setScope(Mockito.eq(SCOPE));
		Assert.assertEquals("Exactly one link should be created.", 1, links.size());
		ResourceLink link = links.iterator().next();
		Assert.assertEquals(StockResourceRels.STOCK_REL, link.getRel());
		Assert.assertEquals(StockResourceRels.ITEM_REV, link.getRev());
		Assert.assertEquals(StocksMediaTypes.STOCK.id(), link.getType());
		Assert.assertEquals(STOCK_URI, link.getUri());
	}

	private Collection<ResourceLink> runCreateLinksSuccessfully() {
		Mockito.when(stockUriBuilder.setItemId(Mockito.anyString())).thenReturn(stockUriBuilder);
		Mockito.when(stockUriBuilder.setScope(Mockito.anyString())).thenReturn(stockUriBuilder);
		Mockito.when(stockUriBuilder.build()).thenReturn(STOCK_URI);
		ExecutionResult<Boolean> isStockDisplayedResult = ExecutionResultFactory.createReadOK(Boolean.TRUE);
		mockIsStockDisplayed(isStockDisplayedResult);
		return linkToItemStrategy.getLinks(ITEM);
	}

	private void mockIsStockDisplayed(final ExecutionResult<Boolean> isStockDisplayedResult) {
		Mockito.when(stockLookupStrategy.isStockDisplayedForItem(SCOPE, ITEM_ID)).thenReturn(isStockDisplayedResult);
	}

	/**
	 * Test that a stock link is not created for an item that is intangible (i.e. stock quantity is not relevant).
	 */
	@Test
	public void testCreateLinksForItemWithoutStockDisplayed() {
		ExecutionResult<Boolean> isStockDisplayedResult = ExecutionResultFactory.createReadOK(Boolean.FALSE);
		mockIsStockDisplayed(isStockDisplayedResult);
		Collection<ResourceLink> links = linkToItemStrategy.getLinks(ITEM);
		Assert.assertEquals(0, links.size());
	}

	/**
	 * Test that a stock link is not created for an item that is missing.
	 */
	@Test
	public void testCreateLinksWithMissingItem() {
		ExecutionResult<Boolean> isStockDisplayedResult = ExecutionResultFactory.createNotFound();
		mockIsStockDisplayed(isStockDisplayedResult);
		Collection<ResourceLink> links = linkToItemStrategy.getLinks(ITEM);
		Assert.assertEquals(0, links.size());
	}

}

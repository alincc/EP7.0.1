/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.impl;

import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.definition.stocks.StockEntity;
import com.elasticpath.rest.resource.stock.integration.StockLookupStrategy;
import com.elasticpath.rest.resource.stock.rel.StockResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Tests for {@link StockResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StockResourceOperatorImplTest {

	private static final String RESOURCE_SERVER_NAME = "stocks";
	private static final String SCOPE = "testscope";
	private static final String ITEM_ID = "testitemid";
	private static final String ITEM_URI = "/uri";
	private static final ResourceState<ItemEntity> ITEM = ResourceState.Builder
			.create(ItemEntity.builder()
					.withItemId(ITEM_ID)
					.build())
			.withScope(SCOPE)
			.withSelf(SelfFactory.createSelf(ITEM_URI))
			.build();
	private static final String STOCK_URI = new StockUriBuilderImpl(RESOURCE_SERVER_NAME).setScope(SCOPE).setItemId(ITEM_ID).build();
	private static final ResourceOperation READ_OP = TestResourceOperationFactory.createRead(STOCK_URI);

	@Mock
	private StockLookupStrategy stockLookup;
	@InjectMocks
	private StockResourceOperatorImpl stockResourceOperatorImpl;


	@Test
	public void testProcessReadSuccess() {
		StockEntity expectedStockEntity = StockEntity.builder().build();
		Mockito.when(stockLookup.getStockByItemId(SCOPE, ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(expectedStockEntity));
		ResourceLink expectedItemLink = ResourceLinkFactory.create(ITEM_URI, ItemsMediaTypes.ITEM.id(),
				StockResourceRels.ITEM_REL, StockResourceRels.STOCK_REV);

		OperationResult readResult = stockResourceOperatorImpl.processRead(ITEM, READ_OP);

		assertOperationResult(readResult).resourceStatus(ResourceStatus.READ_OK);
		assertResourceState(readResult.getResourceState())
				.self(SelfFactory.createSelf(STOCK_URI))
				.containsLink(expectedItemLink);
	}


	@Test(expected = BrokenChainException.class)
	public void testProcessReadWithLookupNotFound() {
		Mockito.when(stockLookup.getStockByItemId(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<StockEntity>createNotFound());

		stockResourceOperatorImpl.processRead(ITEM, READ_OP);
	}


	@Test(expected = BrokenChainException.class)
	public void testProcessReadWithLookupServerError() {
		Mockito.when(stockLookup.getStockByItemId(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<StockEntity>createServerError("test server error"));

		stockResourceOperatorImpl.processRead(ITEM, READ_OP);
	}
}

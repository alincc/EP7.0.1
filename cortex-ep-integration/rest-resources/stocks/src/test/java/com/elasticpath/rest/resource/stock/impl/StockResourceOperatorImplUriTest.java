/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.stock.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * URI test for {@link StockResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StockResourceOperatorImplUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "stocks";
	private static final String SCOPE = "testscope";
	private static final String ENCODED_ITEM_ID = Base32Util.encode("testitemid");

	@Mock
	private StockResourceOperatorImpl stockResourceOperatorImpl;

	private String stockUri;

	/**
	 * Prepare common test conditions.
	 */
	@Before
	public void setUp() {
		stockUri = new StockUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setScope(SCOPE)
				.setItemId(ENCODED_ITEM_ID)
				.build();
	}

	/**
	 * Test {@link StockResourceOperatorImpl#processRead(ResourceState, ResourceOperation)}.
	 */
	@Test
	public void testPathAnnotationForProcessRead() {
		ResourceOperation operation = TestResourceOperationFactory.createRead(stockUri);
		when(stockResourceOperatorImpl.processRead(anyItemEntity(), anyResourceOperation())).thenReturn(operationResult);

		mediaType(ItemsMediaTypes.ITEM);
		readOther(operation);

		dispatchMethod(operation, stockResourceOperatorImpl);

		verify(stockResourceOperatorImpl).processRead(anyItemEntity(), anyResourceOperation());
	}


	private ResourceState<ItemEntity> anyItemEntity() {
		return any();
	}

}

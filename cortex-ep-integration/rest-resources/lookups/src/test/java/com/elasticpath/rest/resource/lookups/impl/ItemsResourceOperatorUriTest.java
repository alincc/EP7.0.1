/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups.impl;

import static com.elasticpath.rest.TestResourceOperationFactory.createCreate;
import static com.elasticpath.rest.TestResourceOperationFactory.createRead;
import static com.elasticpath.rest.definition.items.ItemsMediaTypes.ITEM;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests uri patterns in {@link ItemLookupResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ItemLookupResourceOperatorImpl.class})
public class ItemsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER = "/lookups";
	private static final String SCOPE = "scope";

	@Mock
	private ItemLookupResourceOperatorImpl resourceOperator;

	@Test
	public void testRootLookupsRead() {
		String formUri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setScope(SCOPE)
				.build();
		ResourceOperation operation = createRead(formUri);
		when(resourceOperator.processReadLookups(anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadLookups(anyString(), anyResourceOperation());
	}

	@Test
	public void testFormUriRead() {
		String formUri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setScope(SCOPE)
				.setItemsPart()
				.setFormPart()
				.build();
		ResourceOperation operation = createRead(formUri);
		when(resourceOperator.processItemLookupFormRead(anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processItemLookupFormRead(anyString(), anyResourceOperation());
	}

	@Test
	public void testSearchUriCreate() {
		String searchUri = new ItemLookupUriBuilderImpl(RESOURCE_SERVER)
				.setScope(SCOPE)
				.setItemsPart()
				.build();
		ResourceState mockResourceState = mock(ResourceState.class);
		ResourceOperation operation = createCreate(searchUri, mockResourceState);
		when(resourceOperator.processItemCodeSearch(anyString(), anyResourceOperation()))
				.thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processItemCodeSearch(anyString(), anyResourceOperation());
	}

	@Test
	public void testRfoUriRead() {
		String skuLookupRfoUri = RESOURCE_SERVER + "/rfo/uri";
		ResourceOperation operation = createRead(skuLookupRfoUri);
		mediaType(ITEM);
		readOther(operation);
		when(resourceOperator.processReadCodeForItem(anyItemEntity(), anyResourceOperation())).thenReturn(operationResult);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator.processReadCodeForItem(anyItemEntity(), anyResourceOperation()));
	}

	private static ResourceState<ItemEntity> anyItemEntity() {
		return Mockito.any();
	}
}

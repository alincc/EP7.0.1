/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;


/**
 * Test for {@link com.elasticpath.rest.resource.purchases.lineitems.impl.PurchaseLineItemComponentResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PurchaseLineItemComponentResourceOperatorImplTest {

	private static final String SCOPE = "mobee";
	private static final String COMPONENT_URI = "/mock/component/uri";
	private static final String PARENT_URI = "/purchases/lineitems/1234";
	private static final String PURCHASE_ID = "purchase id";
	private static final String LINE_ITEM_ID = "line item id";
	private static final String COMPONENT_ID_1 = "component id 1";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(PARENT_URI);
	@Mock
	private PurchaseLineItemLookup mockLookup;
	@InjectMocks
	private PurchaseLineItemComponentResourceOperatorImpl classUnderTest;


	/**
	 * Tests that the classUnderTest returns a representation if the lookup is successful.
	 */
	@Test
	public void testExecuteWithLookupSuccess() {
		Self componentSelf = SelfFactory.createSelf(COMPONENT_URI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());
		ResourceState<PurchaseLineItemEntity> componentRepresentation = ResourceState.Builder
				.create(PurchaseLineItemEntity.builder().build())
				.withSelf(componentSelf)
				.build();
		when(mockLookup.getPurchaseLineItem(SCOPE, PURCHASE_ID, COMPONENT_ID_1, PARENT_URI, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(componentRepresentation));
		ResourceState<PurchaseLineItemEntity> purchaseLineItem = createPurchaseLineItem();

		OperationResult representationResult =
				classUnderTest.processReadLineItemComponent(purchaseLineItem, COMPONENT_ID_1, READ);

		assertTrue(representationResult.isSuccessful());
		assertEquals(componentRepresentation, representationResult.getResourceState());
	}

	/**
	 * Tests that the classUnderTest fails if the lookup fails.
	 */
	@Test
	public void testExecuteWithLookupFailure() {

		when(mockLookup.getPurchaseLineItem(SCOPE, PURCHASE_ID, COMPONENT_ID_1, PARENT_URI, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.<ResourceState<PurchaseLineItemEntity>>createNotFound("Component not found"));

		ResourceState<PurchaseLineItemEntity> purchaseLineItem = createPurchaseLineItem();
		OperationResult representationResult =
				classUnderTest.processReadLineItemComponent(purchaseLineItem, COMPONENT_ID_1, READ);

		assertTrue(representationResult.isFailure());
		assertEquals(ResourceStatus.NOT_FOUND, representationResult.getResourceStatus());
	}

	private ResourceState<PurchaseLineItemEntity> createPurchaseLineItem() {
		Self self = SelfFactory.createSelf(PARENT_URI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());

		return ResourceState.Builder
				.create(PurchaseLineItemEntity.builder()
						.withPurchaseId(PURCHASE_ID)
						.withLineItemId(LINE_ITEM_ID)
						.build())
				.withScope(SCOPE)
				.withSelf(self)
				.build();
	}

}

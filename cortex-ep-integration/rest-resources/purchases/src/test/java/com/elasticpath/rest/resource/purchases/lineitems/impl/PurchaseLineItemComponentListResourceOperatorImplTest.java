/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.purchases.constants.PurchaseResourceConstants;
import com.elasticpath.rest.resource.purchases.lineitems.PurchaseLineItemLookup;
import com.elasticpath.rest.resource.purchases.lineitems.rel.PurchaseLineItemsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests reading purchase line item classUnderTest.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PurchaseLineItemComponentListResourceOperatorImplTest {

	private static final String SCOPE = "mobee";
	private static final String PARENT_URI = "/purchases/lineitems/1234";
	private static final String COMPONENT_ID_1 = "component id 1";
	private static final String PURCHASE_ID = "purchase id";
	private static final String LINE_ITEM_ID = "line item id";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(PARENT_URI);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PurchaseLineItemLookup mockPurchaseLineItemLookup;
	@InjectMocks
	private PurchaseLineItemComponentListResourceOperatorImpl classUnderTest;

	/**
	 * Test read purchase components list.
	 */
	@Test
	public void testReadPurchaseComponentsList() {
		ResourceState<PurchaseLineItemEntity> purchaseLineItem = createPurchaseLineItem();

		when(mockPurchaseLineItemLookup.getComponentIdsForLineItemId(SCOPE, PURCHASE_ID, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(COMPONENT_ID_1)));

		OperationResult resultRepresentation = classUnderTest.processReadLineItemComponentsList(purchaseLineItem, READ);

		assertTrue("Operation should be successful.", resultRepresentation.isSuccessful());
		Collection<ResourceLink> links = resultRepresentation.getResourceState().getLinks();
		assertEquals(2, links.size());
		String expectedUri = URIUtil.format(PARENT_URI, Components.URI_PART, COMPONENT_ID_1);

		ResourceLink expectedParentLink = ResourceLinkFactory.create(PARENT_URI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id(),
				PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_REL, PurchaseLineItemsResourceRels.PURCHASE_LINEITEM_COMPONENTS_REL);

		ResourceLink expectedComponentLink = ElementListFactory.createElementOfList(expectedUri, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id());

		Collection<ResourceLink> expectedLinks = new ArrayList<>(2);
		expectedLinks.add(expectedComponentLink);
		expectedLinks.add(expectedParentLink);
		assertTrue("Not all expected links match.", links.containsAll(expectedLinks));
		assertEquals("Max age does not match expected value.",
				PurchaseResourceConstants.MAX_AGE, resultRepresentation.getResourceState().getResourceInfo().getMaxAge().intValue());
	}

	/**
	 * Test read purchase components list when line item id not found.
	 */
	@Test
	public void testReadPurchaseComponentsListWhenLineItemIdNotFound() {
		ResourceState<PurchaseLineItemEntity> purchaseLineItem = createPurchaseLineItem();

		when(mockPurchaseLineItemLookup.getComponentIdsForLineItemId(SCOPE, PURCHASE_ID, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		OperationResult result = classUnderTest.processReadLineItemComponentsList(purchaseLineItem, READ);

		assertTrue("The result returned should be successful", result.isSuccessful());
		assertEquals("The requested item should be not found", ResourceStatus.NOT_FOUND, result.getResourceStatus());
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

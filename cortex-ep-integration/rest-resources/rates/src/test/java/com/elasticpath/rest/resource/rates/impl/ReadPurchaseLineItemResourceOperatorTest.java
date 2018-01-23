/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.rates.impl;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.rates.integration.PurchaseLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;


/**
 * Test {@link com.elasticpath.rest.resource.rates.impl.ReadPurchaseLineItemResourceOperator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadPurchaseLineItemResourceOperatorTest {

	private static final String SCOPE = "scope";
	private static final String LINE_ITEM_ID = "lineitem_id";
	private static final String ITEM_URI = "/item";
	private static final String RATE_URI = "/rate";
	private static final String PURCHASE_ID = "purchaseId";

	@Mock
	private PurchaseLineItemRateLookupStrategy strategy;
	@InjectMocks
	private ReadPurchaseLineItemResourceOperator classUnderTest;


	@Test
	public void testLineItem() {
		ResourceLink expectedLink = ResourceLinkFactory.create(ITEM_URI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id(),
				RateRepresentationRels.LINE_ITEM_REL, RateRepresentationRels.RATE_REV);
		ResourceOperation operation = TestResourceOperationFactory.createRead(RATE_URI);
		ResourceState<PurchaseLineItemEntity> cartLineItem = ResourceState.Builder
				.create(PurchaseLineItemEntity.builder()
						.withLineItemId(Base32Util.encode(LINE_ITEM_ID))
						.withPurchaseId(Base32Util.encode(PURCHASE_ID))
						.build())
				.withScope(SCOPE)
				.withSelf(SelfFactory.createSelf(ITEM_URI, PurchasesMediaTypes.PURCHASE_LINE_ITEM.id()))
				.build();
		when(strategy.getLineItemRate(SCOPE, PURCHASE_ID, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(RateEntity.builder().build()));

		OperationResult result = classUnderTest.processRead(cartLineItem, operation);

		assertResourceState(result.getResourceState())
				.self(SelfFactory.createSelf(RATE_URI))
				.containsLink(expectedLink);
	}
}

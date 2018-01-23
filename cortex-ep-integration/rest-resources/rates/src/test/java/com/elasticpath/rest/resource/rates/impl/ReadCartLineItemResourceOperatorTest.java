/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
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
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.rates.RateEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.rates.integration.CartLineItemRateLookupStrategy;
import com.elasticpath.rest.resource.rates.rel.RateRepresentationRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;


/**
 * Test {@link com.elasticpath.rest.resource.rates.impl.ReadCartLineItemResourceOperator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadCartLineItemResourceOperatorTest {

	private static final String SCOPE = "scope";
	private static final String CART_ID = "cart_id";
	private static final String LINE_ITEM_ID = "lineitem_id";
	private static final String ITEM_URI = "/item";
	private static final String RATE_URI = "/rate";

	@Mock
	private CartLineItemRateLookupStrategy cartLineItemRateLookupStrategy;
	@InjectMocks
	private ReadCartLineItemResourceOperator readCartLineItemResourceOperator;


	@Test
	public void testLineItem() {
		ResourceLink expectedLink = ResourceLinkFactory.create(ITEM_URI, CartsMediaTypes.LINE_ITEM.id(), RateRepresentationRels.LINE_ITEM_REL,
				RateRepresentationRels.RATE_REV);
		ResourceOperation operation = TestResourceOperationFactory.createRead(RATE_URI);
		ResourceState<LineItemEntity> cartLineItem = ResourceState.Builder
				.create(LineItemEntity.builder()
						.withLineItemId(Base32Util.encode(LINE_ITEM_ID))
						.withCartId(Base32Util.encode(CART_ID))
						.build())
				.withScope(SCOPE)
				.withSelf(SelfFactory.createSelf(ITEM_URI, CartsMediaTypes.LINE_ITEM.id()))
				.build();
		when(cartLineItemRateLookupStrategy.getLineItemRate(SCOPE, CART_ID, LINE_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(RateEntity.builder().build()));

		OperationResult result = readCartLineItemResourceOperator.processRead(cartLineItem, operation);

		assertResourceState(result.getResourceState())
				.self(SelfFactory.createSelf(RATE_URI))
				.containsLink(expectedLink);
	}
}

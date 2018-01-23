/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.BDDMockito.given;

import com.elasticpath.rest.ResourceStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.prices.integration.CartLineItemPriceLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests the {@link com.elasticpath.rest.resource.prices.impl.CartLineItemPriceLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartLineItemPriceLookupImplTest {
	private static final String DECODED_CART_ID = "decodedCartId";
	private static final String ENCODED_CART_ID = Base32Util.encode(DECODED_CART_ID);
	private static final String DECODED_LINE_ITEM_ID = "decodedLineItemId";
	private static final String ENCODED_LINE_ITEM_ID = Base32Util.encode(DECODED_LINE_ITEM_ID);
	private static final String SCOPE = "scope";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private CartLineItemPriceLookupStrategy cartLineItemPriceLookupStrategy;
	@InjectMocks
	private CartLineItemPriceLookupImpl cartLineItemPriceLookup;
	@Mock
	private CartLineItemPriceEntity cartLineitemPriceEntity;

	private ResourceState<LineItemEntity> lineItem;

	@Before
	public void setUpCommonTestComponents() {
		lineItem = ResourceState.Builder.create(LineItemEntity.builder()
																.withCartId(ENCODED_CART_ID)
																.withLineItemId(ENCODED_LINE_ITEM_ID)
																.build())
										.withScope(SCOPE)
										.build();
	}

	@Test
	public void ensureLineItemPriceCanBeLookedUp() {
		given(cartLineItemPriceLookupStrategy.getLineItemPrice(SCOPE, DECODED_CART_ID, DECODED_LINE_ITEM_ID))
				.willReturn(ExecutionResultFactory.createReadOK(cartLineitemPriceEntity));

		assertExecutionResult(cartLineItemPriceLookup.getLineItemPrice(lineItem)).data(cartLineitemPriceEntity);
	}

	@Test
	public void ensureNotFoundReturnedWhenPriceForLineItemNotFound() {
		given(cartLineItemPriceLookupStrategy.getLineItemPrice(SCOPE, DECODED_CART_ID, DECODED_LINE_ITEM_ID))
				.willReturn(ExecutionResultFactory.<CartLineItemPriceEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		cartLineItemPriceLookup.getLineItemPrice(lineItem);
	}

	@Test
	public void ensureServerErrorReturnedWhenQueryForLineItemPriceFails() {
		given(cartLineItemPriceLookupStrategy.getLineItemPrice(SCOPE, DECODED_CART_ID, DECODED_LINE_ITEM_ID))
				.willReturn(ExecutionResultFactory.<CartLineItemPriceEntity>createServerError(""));

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		cartLineItemPriceLookup.getLineItemPrice(lineItem);
	}
}

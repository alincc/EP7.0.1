/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.BDDMockito.given;

import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * The test for {@link LineItemLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class LineItemLookupImplTest {
	public static final String DECODED_CART_ID = "cartId";
	public static final String ENCODED_CART_ID = Base32Util.encode(DECODED_CART_ID);
	public static final String SCOPE = "scope";
	public static final String DECODED_LINE_ITEM_ID = "lineItemId";
	public static final String ENCODED_LINE_ITEM_ID = Base32Util.encode(DECODED_LINE_ITEM_ID);
	public static final String ITEM_ID = "itemId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private LineItemLookupStrategy lineItemLookupStrategy;
	@Mock
	private TransformToResourceState<LineItemEntity, LineItemEntity> lineItemDetailsTransformer;
	@InjectMocks
	private LineItemLookupImpl lineItemLookup;
	@Mock
	private LineItemEntity lineItemEntity;
	@Mock
	private ResourceState<LineItemEntity> lineItemRepresentation;

	@Test
	public void ensureValidLineItemCanBeFoundSuccessfully() {
		ResourceState<CartEntity> cartRepresentation = createCartRepresentation();
		given(lineItemLookupStrategy.getLineItem(SCOPE, DECODED_CART_ID, DECODED_LINE_ITEM_ID))
				.willReturn(ExecutionResultFactory.createReadOK(lineItemEntity));
		given(lineItemDetailsTransformer.transform(SCOPE, lineItemEntity)).willReturn(lineItemRepresentation);

		ExecutionResult<ResourceState<LineItemEntity>> result = lineItemLookup.find(cartRepresentation, ENCODED_LINE_ITEM_ID);

		assertExecutionResult(result).data(lineItemRepresentation);
	}

	@Test
	public void ensureNotFoundReturnedWheLineItemNotFound() {
		ResourceState<CartEntity> cartRepresentation = createCartRepresentation();
		given(lineItemLookupStrategy.getLineItem(SCOPE, DECODED_CART_ID, DECODED_LINE_ITEM_ID))
				.willReturn(ExecutionResultFactory.<LineItemEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemLookup.find(cartRepresentation, ENCODED_LINE_ITEM_ID);
	}

	@Test
	public void ensureServerErrorReturnedWhenLineItemLookupFails() {
		ResourceState<CartEntity> cartRepresentation = createCartRepresentation();
		given(lineItemLookupStrategy.getLineItem(SCOPE, DECODED_CART_ID, DECODED_LINE_ITEM_ID))
				.willReturn(ExecutionResultFactory.<LineItemEntity>createServerError(""));

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		lineItemLookup.find(cartRepresentation, ENCODED_LINE_ITEM_ID);
	}

	@Test
	public void ensureLineItemsCanBeRetrievedSuccessfullyForCart() {
		Collection<String> lineItemIds = Collections.singleton(DECODED_LINE_ITEM_ID);

		given(lineItemLookupStrategy.getLineItemIdsForCart(SCOPE, DECODED_CART_ID))
				.willReturn(ExecutionResultFactory.createReadOK(lineItemIds));

		ExecutionResult<Collection<String>> result = lineItemLookup.findIdsForCart(ENCODED_CART_ID, SCOPE);

		assertThat(result.getData(), hasItems(ENCODED_LINE_ITEM_ID));
	}

	@Test
	public void ensureNotFoundReturnedWhenCartForLineItemsNotFound() {
		given(lineItemLookupStrategy.getLineItemIdsForCart(SCOPE, DECODED_CART_ID))
				.willReturn(ExecutionResultFactory.<Collection<String>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemLookup.findIdsForCart(ENCODED_CART_ID, SCOPE);
	}

	@Test
	public void ensureServerErrorReturnedWhenLineItemsLookupFails() {
		given(lineItemLookupStrategy.getLineItemIdsForCart(SCOPE, DECODED_CART_ID))
				.willReturn(ExecutionResultFactory.<Collection<String>>createServerError(""));

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		lineItemLookup.findIdsForCart(ENCODED_CART_ID, SCOPE);
	}

	@Test
	public void ensurePurchasableitemCanBeQueriedCorrectly() {
		given(lineItemLookupStrategy.isItemPurchasable(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		ExecutionResult<Boolean> result = lineItemLookup.isItemPurchasable(SCOPE, ITEM_ID);

		assertExecutionResult(result).data(true);
	}

	@Test
	public void ensureNotFoundReturnedWhenItemForPurchasableQueryNotfound() {
		given(lineItemLookupStrategy.isItemPurchasable(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.<Boolean>createNotFound());

		ExecutionResult<Boolean> result = lineItemLookup.isItemPurchasable(SCOPE, ITEM_ID);

		assertExecutionResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorReturnedWhenPurchasableitemQueryFails() {
		given(lineItemLookupStrategy.isItemPurchasable(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.<Boolean>createServerError(""));

		ExecutionResult<Boolean> result = lineItemLookup.isItemPurchasable(SCOPE, ITEM_ID);

		assertExecutionResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	private ResourceState<CartEntity> createCartRepresentation() {
		return ResourceState.Builder.create(CartEntity.builder()
																	.withCartId(ENCODED_CART_ID)
																	.build())
										.withScope(SCOPE)
										.build();
	}
}

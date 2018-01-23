/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
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
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemWriterStrategy;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;

/**
 * Tests the {@link LineItemWriterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class LineItemWriterImplTest {
	public static final String SCOPE = "scope";
	public static final String DECODED_CART_ID = "cartId";
	public static final String ENCODED_CART_ID = Base32Util.encode(DECODED_CART_ID);
	public static final String DECODED_LINE_ITEM_ID = "lineItemId";
	public static final String ENCODED_LINE_ITEM_ID = Base32Util.encode(DECODED_LINE_ITEM_ID);
	public static final String ITEM_ID = "itemId";
	public static final int QUANTITY = 1;
	public static final String CART_SELF_URI = "/cartSelfUri";
	public static final String CREATED_LINE_ITEM_URI = "/createdLineItemUri";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private LineItemWriterStrategy lineItemWriterStrategy;
	@Mock
	private CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	@InjectMocks
	private LineItemWriterImpl lineItemWriter;
	@Mock
	private CartLineItemsUriBuilder cartLineItemUriBuilder;

	private ResourceState<CartEntity> cartRepresentation;

	@Before
	public void setUp() {
		given(cartLineItemsUriBuilderFactory.get()).willReturn(cartLineItemUriBuilder);
		given(cartLineItemUriBuilder.setSourceUri(CART_SELF_URI)).willReturn(cartLineItemUriBuilder);
		given(cartLineItemUriBuilder.setLineItemId(ENCODED_LINE_ITEM_ID)).willReturn(cartLineItemUriBuilder);
		given(cartLineItemUriBuilder.build()).willReturn(CREATED_LINE_ITEM_URI);

		cartRepresentation = ResourceState.Builder.create(CartEntity.builder()
																				.withCartId(ENCODED_CART_ID)
																				.build())
														.withSelf(SelfFactory.createSelf(CART_SELF_URI, CartsMediaTypes.CART.id()))
														.withScope(SCOPE)
														.build();
	}

	@Test
	public void ensureAllLineItemsCanBeRemovedSuccessfully() {
		given(lineItemWriterStrategy.deleteAllLineItemsFromCart(SCOPE, DECODED_CART_ID))
				.willReturn(ExecutionResultFactory.<Void>createDeleteOK());

		ExecutionResult<Void> result = lineItemWriter.removeAll(cartRepresentation);

		assertExecutionResult(result).resourceStatus(ResourceStatus.DELETE_OK);
	}

	@Test
	public void ensureLineItemCanBeRemovedSuccessfully() {
		given(lineItemWriterStrategy.deleteLineItemFromCart(SCOPE, getLineItemEntityToRemove()))
				.willReturn(ExecutionResultFactory.<Void>createDeleteOK());

		ExecutionResult<Void> result = lineItemWriter.remove(cartRepresentation, ENCODED_LINE_ITEM_ID);

		assertExecutionResult(result).resourceStatus(ResourceStatus.DELETE_OK);
	}

	@Test
	public void ensureNotFoundIsReturnedWhenLineItemToRemoveCanNotBeFound() {
		given(lineItemWriterStrategy.deleteLineItemFromCart(SCOPE, getLineItemEntityToRemove()))
				.willReturn(ExecutionResultFactory.<Void>createNotFound());

		ExecutionResult<Void> result = lineItemWriter.remove(cartRepresentation, ENCODED_LINE_ITEM_ID);

		assertExecutionResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorIsReturnedWhenLineItemFailsToBeRemoved() {
		given(lineItemWriterStrategy.deleteLineItemFromCart(SCOPE, getLineItemEntityToRemove()))
				.willReturn(ExecutionResultFactory.<Void>createServerError(""));

		ExecutionResult<Void> result = lineItemWriter.remove(cartRepresentation, ENCODED_LINE_ITEM_ID);

		assertExecutionResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void ensureNewLineItemCanBeAddedToCartSuccessfully() {
		given(lineItemWriterStrategy.addToCart(SCOPE, getLineItemEntityToAdd()))
				.willReturn(ExecutionResultFactory.createCreateOKWithData(getCreatedLineItemEntity(), false));

		ExecutionResult<ResourceState<ResourceEntity>> result = lineItemWriter.addLineItemToCart(cartRepresentation, ITEM_ID,
				getPostedLineItemEntityBuilder().build());

		assertExecutionResult(result).resourceStatus(ResourceStatus.CREATE_OK);
		assertEquals(CREATED_LINE_ITEM_URI, result.getData().getSelf().getUri());
	}

	@Test
	public void ensureExistingLineItemAddedToCartReturnsReadOk() {
		given(lineItemWriterStrategy.addToCart(SCOPE, getLineItemEntityToAdd()))
				.willReturn(ExecutionResultFactory.createCreateOKWithData(getCreatedLineItemEntity(), true));

		ExecutionResult<ResourceState<ResourceEntity>> result = lineItemWriter.addLineItemToCart(cartRepresentation, ITEM_ID,
				getPostedLineItemEntityBuilder().build());

		assertExecutionResult(result).resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void ensureNotFoundReturnedWhenItemToAddNotFound() {
		given(lineItemWriterStrategy.addToCart(SCOPE, getLineItemEntityToAdd()))
				.willReturn(ExecutionResultFactory.<LineItemEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemWriter.addLineItemToCart(cartRepresentation, ITEM_ID, getPostedLineItemEntityBuilder().build());
	}

	@Test
	public void ensureServerErrorReturnedWhenAddToCartFails() {
		given(lineItemWriterStrategy.addToCart(SCOPE, getLineItemEntityToAdd()))
				.willReturn(ExecutionResultFactory.<LineItemEntity>createServerError(""));

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		lineItemWriter.addLineItemToCart(cartRepresentation, ITEM_ID,
				getPostedLineItemEntityBuilder().build());
	}

	@Test
	public void ensureAddingLineItemWithInvalidQuantityFieldReturnsStateFailure() {
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		lineItemWriter.addLineItemToCart(cartRepresentation, ITEM_ID,
				getPostedLineItemEntityBuilder()
						.withQuantity(null)
						.build());
	}

	@Test
	public void ensureLineItemCanBeUpdatedCorrectly() {
		given(lineItemWriterStrategy.updateLineItem(SCOPE, getLineItemEntityToUpdate())).willReturn(ExecutionResultFactory.<Void>createUpdateOK());

		ExecutionResult<Void> result = lineItemWriter.update(cartRepresentation, ENCODED_LINE_ITEM_ID, getPostedLineItemEntityBuilder().build());

		assertExecutionResult(result).resourceStatus(ResourceStatus.UPDATE_OK);
	}

	@Test
	public void ensureNotFoundReturnedWhenLineItemToUpdateNotFound() {
		given(lineItemWriterStrategy.updateLineItem(SCOPE, getLineItemEntityToUpdate())).willReturn(ExecutionResultFactory.<Void>createNotFound());

		ExecutionResult<Void> result = lineItemWriter.update(cartRepresentation, ENCODED_LINE_ITEM_ID, getPostedLineItemEntityBuilder().build());

		assertExecutionResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorReturnedWhenLineItemUpdateFails() {
		given(lineItemWriterStrategy.updateLineItem(SCOPE, getLineItemEntityToUpdate()))
				.willReturn(ExecutionResultFactory.<Void>createServerError(""));

		ExecutionResult<Void> result = lineItemWriter.update(cartRepresentation, ENCODED_LINE_ITEM_ID, getPostedLineItemEntityBuilder().build());

		assertExecutionResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void ensureUpdatingLineItemWithInvalidQuantityFormatReturnsStateFailure() {
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		lineItemWriter.update(cartRepresentation, ENCODED_LINE_ITEM_ID,
				getPostedLineItemEntityBuilder()
						.withQuantity(null)
						.build());
	}

	private LineItemEntity.Builder getPostedLineItemEntityBuilder() {
		return LineItemEntity.builder()
								.withQuantity(QUANTITY);
	}

	private LineItemEntity getLineItemEntityToRemove() {
		return LineItemEntity.builder()
								.withCartId(DECODED_CART_ID)
								.withLineItemId(DECODED_LINE_ITEM_ID)
								.build();
	}

	private LineItemEntity getLineItemEntityToAdd() {
		return LineItemEntity.builder()
								.withCartId(DECODED_CART_ID)
								.withItemId(ITEM_ID)
								.withQuantity(QUANTITY)
								.build();
	}

	private LineItemEntity getLineItemEntityToUpdate() {
		return LineItemEntity.builder()
								.withCartId(DECODED_CART_ID)
								.withQuantity(QUANTITY)
								.withLineItemId(DECODED_LINE_ITEM_ID)
								.build();
	}

	private LineItemEntity getCreatedLineItemEntity() {
		return LineItemEntity.builder()
								.withCartId(DECODED_CART_ID)
								.withItemId(ITEM_ID)
								.withQuantity(QUANTITY)
								.withLineItemId(DECODED_LINE_ITEM_ID)
								.build();
	}


}

/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.Before;
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
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.definition.carts.CartsMediaTypes;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.carts.lineitems.LineItemLookup;
import com.elasticpath.rest.resource.carts.lineitems.LineItemWriter;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilder;
import com.elasticpath.rest.schema.uri.CartLineItemsUriBuilderFactory;

/**
 * Tests the {@link LineItemResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemResourceOperatorImplTest {
	private static final String LINE_ITEM_ID = "lineItemId";
	private static final String ITEM_ID = "itemId";
	public static final String CART_URI = "/cartUri";
	public static final String LINE_ITEMS_SELF_URI = "/lineItemsSelfUri";
	public static final String CART_ID = "cartId";
	public static final String SCOPE = "scope";
	public static final String QUANTITY_IS_EITHER_MISSING_OR_IS_NOT_AN_INTEGER = "Quantity is either missing or is not an integer";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private LineItemLookup lineItemLookup;
	@Mock
	private LineItemWriter lineItemWriter;
	@Mock
	private CartLineItemsUriBuilderFactory cartLineItemsUriBuilderFactory;
	@InjectMocks
	private LineItemResourceOperatorImpl lineItemResourceOperator;
	@Mock
	private ResourceOperation resourceOperation;
	@Mock
	private ResourceState<LineItemEntity> lineItemResource;

	private ResourceState postedLineItemRepresentation;
	private ResourceState<CartEntity> cart;
	private LineItemEntity postedLineItemEntity;

	@Before
	public void setUp() {
		postedLineItemEntity = LineItemEntity.builder().build();
		postedLineItemRepresentation = ResourceState.Builder.create(postedLineItemEntity).build();

		given(resourceOperation.getResourceState()).willReturn(postedLineItemRepresentation);

		CartLineItemsUriBuilder cartLineItemsUriBuilder = mock(CartLineItemsUriBuilder.class);
		given(cartLineItemsUriBuilderFactory.get()).willReturn(cartLineItemsUriBuilder);
		given(cartLineItemsUriBuilder.setSourceUri(CART_URI)).willReturn(cartLineItemsUriBuilder);
		given(cartLineItemsUriBuilder.build()).willReturn(LINE_ITEMS_SELF_URI);

		cart = ResourceState.Builder.create(CartEntity.builder()
											.withCartId(CART_ID)
											.build())
				.withScope(SCOPE)
				.withSelf(SelfFactory.createSelf(CART_URI))
				.build();

	}

	@Test
	public void ensureValidLineItemCanBeCreatedForCart() {
		given(lineItemWriter.addLineItemToCart(cart, ITEM_ID, postedLineItemEntity))
				.willReturn(ExecutionResultFactory.<ResourceState<?>>createCreateOK("/newCartLineItem", false));

		OperationResult result = lineItemResourceOperator.processCreateLineItem(cart, ITEM_ID, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.CREATE_OK);
	}

	@Test
	public void inValidLineItemThrowsBCE() {
		given(lineItemWriter.addLineItemToCart(cart, ITEM_ID, postedLineItemEntity))
				.willThrow(
						new BrokenChainException(
								ExecutionResultFactory.<ResourceState<?>>createBadRequestBody(QUANTITY_IS_EITHER_MISSING_OR_IS_NOT_AN_INTEGER)));
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		lineItemResourceOperator.processCreateLineItem(cart, ITEM_ID, resourceOperation);
	}

	@Test
	public void ensureServerErrorIsReturnedWhenLineItemCanNotBeAddedToCart() {
		given(lineItemWriter.addLineItemToCart(cart, ITEM_ID, postedLineItemEntity))
				.willReturn(ExecutionResultFactory.<ResourceState<ResourceEntity>>createServerError(""));

		OperationResult result = lineItemResourceOperator.processCreateLineItem(cart, ITEM_ID, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void ensureValidLineItemCanBeDeletedSuccessfullyFromCart() {
		given(lineItemWriter.remove(cart, LINE_ITEM_ID)).willReturn(ExecutionResultFactory.<Void>createDeleteOK());

		OperationResult result = lineItemResourceOperator.processDeleteLineItem(cart, LINE_ITEM_ID, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.DELETE_OK);
	}

	@Test
	public void lineItemToDeleteNotFoundThrowsBCE() {
		given(lineItemWriter.remove(cart, LINE_ITEM_ID))
				.willThrow(new BrokenChainException(ExecutionResultFactory.<Void>createNotFound()));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemResourceOperator.processDeleteLineItem(cart, LINE_ITEM_ID, resourceOperation);
	}

	@Test
	public void failedLineItemDeletionThrowsBCE() {
		given(lineItemWriter.remove(cart, LINE_ITEM_ID)).willThrow(new BrokenChainException(ExecutionResultFactory.<Void>createNotFound()));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemResourceOperator.processDeleteLineItem(cart, LINE_ITEM_ID, resourceOperation);
	}

	@Test
	public void ensureAllLineItemsCanBeReadForCart() {
		OperationResult result = lineItemResourceOperator.processReadLineItems(cart, resourceOperation);

		LinksEntity expectedLineItemLinksEntity = LinksEntity.builder()
				.withElementListId(CART_ID)
				.withElementListType(CartsMediaTypes.CART.id())
				.build();

		ResourceState<LinksEntity> expectedLineItemLinksResourceState = ResourceState.Builder
				.create(expectedLineItemLinksEntity)
				.withSelf(SelfFactory.createSelf(LINE_ITEMS_SELF_URI))
				.withScope(SCOPE)
				.build();

		assertOperationResult(result).resourceState(expectedLineItemLinksResourceState);
	}

	@Test
	public void ensureValidLineItemCanBeReadFromCart() {
		given(lineItemLookup.find(cart, LINE_ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(lineItemResource));

		OperationResult result = lineItemResourceOperator.processReadLineItem(cart, LINE_ITEM_ID, resourceOperation);

		assertOperationResult(result)
				.resourceState(lineItemResource)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void ensureNotFoundReturnedWhenLineItemNotFound() {
		given(lineItemLookup.find(cart, LINE_ITEM_ID))
				.willThrow(new BrokenChainException(ExecutionResultFactory.<ResourceState<LineItemEntity>>createNotFound()));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemResourceOperator.processReadLineItem(cart, LINE_ITEM_ID, resourceOperation);
	}

	@Test
	public void ensureServerErrorReturnedWhenLineItemLookupFails() {
		given(lineItemLookup.find(cart, LINE_ITEM_ID))
				.willReturn(ExecutionResultFactory.<ResourceState<LineItemEntity>>createServerError(""));

		OperationResult result = lineItemResourceOperator.processReadLineItem(cart, LINE_ITEM_ID, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void ensureValidLineItemCanBeUpdatedCorrectly() {
		given(lineItemWriter.update(cart, LINE_ITEM_ID, postedLineItemEntity))
				.willReturn(ExecutionResultFactory.<Void>createUpdateOK());

		OperationResult result = lineItemResourceOperator.processUpdateLineItem(cart, LINE_ITEM_ID, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.UPDATE_OK);
	}

	@Test
	public void ensureNotFoundReturnedWhenLineItemToUpdateIsNotFound() {
		given(lineItemWriter.update(cart, LINE_ITEM_ID, postedLineItemEntity))
				.willReturn(ExecutionResultFactory.<Void>createNotFound());

		OperationResult result = lineItemResourceOperator.processUpdateLineItem(cart, LINE_ITEM_ID, resourceOperation);

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void ensureServerErrorReturnedWhenLineItemFailsToUpdate() {
		given(lineItemWriter.update(cart, LINE_ITEM_ID, postedLineItemEntity))
				.willThrow(
						new BrokenChainException(
								ExecutionResultFactory.<Void>createBadRequestBody(QUANTITY_IS_EITHER_MISSING_OR_IS_NOT_AN_INTEGER)));
		thrown.expect(containsResourceStatus(ResourceStatus.BAD_REQUEST_BODY));

		lineItemResourceOperator.processUpdateLineItem(cart, LINE_ITEM_ID, resourceOperation);
	}
}

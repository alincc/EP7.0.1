/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformer;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformerImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Tests the {@link LineItemWriterStrategyImpl} deleteLineItemFromCart.
 */
public class LineItemWriterStrategyImplDeleteLineItemTest {
	private static final String RESULT_SHOULD_BE_SUCCESSFUL = "Result Should be Successful";
	private static final String STORECODE = "STORECODE";
	private static final String ITEM_ID = "item id";
	private static final String LINE_ITEM_GUID = "item guid";
	private static final int QUANTITY = 2;
	private static final String CART_GUID = "cart guid";
	private static final long SHOPPING_ITEM_UID = 4;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ShoppingCart cart = context.mock(ShoppingCart.class);
	private final Store mockStore = context.mock(Store.class);
	private final ShoppingItem mockShoppingItem = context.mock(ShoppingItem.class);

	private final ShoppingCartRepository mockShoppingCartRepository = context.mock(ShoppingCartRepository.class);
	private final ItemRepository mockItemRepository = context.mock(ItemRepository.class);
	private final StoreProductRepository mockStoreProductRepository = context.mock(StoreProductRepository.class);
	private final ProductSkuRepository mockProductSkuRepository = context.mock(ProductSkuRepository.class);
	private final LineItemTransformer lineItemTransformer = new LineItemTransformerImpl();
	private final LineItemWriterStrategyImpl strategy = new LineItemWriterStrategyImpl(
			mockShoppingCartRepository,
			mockItemRepository, mockStoreProductRepository, mockProductSkuRepository,
			lineItemTransformer,
			null);

	/**
	 * Test Delete Line Item From Cart Happy Path.
	 */
	@Test
	public void testDeleteLineItemFromCart() {
		mockShoppingCartExpectations(CART_GUID);

		LineItemEntity entity = createLineItemEntity();

		context.checking(new Expectations() {
			{
				allowing(cart).getShoppingItemByGuid(LINE_ITEM_GUID);
				will(returnValue(mockShoppingItem));

				allowing(mockShoppingCartRepository).removeItemFromCart(cart, SHOPPING_ITEM_UID);
				will(returnValue(ExecutionResultFactory.createDeleteOK()));
			}
		});
		ExecutionResult<Void> executionResult = strategy.deleteLineItemFromCart(STORECODE, entity);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, executionResult.isSuccessful());
	}

	/**
	 * Test Delete Line Item From Cart When Save Cart is Failure.
	 */
	@Test
	public void testDeleteLineItemFromCartWhenSaveCartIsFailure() {
		mockShoppingCartExpectations(CART_GUID);

		LineItemEntity entity = createLineItemEntity();

		context.checking(new Expectations() {
			{
				allowing(cart).getShoppingItemByGuid(LINE_ITEM_GUID);
				will(returnValue(mockShoppingItem));

				allowing(mockShoppingCartRepository).removeItemFromCart(cart, SHOPPING_ITEM_UID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.deleteLineItemFromCart(STORECODE, entity);
	}

	/**
	 * Test Delete Line Item From Cart When Line Item is Null.
	 */
	@Test
	public void testDeleteLineItemFromCartWhenLineItemNull() {
		mockShoppingCartExpectations(CART_GUID);

		LineItemEntity entity = createLineItemEntity();

		context.checking(new Expectations() {
			{
				allowing(cart).getShoppingItemByGuid(LINE_ITEM_GUID);
				will(returnValue(null));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.deleteLineItemFromCart(STORECODE, entity);
	}

	/**
	 * Test Delete Line Item From Cart When Cart Not Found.
	 */
	@Test
	public void testDeleteLineItemFromCartWhenCartNotFound() {
		LineItemEntity entity = createLineItemEntity();

		context.checking(new Expectations() {
			{
				allowing(mockShoppingCartRepository).getShoppingCart(CART_GUID);
				will(returnValue(ExecutionResultFactory.createNotFound("not found")));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		strategy.deleteLineItemFromCart(STORECODE, entity);
	}

	private ShoppingCartRepository mockShoppingCartExpectations(final String cartGuid) {
		if (cartGuid == null) {
			context.checking(new Expectations() {
				{
					allowing(mockShoppingCartRepository).getDefaultShoppingCart();
					will(returnValue(ExecutionResultFactory.createReadOK(cart)));
				}
			});
		} else {
			context.checking(new Expectations() {
				{
					allowing(mockShoppingCartRepository).getShoppingCart(CART_GUID);
					will(returnValue(ExecutionResultFactory.createReadOK(cart)));
				}
			});
		}
		context.checking(new Expectations() {
			{
				allowing(cart).getGuid();
				will(returnValue(CART_GUID));

				allowing(cart).getStore();
				will(returnValue(mockStore));

				allowing(mockStore).getCode();
				will(returnValue(STORECODE));

				allowing(cart).getCartItemByGuid(LINE_ITEM_GUID);
				will(returnValue(mockShoppingItem));

				allowing(mockShoppingItem).getUidPk();
				will(returnValue(SHOPPING_ITEM_UID));

				allowing(cart).removeCartItem(SHOPPING_ITEM_UID);
			}
		});
		return mockShoppingCartRepository;
	}

	private LineItemEntity createLineItemEntity() {
		return LineItemEntity.builder().withItemId(ITEM_ID)
				.withLineItemId(LINE_ITEM_GUID)
				.withCartId(CART_GUID)
				.withQuantity(QUANTITY)
				.build();
	}
}

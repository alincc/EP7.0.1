/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.CartEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.transform.ShoppingCartTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Tests for {@link CartLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartLookupStrategyImplTest {

	private static final String STORE_CODE = "STORE_CODE";
	private static final String CART_GUID = "CART_GUID";
	private static final String SKU_CODE = "SKU_CODE";
	private static final String ITEM_GUID = "ITEM_GUID";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private ShoppingCartTransformer shoppingCartTransformer;
	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private CartLookupStrategyImpl cartLookupStrategy;


	/**
	 * Tests happy path for getting a cart with a given GUID.
	 */
	@Test
	public void testGetCartWithSuccessfulCartResult() {
		ShoppingCart shoppingCart = Mockito.mock(ShoppingCart.class);
		CartEntity expectedCartEntity = Mockito.mock(CartEntity.class);

		shouldGetShoppingCartWithResult(CART_GUID, ExecutionResultFactory.createReadOK(shoppingCart));
		shouldTransformShoppingCartToCartEntity(shoppingCart, expectedCartEntity);

		ExecutionResult<CartEntity> result = cartLookupStrategy.getCart(STORE_CODE, CART_GUID);

		assertTrue("This should be a successful operation.", result.isSuccessful());
		assertEquals("The resulting data should be as expected.", expectedCartEntity, result.getData());
	}

	@Test
	public void testGetCartWithFailedCartResult() {
		shouldGetShoppingCartWithResult(CART_GUID, ExecutionResultFactory.<ShoppingCart>createNotFound("cart not found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		cartLookupStrategy.getCart(STORE_CODE, CART_GUID);
	}

	private void shouldGetShoppingCartWithResult(
			final String cartGuid,
			final ExecutionResult<ShoppingCart> result) {

		when(shoppingCartRepository.getShoppingCart(cartGuid))
				.thenReturn(result);
	}

	private void shouldTransformShoppingCartToCartEntity(final ShoppingCart shoppingCart, final CartEntity expectedCartEntity) {
		when(shoppingCartTransformer.transformToEntity(shoppingCart))
				.thenReturn(expectedCartEntity);
	}

	/**
	 * Test finding a cart containing an item when cart does not contain the item.
	 */
	@Test
	public void testFindNotContainingItem() {
		ShoppingCart shoppingCart = Mockito.mock(ShoppingCart.class);
		when(shoppingCart.getCartItem(SKU_CODE)).thenReturn(null);
		when(shoppingCartRepository.getDefaultShoppingCart()).thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));

		ProductSku productSku = Mockito.mock(ProductSku.class);
		when(itemRepository.getSkuForItemId(Mockito.anyString())).thenReturn(ExecutionResultFactory.createReadOK(productSku));
		when(productSku.getSkuCode()).thenReturn(SKU_CODE);

		ExecutionResult<Collection<String>> result = cartLookupStrategy.findContainingItem(ITEM_GUID);

		assertTrue("The result should be a success", result.isSuccessful());
		assertEquals("Zero results expected", 0, result.getData().size());
	}

	/**
	 * Test finding a cart containing an item when cart does contain the item.
	 */
	@Test
	public void testFindContainingItem() {
		ShoppingItem shoppingItem = Mockito.mock(ShoppingItem.class);
		ShoppingCart shoppingCart = Mockito.mock(ShoppingCart.class);
		when(shoppingCart.getCartItem(SKU_CODE)).thenReturn(shoppingItem);
		when(shoppingCartRepository.getDefaultShoppingCart()).thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));

		ProductSku productSku = Mockito.mock(ProductSku.class);
		when(itemRepository.getSkuForItemId(Mockito.anyString())).thenReturn(ExecutionResultFactory.createReadOK(productSku));
		when(productSku.getSkuCode()).thenReturn(SKU_CODE);

		ExecutionResult<Collection<String>> result = cartLookupStrategy.findContainingItem(ITEM_GUID);

		assertTrue("The result should be a success", result.isSuccessful());
		assertEquals("Zero results expected", 1, result.getData().size());
	}
}

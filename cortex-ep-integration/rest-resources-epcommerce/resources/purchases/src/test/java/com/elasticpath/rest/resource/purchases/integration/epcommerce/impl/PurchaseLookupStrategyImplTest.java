/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.purchases.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.purchases.integration.epcommerce.transform.OrderTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link PurchaseLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseLookupStrategyImplTest {

	private static final String STORE_CODE = "STORE_CODE";
	private static final String USER_GUID = "USER_GUID";
	private static final String ORDER_GUID = "ORDER_GUID";
	private static final String SHOPPING_CART_GUID = "SHOPPING_CART_GUID";
	private static final String CART_ITEM_GUID = "cart item guid";
	private static final String SKU_CODE = "sku";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private OrderTransformer orderTransformer;
	@Mock
	private StoreProductRepository storeProductRepository;

	@InjectMocks
	private PurchaseLookupStrategyImpl purchaseLookupStrategy;

	@Mock
	private CartItem cartItem;
	@Mock
	private StoreProduct storeProduct;

	/**
	 * Test {@link PurchaseLookupStrategyImpl#isOrderPurchasable(String, String)} for valid order.
	 */
	@Test
	public void testIsOrderPurchasableWithValidOrder() {
		ProductSku productSku = mock(ProductSku.class);

		CartOrder cartOrder = createCartOrder();

		when(cartItem.getSkuGuid()).thenReturn(CART_ITEM_GUID);
		when(storeProductRepository.findDisplayableStoreProductWithAttributesBySkuGuid(STORE_CODE, CART_ITEM_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(storeProduct));
		when(storeProduct.getSkuByGuid(CART_ITEM_GUID)).thenReturn(productSku);
		when(productSku.getSkuCode()).thenReturn(SKU_CODE);
		when(storeProduct.isSkuAvailable(SKU_CODE)).thenReturn(true);

		shouldFindCartOrderByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldShoppingCartHasItems(true);

		ExecutionResult<Boolean> result = purchaseLookupStrategy.isOrderPurchasable(STORE_CODE, ORDER_GUID);

		assertTrue(result.isSuccessful());
		assertTrue("Order should be purchasable.", result.getData());
	}

	/**
	 * When cart contains at least one unavailable item, the order is not purchasable.
	 */
	@Test
	public void testOrderIsNotPurchasableWhenCartHasUnavailableItems() {
		ProductSku productSku = mock(ProductSku.class);
		CartOrder cartOrder = createCartOrder();

		when(cartItem.getSkuGuid()).thenReturn(CART_ITEM_GUID);
		when(storeProductRepository.findDisplayableStoreProductWithAttributesBySkuGuid(STORE_CODE, CART_ITEM_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(storeProduct));
		when(storeProduct.getSkuByGuid(CART_ITEM_GUID)).thenReturn(productSku);
		when(productSku.getSkuCode()).thenReturn(SKU_CODE);
		when(storeProduct.isSkuAvailable(SKU_CODE)).thenReturn(false);

		shouldFindCartOrderByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldShoppingCartHasItems(true);

		ExecutionResult<Boolean> result = purchaseLookupStrategy.isOrderPurchasable(STORE_CODE, ORDER_GUID);

		assertTrue(result.isSuccessful());
		assertFalse("Order should be purchasable.", result.getData());
	}

	/**
	 * Test {@link PurchaseLookupStrategyImpl#isOrderPurchasable(String, String)} for valid order with empty cart is not purchasable.
	 */
	@Test
	public void testIsOrderPurchasableWithValidOrderWithEmptyCart() {
		CartOrder cartOrder = createCartOrder();

		shouldFindCartOrderByGuidWithResult(ExecutionResultFactory.createReadOK(cartOrder));
		shouldShoppingCartHasItems(false);

		ExecutionResult<Boolean> result = purchaseLookupStrategy.isOrderPurchasable(STORE_CODE, ORDER_GUID);

		assertTrue(result.isSuccessful());
		assertFalse("Order should not be purchasable.", result.getData());
	}

	/**
	 * Test {@link PurchaseLookupStrategyImpl#isOrderPurchasable(String, String)} for invalid order.
	 */
	@Test
	public void testIsOrderPurchasableWithInvalidOrder() {
		shouldFindCartOrderByGuidWithResult(ExecutionResultFactory.<CartOrder>createNotFound("Not found."));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLookupStrategy.isOrderPurchasable(STORE_CODE, ORDER_GUID);
	}

	@Test
	public void testGetOrdersForProfile() {
		when(orderRepository.findOrderIdsByCustomerGuid(STORE_CODE, USER_GUID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createReadOK(Collections.singleton(ORDER_GUID)));

		ExecutionResult<Collection<String>> result = purchaseLookupStrategy.getPurchaseIds(STORE_CODE, USER_GUID);

		assertTrue(result.isSuccessful());
		assertEquals(ORDER_GUID, CollectionUtil.first(result.getData()));
	}

	/**
	 * Test the behaviour of get purchase.
	 */
	@Test
	public void testGetPurchase() {
		Order order = createMockOrder(null);
		PurchaseEntity purchaseEntity = mock(PurchaseEntity.class);

		shouldFindSubject();
		shouldFindOrderByGuidWithResult(ExecutionResultFactory.createReadOK(order));
		shouldTransformToEntity(order, purchaseEntity);

		ExecutionResult<PurchaseEntity> result = purchaseLookupStrategy.getPurchase(STORE_CODE, ORDER_GUID);

		assertTrue("The operation should have been successful", result.isSuccessful());
		assertEquals("The returned DTO should be the one provided by the transformer", purchaseEntity, result.getData());
	}

	/**
	 * Test the behaviour of get purchase when find fails.
	 */
	@Test
	public void testGetPurchaseWhenFindFails() {
		shouldFindOrderByGuidWithResult(ExecutionResultFactory.<Order>createNotFound("No orders found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		purchaseLookupStrategy.getPurchase(STORE_CODE, ORDER_GUID);
	}

	private void shouldFindSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USER_GUID, Locale.ENGLISH);
		when(resourceOperationContext.getSubject())
				.thenReturn(subject);
	}


	private void shouldShoppingCartHasItems(final boolean cartHasItems) {
		List<ShoppingItem> cartItems = new ArrayList<>();
		if (cartHasItems) {
			cartItems.add(cartItem);
		}
		final ShoppingCart shoppingCart = mock(ShoppingCart.class);
		when(shoppingCartRepository.getShoppingCart(SHOPPING_CART_GUID)).thenReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		when(shoppingCart.getCartItems()).thenReturn(cartItems);
	}

	private void shouldFindCartOrderByGuidWithResult(final ExecutionResult<CartOrder> result) {
		when(cartOrderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(result);
	}

	private void shouldFindOrderByGuidWithResult(final ExecutionResult<Order> result) {
		when(orderRepository.findByGuid(STORE_CODE, ORDER_GUID)).thenReturn(result);
	}

	private void shouldTransformToEntity(final Order order, final PurchaseEntity purchaseEntity) {
		when(orderTransformer.transformToEntity(order, Locale.ENGLISH)).thenReturn(purchaseEntity);
	}

	private Order createMockOrder(final OrderStatus orderStatus) {
		Order order = mock(Order.class);
		stub(order.getStatus()).toReturn(orderStatus);
		return order;
	}

	private CartOrder createCartOrder() {
		CartOrder cartOrder = new CartOrderImpl();
		cartOrder.setShoppingCartGuid(SHOPPING_CART_GUID);
		return cartOrder;
	}
}

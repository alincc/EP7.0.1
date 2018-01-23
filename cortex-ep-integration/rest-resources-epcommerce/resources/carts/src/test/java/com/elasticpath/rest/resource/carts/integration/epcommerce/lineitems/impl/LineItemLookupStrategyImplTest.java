/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * The test of {@link LineItemLookupStrategyImpl}.
 */
@SuppressWarnings({"PMD.TooManyMethods"})
@RunWith(MockitoJUnitRunner.class)
public class LineItemLookupStrategyImplTest {

	private static final String SHOPPING_ITEM_GUID = "shoppingItemGuid";
	private static final String ITEM_ID = "itemId";
	private static final String SKU_CODE = "sku_code";
	private static final String SKU_GUID = "sku_guid";
	private static final String STORECODE = "storecode";
	private static final String CART_GUID = "cartGuid";
	private static final String RESULT_SHOULD_BE_SUCCESSFUL = "The Result Should be Successful";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private Product product;

	@Mock
	private Shopper shopper;

	@Mock
	private LineItemEntity lineItemEntity;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private LineItemTransformer lineItemTransformer;

	@Mock
	private ProductSkuRepository productSkuRepository;

	@Mock
	private StoreProductRepository storeProductRepository;

	@Mock
	private PriceRepository priceRepository;

	@Mock
	private CartItemModifiersRepository cartItemModifiersRepository;

	@InjectMocks
	private LineItemLookupStrategyImpl lineItemLookupStrategy;


	/**
	 * Test Get Line item happy path.
	 */
	@Test
	public void testGetLineItemWhenCartFoundAndItemFound() {
		ProductSku productSku = createMockProductSku(product, SKU_CODE);
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		ShoppingCart shoppingCart = createMockShoppingCart(shoppingItem);
		LineItem lineItem = createLineItem(shoppingItem, CART_GUID, ITEM_ID);
		CartItemModifierField cartItemModifierField = mock(CartItemModifierField.class);
		final ImmutableMap<CartItemModifierField, String> fieldValues = ImmutableMap.of(cartItemModifierField, "value");
		lineItem.setCartItemModifierValues(fieldValues);

		shouldGetShoppingCartWithResult(ExecutionResultFactory.createReadOK(shoppingCart));
		shouldGetItemIdForProductSkuWithResult(productSku, ExecutionResultFactory.createReadOK(ITEM_ID));
		shouldGetCartItemModifiers(fieldValues);
		shouldTransformToEntity(lineItem, lineItemEntity);

		ExecutionResult<LineItemEntity> result = lineItemLookupStrategy.getLineItem(STORECODE, CART_GUID, SHOPPING_ITEM_GUID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals("result should be as expected", lineItemEntity, result.getData());
	}

	/**
	 * Test Get Line item when cart result is not found.
	 */
	@Test
	public void testGetLineItemWhenCartResultNotFound() {
		shouldGetShoppingCartWithResult(ExecutionResultFactory.<ShoppingCart>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemLookupStrategy.getLineItem(STORECODE, CART_GUID, SHOPPING_ITEM_GUID);
	}

	/**
	 * Test Get Line item when Shopping Item is null.
	 */
	@Test
	public void testGetLineItemWhenShoppingItemNull() {
		ShoppingCart shoppingCart = createMockShoppingCart(null);

		shouldGetShoppingCartWithResult(ExecutionResultFactory.<ShoppingCart>createReadOK(shoppingCart));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemLookupStrategy.getLineItem(STORECODE, CART_GUID, SHOPPING_ITEM_GUID);
	}

	/**
	 * Test Get Line item when Item is not found.
	 */
	@Test
	public void testGetLineItemWhenItemNotFound() {
		ProductSku productSku = createMockProductSku(product, SKU_CODE);
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		ShoppingCart shoppingCart = createMockShoppingCart(shoppingItem);

		shouldGetShoppingCartWithResult(ExecutionResultFactory.createReadOK(shoppingCart));
		shouldGetItemIdForProductSkuWithResult(productSku, ExecutionResultFactory.<String>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemLookupStrategy.getLineItem(STORECODE, CART_GUID, SHOPPING_ITEM_GUID);
	}

	@Test
	public void ensureItemIsPurchasableIfIsPurchasableAndNotSoldAndPriceExists() {
		mockItemPurchasable(true, false, true);

		ExecutionResult<Boolean> result = lineItemLookupStrategy.isItemPurchasable(STORECODE, ITEM_ID);

		assertEquals(ExecutionResultFactory.createReadOK(Boolean.TRUE), result);
	}

	@Test
	public void ensureItemIsNotPurchasableIfIsNotPurchasableAndNotSoldAndPriceExists() {
		mockItemPurchasable(false, false, true);

		ExecutionResult<Boolean> result = lineItemLookupStrategy.isItemPurchasable(STORECODE, ITEM_ID);

		assertEquals(ExecutionResultFactory.createReadOK(Boolean.FALSE), result);
	}

	@Test
	public void ensureItemIsNotPurchasableIfIsPurchasableAndSoldAndPriceExists() {
		mockItemPurchasable(true, true, true);

		ExecutionResult<Boolean> result = lineItemLookupStrategy.isItemPurchasable(STORECODE, ITEM_ID);

		assertEquals(ExecutionResultFactory.createReadOK(Boolean.FALSE), result);
	}

	@Test
	public void ensureItemIsNotPurchasableIfIsPurchasableAndNotSoldAndNoPriceExists() {
		mockItemPurchasable(true, false, false);

		ExecutionResult<Boolean> result = lineItemLookupStrategy.isItemPurchasable(STORECODE, ITEM_ID);

		assertEquals(ExecutionResultFactory.createReadOK(Boolean.FALSE), result);
	}

	private void mockItemPurchasable(final boolean isPurchasable, final boolean isNotSoldSeparately, final boolean priceExists) {
		ProductSku productSku = mock(ProductSku.class);
		Product product = mock(Product.class);
		when(productSku.getProduct()).thenReturn(product);
		when(productSku.getSkuCode()).thenReturn(SKU_CODE);
		when(itemRepository.getSkuForItemId(ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(productSku));
		StoreProduct storeProduct = mock(StoreProduct.class);
		when(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(STORECODE, product.getGuid())).thenReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		//is purchase able
		when(storeProduct.isSkuPurchasable(SKU_CODE)).thenReturn(isPurchasable);
		// is not sold separately
		when(storeProduct.isNotSoldSeparately()).thenReturn(isNotSoldSeparately);
		// price exists
		when(priceRepository.priceExists(STORECODE, ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(priceExists));
	}

	/**
	 * Tests get line item ids for cart with appropriate parameters.
	 */
	@Test
	public void testGetLineItemIdsForCartWithAppropriateParameters() {
		ProductSku productSku = createMockProductSku(product, SKU_CODE);
		ShoppingItem shoppingItem = createMockShoppingItem(productSku);
		ShoppingCart shoppingCart = createMockShoppingCart(shoppingItem);

		shouldGetShoppingCartWithResult(ExecutionResultFactory.createReadOK(shoppingCart));

		ExecutionResult<Collection<String>> result = lineItemLookupStrategy.getLineItemIdsForCart(STORECODE, CART_GUID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertTrue("The collection of line item ids should return should only have on element",
				CollectionUtil.containsOnly(Collections.singleton(SHOPPING_ITEM_GUID), result.getData()));
	}

	/**
	 * Tests get line item ids for cart when shopping cart not found.
	 */
	@Test
	public void testGetLineItemIdsForCartWhenShoppingCartNotFound() {
		shouldGetShoppingCartWithResult(ExecutionResultFactory.<ShoppingCart>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemLookupStrategy.getLineItemIdsForCart(STORECODE, CART_GUID);
	}

	private void shouldGetItemIdForProductSkuWithResult(final ProductSku productSku, final ExecutionResult<String> result) {

		when(itemRepository.getItemIdForSku(productSku)).thenReturn(result);
	}

	private void shouldGetShoppingCartWithResult(final ExecutionResult<ShoppingCart> result) {
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(result);
	}

	private void shouldGetCartItemModifiers(final Map<CartItemModifierField, String> fieldValues) {
		when(cartItemModifiersRepository.findCartItemModifierValues(CART_GUID, SHOPPING_ITEM_GUID)).thenReturn(
			ExecutionResultFactory.createReadOK(fieldValues));
	}

	private void shouldTransformToEntity(final LineItem lineItem, final LineItemEntity result) {
		when(lineItemTransformer.transformToEntity(lineItem)).thenReturn(result);
	}

	private ProductSku createMockProductSku(final Product product, final String skuCode) {
		ProductSku productSku = mock(ProductSku.class);
		stub(productSku.getProduct()).toReturn(product);
		stub(productSku.getSkuCode()).toReturn(skuCode);
		return productSku;
	}

	private ShoppingItem createMockShoppingItem(final ProductSku productSku) {
		ShoppingItem shoppingItem = mock(ShoppingItem.class);
		stub(shoppingItem.getSkuGuid()).toReturn(SKU_GUID);
		stub(shoppingItem.getGuid()).toReturn(SHOPPING_ITEM_GUID);

		when(productSkuRepository.getProductSkuWithAttributesByGuid(SKU_GUID)).thenReturn(ExecutionResultFactory.createReadOK(productSku));

		return shoppingItem;
	}

	private ShoppingCart createMockShoppingCart(final ShoppingItem shoppingItem) {
		ShoppingCart shoppingCart = mock(ShoppingCart.class);
		stub(shoppingCart.getCartItemByGuid(SHOPPING_ITEM_GUID)).toReturn(shoppingItem);
		stub(shoppingCart.getShopper()).toReturn(shopper);
		stub(shoppingCart.getGuid()).toReturn(CART_GUID);
		stub(shoppingCart.getAllItems()).toReturn(Arrays.asList(shoppingItem));
		return shoppingCart;
	}

	private LineItem createLineItem(final ShoppingItem shoppingItem, final String cartGuid, final String itemId) {
		return ResourceTypeFactory.createResourceEntity(LineItem.class)
			.setShoppingItem(shoppingItem)
			.setCartId(cartGuid)
			.setItemId(itemId)
			.setCartItemModifierValues(Collections.emptyMap());
	}

}

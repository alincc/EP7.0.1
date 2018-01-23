/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformer;
import com.elasticpath.rest.resource.carts.lineitems.integration.LineItemLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.sellingchannel.ProductNotPurchasableException;
import com.elasticpath.sellingchannel.ProductUnavailableException;

/**
 * Test the two methods addToCart in LineItemWriterStrategyImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemWriterStrategyImplAddToCartTest {
	private static final String CART_ID = "cartId";
	private static final String ITEM_ID = "itemId";
	private static final int QUANTITY = 1;
	private static final String STORE_CODE = "storeCode";
	private static final String PRODUCT_SKU_CODE = "productSkuCode";
	private static final String SHOPPING_ITEM_SKU_CODE = "shoppingItemSkuCode";
	private static final String CART_GUID = "cartGui";
	private static final int SHOPPING_ITEM_DTO_QUANTITY = 3;
	private static final int SHOPPING_ITEM_QUANTITY = 4;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private StoreProductRepository storeProductRepository;
	@Mock
	private LineItemTransformer lineItemTransformer;
	@Mock
	private LineItemLookupStrategy lineItemLookupStrategy;
	@InjectMocks
	private LineItemWriterStrategyImpl lineItemWriterStrategy;

	@Mock
	private ShoppingCart shoppingCart;
	@Mock
	private ShoppingCart updatedShoppingCart;
	@Mock
	private ProductSku productSku;
	@Mock
	private ProductSkuRepository productSkuRepository;
	@Mock
	private Store store;
	@Mock
	private Product product;
	@Mock
	private StoreProduct storeProduct;
	@Mock
	private ShoppingItemDto shoppingItemDto;
	@Mock
	private ShoppingItem shoppingItem;

	private LineItemEntity addToCartLineItemEntity;

	@Before
	public void setUpCommonMockComponents() {
		//Set up mock cart
		given(shoppingCart.getStore()).willReturn(store);
		given(shoppingCart.getGuid()).willReturn(CART_GUID);
		given(store.getCode()).willReturn(STORE_CODE);

		//Set up product sku and store product
		given(productSku.getProduct()).willReturn(product);
		given(productSku.getSkuCode()).willReturn(PRODUCT_SKU_CODE);
		given(storeProduct.getSkuAvailability(PRODUCT_SKU_CODE)).willReturn(Availability.AVAILABLE);

		//set up shopping item dto
		given(shoppingItemDto.getSkuCode()).willReturn(SHOPPING_ITEM_SKU_CODE);
		given(shoppingItemDto.getQuantity()).willReturn(SHOPPING_ITEM_DTO_QUANTITY);

		//Set up shopping item
		String skuGuid = "skuGuid";
		given(shoppingItem.getSkuGuid()).willReturn(skuGuid);
		given(productSkuRepository.getProductSkuWithAttributesByGuid(skuGuid)).willReturn(ExecutionResultFactory.createReadOK(productSku));
		given(shoppingItem.getQuantity()).willReturn(SHOPPING_ITEM_QUANTITY);

		//Set up updated cart with new shopping item
		given(updatedShoppingCart.getCartItem(PRODUCT_SKU_CODE)).willReturn(shoppingItem);

		//Set up line item entity that is passed into add method
		addToCartLineItemEntity = LineItemEntity.builder()
				.withCartId(CART_ID)
				.withItemId(ITEM_ID)
				.withQuantity(QUANTITY)
				.build();
	}

	@Test
	public void ensureLineItemCanBeAddedToCartSuccessfully() {

		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(productSku));

		given(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).willReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		given(lineItemLookupStrategy.isItemPurchasable(STORE_CODE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		final Map<String, String> fields = Collections.emptyMap();
		given(shoppingCartRepository.addItemToCart(shoppingCart, PRODUCT_SKU_CODE, QUANTITY, fields))
				.willReturn(ExecutionResultFactory.createReadOK(updatedShoppingCart));

		given(itemRepository.getItemIdForSku(productSku)).willReturn(ExecutionResultFactory.createReadOK(ITEM_ID));
		given(updatedShoppingCart.getCartItems(PRODUCT_SKU_CODE)).willReturn(ImmutableList.of(shoppingItem));

		LineItem cartLineItem = ResourceTypeFactory.createResourceEntity(LineItem.class)
				.setShoppingItem(shoppingItem)
				.setCartId(CART_GUID)
				.setItemId(ITEM_ID);

		LineItemEntity createdLineItemEntity = mock(LineItemEntity.class);
		given(lineItemTransformer.transformToEntity(cartLineItem)).willReturn(createdLineItemEntity);

		ExecutionResult<LineItemEntity> result = lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);

		assertExecutionResult(result).data(createdLineItemEntity);
	}

	@Test
	public void ensureAddToCartReturnsNotFoundWhenCartNotFound() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.<ShoppingCart>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);
	}

	@Test
	public void ensureAddToCartReturnsNotFoundWhenItemNotFound() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.<ProductSku>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);
	}

	@Test
	public void ensureAddToCartReturnsStateFailureWhenItemIsNotAvailable() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(productSku));

		given(storeProduct.getSkuAvailability(PRODUCT_SKU_CODE)).willReturn(Availability.NOT_AVAILABLE);

		given(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).willReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);
	}

	@Test
	public void ensureAddToCartReturnsForbiddenWhenItemIsNotPurchasable() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(productSku));

		given(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).willReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		given(lineItemLookupStrategy.isItemPurchasable(STORE_CODE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(false));
		thrown.expect(containsResourceStatus(ResourceStatus.FORBIDDEN));

		lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);
	}

	@Test
	public void ensureAddToCartReturnsStateFailureWhenShoppingItemDtoValidationFails() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(productSku));

		given(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).willReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		given(lineItemLookupStrategy.isItemPurchasable(STORE_CODE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		final Map<String, String> fields = Collections.emptyMap();
		given(shoppingCartRepository.addItemToCart(shoppingCart, PRODUCT_SKU_CODE, QUANTITY, fields))
				.willReturn(ExecutionResultFactory.<ShoppingCart>createStateFailure(""));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);
	}

	@Test
	public void ensureAddToCartReturnsStateFailureWhenProductIsNotPurchasable() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(productSku));

		given(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).willReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		given(lineItemLookupStrategy.isItemPurchasable(STORE_CODE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		final Map<String, String> fields = Collections.emptyMap();
		given(shoppingCartRepository.addItemToCart(shoppingCart, PRODUCT_SKU_CODE, QUANTITY, fields))
				.willThrow(new ProductNotPurchasableException(""));
		thrown.expect(containsResourceStatus(ResourceStatus.STATE_FAILURE));

		lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);
	}

	@Test
	public void ensureAddToCartReturnsNotFoundWhenBundleConfigurationIsInvalid() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(productSku));

		given(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).willReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		given(lineItemLookupStrategy.isItemPurchasable(STORE_CODE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		final Map<String, String> fields = Collections.emptyMap();
		given(shoppingCartRepository.addItemToCart(shoppingCart, PRODUCT_SKU_CODE, QUANTITY, fields))
				.willThrow(new InvalidBundleTreeStructureException(""));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);
	}

	@Test
	public void ensureAddToCartReturnsNotFoundWhenProductIsUnavailable() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(productSku));

		given(storeProductRepository.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).willReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		given(lineItemLookupStrategy.isItemPurchasable(STORE_CODE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		final Map<String, String> fields = Collections.emptyMap();
		given(shoppingCartRepository.addItemToCart(shoppingCart, PRODUCT_SKU_CODE, QUANTITY, fields))
				.willThrow(new ProductUnavailableException(
								"debug message",
								asList(
										new StructuredErrorMessage(
												"message-id",
												"debug message",
												ImmutableMap.of("key", "value")
										)
								)
						)
				);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);

	}

	@Test
	public void ensureReadOkIsReturnedIfShoppingItemToAddAlreadyExists() {
		given(shoppingCartRepository.getShoppingCart(CART_ID)).willReturn(ExecutionResultFactory.createReadOK(shoppingCart));
		given(itemRepository.getSkuForItemId(ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(productSku));

		given(storeProductRepository
					.findDisplayableStoreProductWithAttributesByProductGuid(STORE_CODE, product.getGuid())).willReturn(
				ExecutionResultFactory.createReadOK(storeProduct)
		);
		given(lineItemLookupStrategy.isItemPurchasable(STORE_CODE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(true));

		final Map<String, String> fields = Collections.emptyMap();
		given(shoppingCartRepository.addItemToCart(shoppingCart, PRODUCT_SKU_CODE, QUANTITY, fields))
				.willReturn(ExecutionResultFactory.createReadOK(updatedShoppingCart));

		//Set up shopping item to return same quantity so create doesn't actually result in a change to the line item.
		given(shoppingItem.getQuantity()).willReturn(SHOPPING_ITEM_QUANTITY);
		given(itemRepository.getItemIdForSku(productSku)).willReturn(ExecutionResultFactory.createReadOK(ITEM_ID));

		given(updatedShoppingCart.getCartItems(PRODUCT_SKU_CODE)).willReturn(ImmutableList.of(shoppingItem));

		LineItem cartLineItem = ResourceTypeFactory.createResourceEntity(LineItem.class)
				.setShoppingItem(shoppingItem)
				.setCartId(CART_GUID)
				.setItemId(ITEM_ID);

		LineItemEntity createdLineItemEntity = mock(LineItemEntity.class);
		given(lineItemTransformer.transformToEntity(cartLineItem)).willReturn(createdLineItemEntity);

		ExecutionResult<LineItemEntity> result = lineItemWriterStrategy.addToCart(STORE_CODE, addToCartLineItemEntity);

		assertExecutionResult(result).data(createdLineItemEntity)
				.resourceStatus(ResourceStatus.READ_OK);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.availabilities.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.availabilities.AvailabilityEntity;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.availabilities.integration.epcommerce.transform.StoreProductAvailabilityTransformer;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.domain.wrapper.LineItem;
import com.elasticpath.rest.resource.carts.integration.epcommerce.lineitems.transform.LineItemTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;

/**
 * Tests the {@link AvailabilityLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AvailabilityLookupStrategyImplTest {

	private static final String STORECODE = "storeCode1";
	private static final String USERID = "userid";
	private static final Locale LOCALE = Locale.CANADA;
	private static final String ITEM_ID = "item id";
	private static final String SKUCODE = "sku";
	private static final String CART_GUID = "cart guid";
	private static final String SKU_GUID = "sku guid";

	private final AvailabilityEntity itemAvailabilityDto = AvailabilityEntity.builder().build();

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ProductSku mockProductSku;
	@Mock
	private Product mockProduct;
	@Mock
	private ResourceOperationContext mockResourceOperationContext;
	@Mock
	private ItemRepository mockItemRepository;
	@Mock
	private StoreProductRepository mockStoreProductRepository;
	@Mock
	private StoreProductAvailabilityTransformer mockStoreProductAvailabilityTransformer;
	@Mock
	private ShoppingCartRepository mockShoppingCartRepository;
	@Mock
	private LineItemTransformer lineItemTransformer;

	@InjectMocks
	private AvailabilityLookupStrategyImpl availabilitiesLookupStrategy;

	@Mock
	private ShoppingCart mockShoppingCart;
	@Mock
	private ShoppingItem mockShoppingItem;

	@Mock (answer = Answers.RETURNS_DEEP_STUBS)
	private StoreProduct mockStoreProduct;

	@Before
	public void setUp() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORECODE, USERID, LOCALE);
		when(mockResourceOperationContext.getSubject()).thenReturn(subject);
	}

	/**
	 * Test getting availability for item with valid scope, itemId and subject.
	 */
	@Test
	public void testGetAvailability() {
		mockGetProductSku(ExecutionResultFactory.createReadOK(mockProductSku));
		mockSetUpProduct();
		mockFindStoreProduct(ExecutionResultFactory.createReadOK(mockStoreProduct));
		mockStoreProductTransformer();

		ExecutionResult<AvailabilityEntity> result = availabilitiesLookupStrategy.getAvailability(STORECODE, ITEM_ID);
		AvailabilityEntity resultItemAvailabilityDto = result.getData();

		assertTrue("Operation should be successful.", result.isSuccessful());
		assertEquals("DTO returned does not match expected DTO.", itemAvailabilityDto, resultItemAvailabilityDto);

	}

	/**
	 * Test get availability when product sku not found.
	 */
	@Test
	public void testGetAvailabilityWhenProductSkuNotFound() {
		ExecutionResult<StoreProduct> lookupResult = ExecutionResultFactory.createNotFound();
		mockGetProductSku(ExecutionResultFactory.createReadOK(mockProductSku));
		mockSetUpProduct();
		mockFindStoreProduct(lookupResult);

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		availabilitiesLookupStrategy.getAvailability(STORECODE, ITEM_ID);
	}

	/**
	 * Test get availability when item not found.
	 */
	@Test
	public void testGetAvailabilityWhenItemNotFound() {
		ExecutionResult<ProductSku> lookupResult = ExecutionResultFactory.createNotFound("item not found");

		mockGetProductSku(lookupResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		availabilitiesLookupStrategy.getAvailability(STORECODE, ITEM_ID);
	}

	/**
	 * Test whether needInfo {@link com.elasticpath.rest.definition.carts.LineItemEntity} instances are created
	 * when cart contains unavailable items.
	 */
	@Test
	public void testShouldReturnNonEmptyListWhenCartHasUnavailableItems() {

		final LineItemEntity lineItemEntity = Mockito.mock(LineItemEntity.class);

		when(mockShoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(ExecutionResultFactory.createReadOK(mockShoppingCart));
		when(mockShoppingCart.getCartItems()).thenReturn(Arrays.asList(mockShoppingItem));
		when(mockShoppingItem.getSkuGuid()).thenReturn(SKU_GUID);
		when(mockStoreProductRepository.findDisplayableStoreProductWithAttributesBySkuGuid(STORECODE, SKU_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockStoreProduct));

		when(mockStoreProduct.getSkuByGuid(SKU_GUID).getSkuCode()).thenReturn(SKUCODE);
		when(mockStoreProduct.isSkuAvailable(SKUCODE)).thenReturn(false);
		when(lineItemTransformer.transformToEntity(any(LineItem.class))).thenReturn(lineItemEntity);

		final Collection<LineItemEntity> actualCollection = availabilitiesLookupStrategy.getUnavailableLineItems(STORECODE, CART_GUID).getData();

		assertEquals("There must be one unavailable line item entity in actual collection", 1, actualCollection.size());
	}

	/**
	 * When cart has only available items, the needInfo {@link com.elasticpath.rest.definition.carts.LineItemEntity} instances
	 * should not be created.
	 *
	 */
	@Test
	public void testShouldReturnEmptyListWhenCartDoesNotHaveUnavailableItems() {

		when(mockShoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(ExecutionResultFactory.createReadOK(mockShoppingCart));
		when(mockShoppingCart.getCartItems()).thenReturn(Arrays.asList(mockShoppingItem));
		when(mockShoppingItem.getSkuGuid()).thenReturn(SKU_GUID);
		when(mockStoreProductRepository.findDisplayableStoreProductWithAttributesBySkuGuid(STORECODE, SKU_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockStoreProduct));

		when(mockStoreProduct.getSkuByGuid(SKU_GUID).getSkuCode()).thenReturn(SKUCODE);
		when(mockStoreProduct.isSkuAvailable(SKUCODE)).thenReturn(true);

		final Collection<LineItemEntity> actualCollection = availabilitiesLookupStrategy.getUnavailableLineItems(STORECODE, CART_GUID).getData();

		assertTrue("The collection must be empty", actualCollection.isEmpty());
	}

	private void mockGetProductSku(final ExecutionResult<ProductSku> executionResult) {
		when(mockItemRepository.getSkuForItemId(ITEM_ID)).thenReturn(executionResult);
	}

	private void mockSetUpProduct() {
		when(mockProductSku.getProduct()).thenReturn(mockProduct);
	}

	private void mockFindStoreProduct(final ExecutionResult<StoreProduct> executionResult) {
		when(mockStoreProductRepository
					 .findDisplayableStoreProductWithAttributesByProductGuid(STORECODE, mockProduct.getGuid())).thenReturn(executionResult);
	}

	private void mockStoreProductTransformer() {
		when(mockStoreProductAvailabilityTransformer.transformToEntity(new Pair(mockStoreProduct, mockProductSku), LOCALE))
				.thenReturn(itemAvailabilityDto);
	}
}

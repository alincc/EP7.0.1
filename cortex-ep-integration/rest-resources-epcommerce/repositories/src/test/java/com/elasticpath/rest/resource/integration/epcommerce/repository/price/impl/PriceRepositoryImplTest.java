/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;

@RunWith(MockitoJUnitRunner.class)
public class PriceRepositoryImplTest {

	private static final String ITEM_ID = "test itemId";
	private static final String STORE_CODE = "test store code";
	private static final String RESULT_SHOULD_BE_SUCCESSFUL = "The result should be successful";
	private static final String SKU_CODE = "sku code";

	@Mock
	private Price price;
	@Mock
	private StoreRepository mockStoreRepository;
	@Mock
	private PriceLookupFacade mockPriceLookupFacade;
	@Mock
	private ShoppingItemDtoFactory mockShoppingItemDtoFactory;
	@Mock
	private ShoppingItemDto mockShoppingItemDto;
	@Mock
	private ItemRepository mockItemRepository;
	@Mock
	private CustomerSessionRepository mockCustomerSessionRepository;
	@Mock
	private CustomerSession mockCustomerSession;
	@Mock
	private Shopper mockShopper;
	@Mock
	private Store mockStore;

	private Product product;
	private ProductSkuImpl sku;

	@InjectMocks
	private PriceRepositoryImpl priceRepository;

	@Test
	public void ensurePriceExistsReturnsTrue() {
		mockPriceResult(price);

		ExecutionResult<Boolean> priceExistsResult = priceRepository.priceExists(STORE_CODE, ITEM_ID);

		assertEquals(ResourceStatus.READ_OK, priceExistsResult.getResourceStatus());
		assertEquals(Boolean.TRUE, priceExistsResult.getData());
	}

	@Test
	public void ensurePriceExistsReturnsTrueWhenCached() {
		mockPriceResult(price);

		ExecutionResult<Boolean> priceExistsResult = priceRepository.priceExists(STORE_CODE, ITEM_ID);

		assertEquals(ResourceStatus.READ_OK, priceExistsResult.getResourceStatus());
		assertEquals(Boolean.TRUE, priceExistsResult.getData());
	}

	@Test
	public void ensurePriceDoesNotExistReturnsFalse() {
		mockPriceResult(null);

		ExecutionResult<Boolean> priceExistsResult = priceRepository.priceExists(STORE_CODE, ITEM_ID);

		assertEquals(ResourceStatus.READ_OK, priceExistsResult.getResourceStatus());
		assertEquals(Boolean.FALSE, priceExistsResult.getData());

	}

	@Test
	public void ensureGetPriceReturnsExpectedPrice() {
		mockPriceResult(price);

		ExecutionResult<Price> priceResult = priceRepository.getPrice(STORE_CODE, SKU_CODE);

		assertEquals(ResourceStatus.READ_OK, priceResult.getResourceStatus());
		assertEquals(price, priceResult.getData());
	}

	@Test
	public void ensureGetPriceReturnsNotFoundForNoPrice() {
		mockPriceResult(null);
		ExecutionResult<Price> result = priceRepository.getPrice(STORE_CODE, SKU_CODE);

		assertEquals(ResourceStatus.NOT_FOUND, result.getResourceStatus());
	}

	private void mockPriceResult(final Price price) {

		setupMockStore();
		setupMockShoppingItemDto(SKU_CODE, 1);
		mockItemRepositoryToReturnSku();
		mockPriceLookupFacadeToReturnPriceForSku(price);
	}

	@Test
	public void ensureGetLowestItemPriceReturnsExpectedPrice() {
		mockLowestPriceResult(price);

		ExecutionResult<Price> result = priceRepository.getLowestPrice(STORE_CODE, ITEM_ID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals(price, result.getData());
	}

	@Test
	public void ensureGetLowestItemPriceReturnsExpectedPriceWhenCached() {
		mockLowestPriceResult(price);

		ExecutionResult<Price> result = priceRepository.getLowestPrice(STORE_CODE, ITEM_ID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertEquals(price, result.getData());
	}

	@Test
	public void ensureGetLowestItemPriceRulesReturnsExpectedRules() {
		mockLowestPriceResult(price);
		final long appliedRuleId = 1L;

		final DiscountRecord discountRecord = mock(DiscountRecord.class);
		when(discountRecord.getRuleId()).thenReturn(appliedRuleId);
		when(price.getDiscountRecords()).thenReturn(Collections.singleton(discountRecord));

		ExecutionResult<Set<Long>> result = priceRepository.getLowestPriceRules(STORE_CODE, ITEM_ID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertThat(result.getData(), contains(appliedRuleId));
	}

	@Test
	public void ensureGetLowestItemPriceRulesReturnsExpectedRulesWhenCached() {
		mockLowestPriceResult(price);
		final long appliedRuleId = 1L;

		final DiscountRecord discountRecord = mock(DiscountRecord.class);
		when(discountRecord.getRuleId()).thenReturn(appliedRuleId);
		when(price.getDiscountRecords()).thenReturn(Collections.singleton(discountRecord));

		ExecutionResult<Set<Long>> result = priceRepository.getLowestPriceRules(STORE_CODE, ITEM_ID);

		assertTrue(RESULT_SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertThat(result.getData(), contains(appliedRuleId));
	}

	private void mockLowestPriceResult(final Price price) {

		setupMockStore();
		mockItemRepositoryToReturnSku();
		mockPriceLookupFacadeToReturnPromotedPriceForSku(price);

	}

	private void setupMockStore() {
		when(mockCustomerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(mockCustomerSession));
		when(mockCustomerSession.getShopper()).thenReturn(mockShopper);
		when(mockShopper.getStoreCode()).thenReturn(STORE_CODE);
		when(mockStoreRepository.findStore(STORE_CODE)).thenReturn(ExecutionResultFactory.createReadOK(mockStore));
	}

	private void setupMockShoppingItemDto(final String skuCode, final int quantity) {
		when(mockShoppingItemDtoFactory.createDto(skuCode, quantity)).thenReturn(mockShoppingItemDto);
	}

	private void mockItemRepositoryToReturnSku() {

		product = mock(Product.class);
		when(product.getGuid()).thenReturn("product guid");
		when(product.hasMultipleSkus()).thenReturn(true);

		sku = new ProductSkuImpl();
		sku.setSkuCode("sku code");
		sku.setProduct(product);

		when(mockItemRepository.getSkuCodeForItemId(ITEM_ID)).thenReturn(ExecutionResultFactory.<String>createReadOK(SKU_CODE));
		when(mockItemRepository.getSkuForItemId(ITEM_ID)).thenReturn(ExecutionResultFactory.<ProductSku>createReadOK(sku));
	}

	private void mockPriceLookupFacadeToReturnPriceForSku(final Price price) {
		when(mockPriceLookupFacade.getShoppingItemDtoPrice(mockShoppingItemDto, mockStore, mockShopper))
				.thenReturn(price);
		when(mockPriceLookupFacade.getPromotedPriceForSku(sku, mockStore, mockShopper))
				.thenReturn(price);
	}

	private void mockPriceLookupFacadeToReturnPromotedPriceForSku(final Price price) {
		when(mockPriceLookupFacade.getPromotedPriceForProduct(product, mockStore, mockShopper)).thenReturn(price);
	}

}

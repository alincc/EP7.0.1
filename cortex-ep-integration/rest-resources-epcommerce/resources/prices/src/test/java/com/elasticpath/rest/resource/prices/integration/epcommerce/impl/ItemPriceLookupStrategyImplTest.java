/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.prices.integration.epcommerce.impl;

import static java.util.Locale.CANADA;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.Currency;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Unit test for {@link ItemPriceLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemPriceLookupStrategyImplTest {

	private static final String ITEM_ID = "test itemId";
	private static final String STORE_CODE = "test store code";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private ItemRepository mockItemRepository;
	@Mock
	private PriceRepository mockPriceRepository;
	@Mock
	private MoneyTransformer moneyTransformer;

	@Mock
	private Product mockProduct;
	@Mock
	private Price price;

	@InjectMocks
	private ItemPriceLookupStrategyImpl itemPriceLookupStrategy;

	private ProductSkuImpl sku;

	@Test
	public void ensurePriceExistsReturnsTrue() {
		mockItemRepositoryToReturnSku(false);
		mockPriceRepositoryToReturnPriceExistsForSku(true);

		ExecutionResult<Boolean> priceExistsResult = itemPriceLookupStrategy.priceExists(STORE_CODE, ITEM_ID);

		assertEquals(ResourceStatus.READ_OK, priceExistsResult.getResourceStatus());
		assertTrue(priceExistsResult.getData());
	}

	@Test
	public void ensureGetItemPriceReturnsExpectedPriceEntity() {
		mockItemRepositoryToReturnSku(false);
		mockPriceRepositoryToReturnPriceForSku(price);
		ItemPriceEntity expectedDto = setupCost();

		ExecutionResult<ItemPriceEntity> priceDtoResult = itemPriceLookupStrategy.getItemPrice(STORE_CODE, ITEM_ID);

		assertEquals(ResourceStatus.READ_OK, priceDtoResult.getResourceStatus());
		assertEquals(expectedDto, priceDtoResult.getData());
	}

	@Test
	public void ensureGetItemPriceRangeReturnsExpectedCostEntity() {
		mockItemRepositoryToReturnSku(true);
		mockPriceRepositoryToReturnLowestPriceForProduct(price);
		CostEntity costEntity = CostEntity.builder()
				.build();
		when(moneyTransformer.transformToEntity(any(Money.class)))
				.thenReturn(costEntity);

		ExecutionResult<PriceRangeEntity> result = itemPriceLookupStrategy.getItemPriceRange(STORE_CODE, ITEM_ID);

		assertEquals(ResourceStatus.READ_OK, result.getResourceStatus());
		assertThat(
				result.getData().getFromPrice(),
				contains(costEntity)
		);
	}

	private void mockItemRepositoryToReturnSku(final boolean productHasMultipleSkus) {
		sku = new ProductSkuImpl();
		sku.setSkuCode("sku code");
		sku.setProduct(mockProduct);
		when(mockProduct.hasMultipleSkus()).thenReturn(productHasMultipleSkus);

		when(mockItemRepository.getSkuForItemId(ITEM_ID)).thenReturn(ExecutionResultFactory.<ProductSku>createReadOK(sku));
		when(mockItemRepository.getSkuCodeForItemId(ITEM_ID)).thenReturn(ExecutionResultFactory.createReadOK(sku.getSkuCode()));
	}

	private void mockPriceRepositoryToReturnPriceForSku(final Price price) {
		when(mockPriceRepository.getPrice(STORE_CODE, sku.getSkuCode()))
				.thenReturn(ExecutionResultFactory.createReadOK(price));
	}

	private void mockPriceRepositoryToReturnPriceExistsForSku(final boolean priceExists) {
		when(mockPriceRepository.priceExists(STORE_CODE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(priceExists));
	}

	private void mockPriceRepositoryToReturnLowestPriceForProduct(final Price price) {
		when(mockPriceRepository.getLowestPrice(STORE_CODE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(price));
	}

	private ItemPriceEntity setupCost() {

		Money lowestPrice = Money.valueOf("10", Currency.getInstance(CANADA));
		when(price.getLowestPrice())
				.thenReturn(lowestPrice);
		CostEntity purchaseEntity = CostEntity.builder()
				.build();
		when(moneyTransformer.transformToEntity(lowestPrice))
				.thenReturn(purchaseEntity);

		Money listPrice = Money.valueOf("25", Currency.getInstance(CANADA));
		when(price.getListPrice())
				.thenReturn(listPrice);
		CostEntity costEntity = CostEntity.builder()
				.build();
		when(moneyTransformer.transformToEntity(listPrice))
				.thenReturn(costEntity);

		return ItemPriceEntity.builder()
				.addingPurchasePrice(purchaseEntity)
				.addingListPrice(costEntity)
				.build();
	}

}

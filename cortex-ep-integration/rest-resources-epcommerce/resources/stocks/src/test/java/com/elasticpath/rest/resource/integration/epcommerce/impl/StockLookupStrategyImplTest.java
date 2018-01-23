/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.stocks.StockEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;

/**
 * Tests for {@link StockLookupStrategyImpl}.
 */
public class StockLookupStrategyImplTest {

	private static final String SCOPE = "testScope";
	private static final String ITEM_ID = "testItemId";
	private static final String PRODUCT_SKU_CODE = "testProductSkuCode";
	private static final int QUANTITY_IN_STOCK = 7;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private StoreProductRepository storeProductRepositoryMock;
	@Mock
	private ItemRepository itemRepositoryMock;
	@Mock
	private ProductSku productSkuMock;
	@Mock
	private Product productMock;
	@Mock
	private StoreProduct storeProductMock;
	@Mock
	private SkuInventoryDetails skuInventoryDetailsMock;
	
	private StockLookupStrategyImpl stockLookupStrategyImpl;
	
	/**
	 * Prepare common test environment elements.
	 */
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		stockLookupStrategyImpl = new StockLookupStrategyImpl(storeProductRepositoryMock, itemRepositoryMock);
	}
	
	/**
	 * Test a successful call to {@link StockLookupStrategyImpl#getStockByItemId}.
	 */
	@Test
	public void testGetStockByItemIdSuccess() {
		mockDomainAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		mockDomainQuantityInStock(QUANTITY_IN_STOCK);
		ExecutionResult<ProductSku> productSkuResult = ExecutionResultFactory.createReadOK(productSkuMock);
		mockGetSkuForItemId(productSkuResult);
		ExecutionResult<StoreProduct> storeProductResult = ExecutionResultFactory.createReadOK(storeProductMock);
		mockFindStoreProduct(storeProductResult);
		
		ExecutionResult<StockEntity> stockResult = stockLookupStrategyImpl.getStockByItemId(SCOPE, ITEM_ID);
		
		Assert.assertTrue("Lookup of stock by itemId should be successful.", stockResult.isSuccessful());
		StockEntity stock = stockResult.getData();
		Assert.assertEquals("Stock's quantity remaining should be " + QUANTITY_IN_STOCK, String.valueOf(QUANTITY_IN_STOCK), 
				stock.getQuantityRemaining());
		Assert.assertEquals("Stock's itemId should be " + ITEM_ID, ITEM_ID, stock.getItemId());
	}
	
	/**
	 * Test a failed call to {@link StockLookupStrategyImpl#getStockByItemId}, caused by a missing {@link ProductSku}.
	 */
	@Test
	public void testGetStockByItemIdWhenProductSkuNotFound() {
		ExecutionResult<ProductSku> productSkuResult = ExecutionResultFactory.createNotFound();
		mockGetSkuForItemId(productSkuResult);
		ExecutionResult<StoreProduct> storeProductResult = ExecutionResultFactory.createReadOK(storeProductMock);
		mockFindStoreProduct(storeProductResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		stockLookupStrategyImpl.getStockByItemId(SCOPE, ITEM_ID);
	}
	
	/**
	 * Test a failed call to {@link StockLookupStrategyImpl#getStockByItemId}, caused by a missing {@link StoreProduct}.
	 */
	@Test
	public void testGetStockByItemIdWhenStoreProductNotFound() {
		mockDomainAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		mockDomainQuantityInStock(QUANTITY_IN_STOCK);
		ExecutionResult<ProductSku> productSkuResult = ExecutionResultFactory.createReadOK(productSkuMock);
		mockGetSkuForItemId(productSkuResult);
		ExecutionResult<StoreProduct> storeProductResult = ExecutionResultFactory.createNotFound();
		mockFindStoreProduct(storeProductResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		stockLookupStrategyImpl.getStockByItemId(SCOPE, ITEM_ID);
	}
	
	/**
	 * Test {@link StockLookupStrategyImpl#isStockDisplayedForItem} with {@link AvailabilityCriteria#AVAILABLE_WHEN_IN_STOCK}
	 * and {@link ProductSku#isDigital()} == false, which is the only case that should return true.
	 */
	@Test
	public void testIsStockDisplayedForItemCaseTrue() {
		testIsStockDisplayedForItemSuccessCase(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, false, true);
	}
	
	/**
	 * Test {@link StockLookupStrategyImpl#isStockDisplayedForItem} with {@link AvailabilityCriteria#ALWAYS_AVAILABLE}
	 * and {@link ProductSku#isDigital()} == false, which should return false.
	 */
	@Test
	public void testIsStockDisplayedForItemCaseRefusedByAvailability() {
		testIsStockDisplayedForItemSuccessCase(AvailabilityCriteria.ALWAYS_AVAILABLE, false, false);
	}
	
	/**
	 * Test {@link StockLookupStrategyImpl#isStockDisplayedForItem} with {@link AvailabilityCriteria#AVAILABLE_WHEN_IN_STOCK}
	 * and {@link ProductSku#isDigital()} == true.
	 */
	@Test
	public void testIsStockDisplayedForItemRefusedByBeingDigital() {
		testIsStockDisplayedForItemSuccessCase(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, true, false);
	}
	
	/**
	 * Test {@link StockLookupStrategyImpl#isStockDisplayedForItem} with {@link ItemRepository#getSkuForItemId} failing.
	 */
	@Test
	public void testIsStockDisplayedForItemFailure() {
		ExecutionResult<ProductSku> productSkuResult = ExecutionResultFactory.createNotFound();
		mockGetSkuForItemId(productSkuResult);
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		stockLookupStrategyImpl.isStockDisplayedForItem(SCOPE, ITEM_ID);
	}
	
	private void testIsStockDisplayedForItemSuccessCase(final AvailabilityCriteria availabilityCriteria, final boolean isDigital, 
			final boolean expectedReturn) {
		mockDomainAvailabilityCriteria(availabilityCriteria);
		mockProductSkuIsDigital(isDigital);
		ExecutionResult<ProductSku> productSkuResult = ExecutionResultFactory.createReadOK(productSkuMock);
		mockGetSkuForItemId(productSkuResult);
		
		ExecutionResult<Boolean> displayedResult = stockLookupStrategyImpl.isStockDisplayedForItem(SCOPE, ITEM_ID);
		Assert.assertTrue("Lookup for whether stock is displayed should be successful.", displayedResult.isSuccessful());
		Boolean isDisplayed = displayedResult.getData();
		if (expectedReturn) {
			Assert.assertTrue("Return value of isDisplayed should be true.", isDisplayed);
		} else {
			Assert.assertFalse("Return value of isDisplayed should be false.", isDisplayed);
		}
	}
	
	private void mockDomainAvailabilityCriteria(final AvailabilityCriteria availabilityCriteria) {
		Mockito.when(productSkuMock.getProduct()).thenReturn(productMock);
		Mockito.when(productSkuMock.getSkuCode()).thenReturn(PRODUCT_SKU_CODE);
		Mockito.when(productMock.getAvailabilityCriteria()).thenReturn(availabilityCriteria);
	}
	
	private void mockDomainQuantityInStock(final  int quantityInStock) {
		Mockito.when(storeProductMock.getInventoryDetails(PRODUCT_SKU_CODE)).thenReturn(skuInventoryDetailsMock);
		Mockito.when(skuInventoryDetailsMock.getAvailableQuantityInStock()).thenReturn(quantityInStock);
	}
	
	private void mockGetSkuForItemId(final ExecutionResult<ProductSku> productSkuResult) {
		Mockito.when(itemRepositoryMock.getSkuForItemId(ITEM_ID)).thenReturn(productSkuResult);
	}
	
	private void mockFindStoreProduct(final ExecutionResult<StoreProduct> storeProductResult) {
		Mockito.when(storeProductRepositoryMock.findDisplayableStoreProductWithAttributesByProductGuid(SCOPE, productMock.getGuid())).
				thenReturn(storeProductResult);
	}
	
	private void mockProductSkuIsDigital(final boolean isDigital) {
		Mockito.when(productSkuMock.isDigital()).thenReturn(isDigital);
	}
	
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.product.impl;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalogview.StoreProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link StoreProductRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StoreProductRepositoryImplTest {
	private static final String PRODUCT_GUID_1 = "product_code_1";
	private static final String SCOPE = "scope";
	private static final long PRODUCT_UID_1 = 1L;
	private static final String ERROR_STORE_PRODUCT_NOT_FOUND = "Store product not found";
	private static final String ERROR_PRODUCT_NOT_FOUND = "Product not found";
	private static final String SKU_GUID = "sku guid";

	private final Product product1 = new ProductImpl();
	private final Store store = new StoreImpl();
	private final StoreProduct storeProduct = new StoreProductImpl(product1);

	@Mock
	private ProductLookup mockProductLookup;
	@Mock
	private StoreProductService mockStoreProductService;
	@Mock
	private StoreRepository mockStoreRepository;
	@Mock
	private ProductSkuRepository mockProductSkuRepository;

	@InjectMocks
	private StoreProductRepositoryImpl storeProductRepository;

	@Mock
	private ProductSku mockProductSku;

	@Test
	public void testFindStoreProduct() {
		product1.setGuid(PRODUCT_GUID_1);
		((StoreProductImpl) storeProduct).setProductDisplayable(true);

		when(mockStoreRepository.findStore(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(store));
		when(mockProductLookup.findByGuid(PRODUCT_GUID_1)).thenReturn(product1);
		when(mockStoreProductService.getProductForStore(product1, store)).thenReturn(storeProduct);

		ExecutionResult<StoreProduct> result = storeProductRepository
				.findDisplayableStoreProductWithAttributesByProductGuid(SCOPE, product1.getGuid());

		assertEquals(product1, result.getData().getWrappedProduct());
		verify(mockStoreProductService).getProductForStore(product1, store);
	}

	@Test
	public void testFindAStoreThenMissProductCacheThenMissStoreCacheThenIsDisplayable() {
		((StoreProductImpl) storeProduct).setProductDisplayable(true);

		when(mockStoreRepository.findStore(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(store));
		when(mockProductLookup.findByGuid(PRODUCT_GUID_1)).thenReturn(product1);
		when(mockStoreProductService.getProductForStore(product1, store)).thenReturn(storeProduct);

		ExecutionResult<StoreProduct> result = storeProductRepository
				.findDisplayableStoreProductWithAttributesByProductGuid(SCOPE, PRODUCT_GUID_1);

		assertEquals(product1, result.getData().getWrappedProduct());
		verify(mockStoreRepository).findStore(SCOPE);
		verify(mockProductLookup).findByGuid(PRODUCT_GUID_1);
		verify(mockStoreProductService).getProductForStore(product1, store);
	}

	@Test
	public void testFindAStoreThenMissProductCacheThenMissStoreCacheThenIsNotDisplayable() {
		((StoreProductImpl) storeProduct).setProductDisplayable(false);

		when(mockStoreRepository.findStore(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(store));
		when(mockProductLookup.findByGuid(PRODUCT_GUID_1)).thenReturn(product1);
		when(mockStoreProductService.getProductForStore(product1, store)).thenReturn(storeProduct);

		ExecutionResult<StoreProduct> result = storeProductRepository
				.findDisplayableStoreProductWithAttributesByProductGuid(SCOPE, PRODUCT_GUID_1);

		assertEquals(ResourceStatus.READ_OK, result.getResourceStatus());

		verify(mockStoreRepository).findStore(SCOPE);
		verify(mockProductLookup).findByGuid(PRODUCT_GUID_1);
		verify(mockStoreProductService).getProductForStore(product1, store);
	}

	@Test
	public void testFindStoreThenMissProductCacheThenNullStoreCache() {
		when(mockStoreRepository.findStore(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(store));
		when(mockProductLookup.findByGuid(PRODUCT_GUID_1)).thenReturn(product1);
		when(mockStoreProductService.getProductForStore(product1, store)).thenReturn(null);

		ExecutionResult<StoreProduct> result = storeProductRepository
					.findDisplayableStoreProductWithAttributesByProductGuid(SCOPE, PRODUCT_GUID_1);

		assertEquals(ERROR_STORE_PRODUCT_NOT_FOUND, result.getErrorMessage());
		verify(mockStoreRepository).findStore(SCOPE);
		verify(mockProductLookup).findByGuid(PRODUCT_GUID_1);
		verify(mockStoreProductService).getProductForStore(product1, store);
	}

	@Test
	public void testFindStoreThenNullProductCache() {
		when(mockStoreRepository.findStore(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(store));
		when(mockProductLookup.findByGuid(PRODUCT_GUID_1)).thenReturn(null);

		ExecutionResult<StoreProduct> result = storeProductRepository
					.findDisplayableStoreProductWithAttributesByProductGuid(SCOPE, PRODUCT_GUID_1);

		assertEquals(ERROR_PRODUCT_NOT_FOUND, result.getErrorMessage());
		verify(mockProductLookup).findByGuid(PRODUCT_GUID_1);
		verifyZeroInteractions(mockStoreRepository);
		verifyZeroInteractions(mockStoreProductService);
	}

	@Test
	public void testNotFindStore() {
		when(mockProductLookup.findByGuid(PRODUCT_GUID_1)).thenReturn(product1);
		when(mockStoreRepository.findStore(SCOPE)).thenReturn(ExecutionResult.<Store>builder().withResourceStatus(ResourceStatus.NOT_FOUND).build());

		ExecutionResult<StoreProduct> result = storeProductRepository
				.findDisplayableStoreProductWithAttributesByProductGuid(SCOPE, PRODUCT_GUID_1);

		assertEquals(ResourceStatus.NOT_FOUND, result.getResourceStatus());
		verify(mockProductLookup).findByGuid(PRODUCT_GUID_1);
		verify(mockStoreRepository).findStore(SCOPE);
		verifyZeroInteractions(mockStoreProductService);
	}

	@Test
	public void testFindProductByGuidWhereCacheMiss() {
		when(mockProductLookup.findByGuid(PRODUCT_GUID_1)).thenReturn(product1);

		Product result = storeProductRepository.findByGuid(PRODUCT_GUID_1);

		assertEquals(product1, result);
		verify(mockProductLookup).findByGuid(PRODUCT_GUID_1);
	}

	@Test
	public void testFindByUidsWhereAllCacheMiss() {
		List<Long> uids = new ArrayList<>();
		uids.add(PRODUCT_UID_1);

		when(mockProductLookup.findByUid(PRODUCT_UID_1)).thenReturn(product1);

		List<Product> products = storeProductRepository.findByUids(uids);

		assertEquals(product1, products.get(0));
		assertEquals(1, products.size());
		verify(mockProductLookup).findByUid(PRODUCT_UID_1);
	}

	@Test
	public void testFindBySkuGuidWhenProductSkuIsFound() {
		when(mockProductSku.getProduct()).thenReturn(product1);
		when(mockProductSkuRepository.getProductSkuWithAttributesByGuid(SKU_GUID)).thenReturn(ExecutionResultFactory.createReadOK(mockProductSku));
		when(mockStoreRepository.findStore(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(store));
		when(mockStoreProductService.getProductForStore(product1, store)).thenReturn(storeProduct);

		ExecutionResult<StoreProduct> result = storeProductRepository.findDisplayableStoreProductWithAttributesBySkuGuid(SCOPE, SKU_GUID);

		assertEquals(ResourceStatus.READ_OK, result.getResourceStatus());

		verify(mockProductSkuRepository).getProductSkuWithAttributesByGuid(SKU_GUID);
		verify(mockStoreRepository).findStore(SCOPE);
		verify(mockStoreProductService).getProductForStore(product1, store);
	}

	@Test
	public void testFindBySkuGuidWhenProductSkuIsNotFound() {
		when(mockProductSku.getProduct()).thenReturn(product1);
		when(mockProductSkuRepository.getProductSkuWithAttributesByGuid(SKU_GUID))
				.thenReturn(ExecutionResult.<ProductSku>builder().withResourceStatus(ResourceStatus.NOT_FOUND).build());
		when(mockStoreRepository.findStore(SCOPE)).thenReturn(ExecutionResultFactory.createReadOK(store));
		when(mockStoreProductService.getProductForStore(product1, store)).thenReturn(storeProduct);

		ExecutionResult<StoreProduct> result = storeProductRepository.findDisplayableStoreProductWithAttributesBySkuGuid(SCOPE, SKU_GUID);

		assertEquals(ResourceStatus.NOT_FOUND, result.getResourceStatus());

		verify(mockProductSkuRepository).getProductSkuWithAttributesByGuid(SKU_GUID);
		verifyZeroInteractions(mockStoreRepository);
		verifyZeroInteractions(mockStoreProductService);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.product.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.product.StoreProductRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.catalogview.StoreProductService;

/**
 * Repository for {@link StoreProduct}s.
 */
@Singleton
@Named("storeProductRepository")
public class StoreProductRepositoryImpl implements StoreProductRepository {
	private final StoreRepository storeRepository;
	private final ProductLookup coreProductLookup;
	private final StoreProductService coreStoreProductService;
	private final ProductSkuRepository productSkuRepository;

	/**
	 * Instantiates a new store product repository impl.
	 * @param storeRepository the store repository
	 * @param coreProductLookup the core product lookup
	 * @param coreStoreProductService the core store product service
	 * @param productSkuRepository the product sku repository
	 */
	@Inject
	StoreProductRepositoryImpl(
			@Named("storeRepository")
			final StoreRepository storeRepository,
			@Named("productLookup")
			final ProductLookup coreProductLookup,
			@Named("storeProductService")
			final StoreProductService coreStoreProductService,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository) {

		this.storeRepository = storeRepository;
		this.coreProductLookup = coreProductLookup;
		this.coreStoreProductService = coreStoreProductService;
		this.productSkuRepository = productSkuRepository;
	}

	@Override
	@CacheResult
	public ExecutionResult<StoreProduct> findDisplayableStoreProductWithAttributesByProductGuid(final String storeCode, final String productGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Product product = Assign.ifNotNull(findByGuid(productGuid), OnFailure.returnNotFound("Product not found"));
				StoreProduct storeProduct = Assign.ifSuccessful(findDisplayableStoreProductWithAttributesForProduct(storeCode, product));

				return ExecutionResultFactory.createReadOK(storeProduct);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "findDisplayableStoreProductWithAttributesBySkuGuid")
	public ExecutionResult<StoreProduct> findDisplayableStoreProductWithAttributesBySkuGuid(final String storeCode, final String skuGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku sku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(skuGuid));
				StoreProduct storeProduct = Assign.ifSuccessful(findDisplayableStoreProductWithAttributesForProduct(storeCode, sku.getProduct()));

				return ExecutionResultFactory.createReadOK(storeProduct);
			}
		}.execute();
	}

	@CacheResult
	private ExecutionResult<StoreProduct> findDisplayableStoreProductWithAttributesForProduct(final String storeCode, final Product product) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Store store = Assign.ifSuccessful(storeRepository.findStore(storeCode));
				StoreProduct storeProduct = Assign.ifNotNull(coreStoreProductService.getProductForStore(product, store),
						OnFailure.returnNotFound("Store product not found"));
				return ExecutionResultFactory.createReadOK(storeProduct);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public Product findByGuid(final String productGuid) {
		return coreProductLookup.findByGuid(productGuid);
	}

	@Override
	@CacheResult
	public List<Product> findByUids(final List<Long> productUids) {
		List<Product> productsToReturn = new ArrayList<>();

		for (Long productUid : productUids) {
			Product product = coreProductLookup.findByUid(productUid);
			productsToReturn.add(product);
		}

		return productsToReturn;
	}
}

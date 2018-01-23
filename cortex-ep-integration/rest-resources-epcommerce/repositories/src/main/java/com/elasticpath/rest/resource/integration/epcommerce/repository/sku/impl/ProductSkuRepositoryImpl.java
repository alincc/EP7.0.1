/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.sku.impl;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Product SKU repository.
 */
@Singleton
@Named("productSkuRepository")
public class ProductSkuRepositoryImpl implements ProductSkuRepository {

	private static final String NOT_FOUND_MESSAGE = "Could not find item for item ID.";

	private final ProductSkuLookup productSkuLookup;

	/**
	 * Instantiates a new product sku repository.
	 *
	 * @param productSkuLookup the product sku lookup
	 */
	@Inject
	public ProductSkuRepositoryImpl(
			@Named("productSkuLookup")
			final ProductSkuLookup productSkuLookup) {

		this.productSkuLookup = productSkuLookup;
	}

	@Override
	@CacheResult(uniqueIdentifier = "getProductSkuWithAttributesByCode")
	public ExecutionResult<ProductSku> getProductSkuWithAttributesByCode(final String skuCode) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku productSku = Assign.ifNotNull(productSkuLookup.findBySkuCode(skuCode),
						OnFailure.returnNotFound(NOT_FOUND_MESSAGE));
				return ExecutionResultFactory.createReadOK(productSku);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "getProductSkuWithAttributesByGuid")
	public ExecutionResult<ProductSku> getProductSkuWithAttributesByGuid(final String skuGuid) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku productSku = Assign.ifNotNull(productSkuLookup.findByGuid(skuGuid),
						OnFailure.returnNotFound(NOT_FOUND_MESSAGE));
				return ExecutionResultFactory.createReadOK(productSku);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Boolean> isProductBundle(final String skuGuid) {
		return new ExecutionResultChain() {
			@Override
			protected ExecutionResult<?> build() {
				ProductSku sku = Assign.ifSuccessful(getProductSkuWithAttributesByGuid(skuGuid));
				return ExecutionResultFactory.createReadOK(sku.getProduct() instanceof ProductBundle);
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "isProductSkuExist")
	public ExecutionResult<Boolean> isProductSkuExist(final String encodedItemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				String skuCode = Assign.ifSuccessful(getField(encodedItemId));
				Boolean isProductSkuExist = productSkuLookup.isProductSkuExist(skuCode);
				return ExecutionResultFactory.createReadOK(isProductSkuExist);
			}
		}.execute();
	}

	private static ExecutionResult<String> getField(final String itemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Map<String, String> compositeItemIdFields = Assign.ifNotNull(CompositeIdUtil.decodeCompositeId(itemId),
					OnFailure.returnNotFound("Item not found"));

				return ExecutionResultFactory.createReadOK(compositeItemIdFields.get("S"));
			}
		}.execute();
	}
}

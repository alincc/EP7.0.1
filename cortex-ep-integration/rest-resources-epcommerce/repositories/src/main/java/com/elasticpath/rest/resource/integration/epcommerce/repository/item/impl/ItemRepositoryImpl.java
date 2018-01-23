/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item.impl;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;


import com.google.common.collect.ImmutableSortedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Repository that consolidates access to item domain related concepts such as product, sku, and item configuration.
 */
@Singleton
@Named("itemRepository")
public class ItemRepositoryImpl implements ItemRepository {
	private static final Logger LOG = LoggerFactory.getLogger(ItemRepositoryImpl.class);
	private static final String ITEM_NOT_FOUND_MESSAGE = "Item not found";

	private final ProductSkuRepository productSkuRepository;
	private final ProductSkuLookup productSkuLookup;
	private final BundleIdentifier bundleIdentifier;


	/**
	 * Creates instance with needed services wired in.
	 *
	 * @param productSkuRepository product sku repository
	 * @param productSkuLookup     product sku lookup
	 * @param bundleIdentifier     class for evaluating bundle information about a product
	 */
	@Inject
	public ItemRepositoryImpl(
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository,
			@Named("productSkuLookup")
			final ProductSkuLookup productSkuLookup,
			@Named("bundleIdentifier")
			final BundleIdentifier bundleIdentifier) {

		this.productSkuRepository = productSkuRepository;
		this.productSkuLookup = productSkuLookup;
		this.bundleIdentifier = bundleIdentifier;
	}


	@Override
	@CacheResult
	public ExecutionResult<String> getDefaultItemIdForProduct(final Product product) {
		assert product != null : "Product cannot be null.";

		ProductSku defaultSku = product.getDefaultSku();
		assert defaultSku != null : "Product must have a default sku.";

		return getItemIdForSku(defaultSku);
	}

	@Override
	public ExecutionResult<String> getItemIdForSku(final ProductSku productSku) {
		assert productSku != null : "Product sku cannot be null.";
		String skuCode = productSku.getSkuCode();
		String itemId = CompositeIdUtil.encodeCompositeId(createItemIdMap(skuCode));
		return ExecutionResultFactory.createReadOK(itemId);
	}

	@Override
	@CacheResult
	public IdentifierPart<Map<String, String>> getItemIdForProductSku(final ProductSku productSku) {
		String skuCode = productSku.getSkuCode();
		return CompositeIdentifier.of(createItemIdMap(skuCode));
	}

	@Override
	@CacheResult
	public ProductBundle asProductBundle(final Product product) {
		assert product != null : "product should not be null.";
		return bundleIdentifier.asProductBundle(product);
	}

	private Map<String, String> createItemIdMap(final String skuCode) {
		return ImmutableSortedMap.of(SKU_CODE_KEY, skuCode);
	}

	@Override
	@CacheResult(uniqueIdentifier = "getSkuForItemId")
	public ExecutionResult<ProductSku> getSkuForItemId(final String itemId) {
		assert itemId != null : "itemId should never be null.";
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				String skuCode = Assign.ifSuccessful(getSkuCodeForItemId(itemId));
				ProductSku productSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByCode(skuCode),
						OnFailure.returnNotFound(ITEM_NOT_FOUND_MESSAGE));

				return ExecutionResultFactory.createReadOK(productSku);
			}

		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<String> getSkuCodeForItemId(final String itemId) {
		return getField(itemId, SKU_CODE_KEY);
	}

	private static ExecutionResult<String> getField(final String itemId, final String skuCodeFieldName) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				Map<String, String> compositeItemIdFields = Assign.ifNotNull(CompositeIdUtil.decodeCompositeId(itemId),
						OnFailure.returnNotFound(ITEM_NOT_FOUND_MESSAGE));

				return ExecutionResultFactory.createReadOK(compositeItemIdFields.get(skuCodeFieldName));
			}
		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Boolean> isItemBundle(final String itemId) {
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku productSku = Assign.ifSuccessful(getSkuForItemId(itemId));
				Product product = productSku.getProduct();
				assert product != null : "Product should not be null";

				return ExecutionResultFactory.createReadOK(bundleIdentifier.isBundle(product));
			}
		}.execute();
	}

	@Override
	@CacheResult(uniqueIdentifier = "getSkuForSkuGuid")
	public ExecutionResult<ProductSku> getSkuForSkuGuid(final String skuGuid) {
		assert skuGuid != null : "skuGuid should never be null.";
		return new ExecutionResultChain() {
			public ExecutionResult<?> build() {
				ProductSku productSku = Assign.ifSuccessful(productSkuRepository.getProductSkuWithAttributesByGuid(skuGuid),
						OnFailure.returnNotFound("Sku not found"));

				return ExecutionResultFactory.createReadOK(productSku);
			}

		}.execute();
	}

	@Override
	@CacheResult
	public ExecutionResult<Set<SkuOption>> getSkuOptionsForItemId(final String itemId) {
		ExecutionResult<String> skuCodeResult = getSkuCodeForItemId(itemId);
		if (skuCodeResult.isFailure()) {
			return ExecutionResultFactory.createNotFound(skuCodeResult.getErrorMessage());
		}

		ExecutionResult<Set<SkuOption>> result;
		final String skuCode = skuCodeResult.getData();
		try {
			ProductSku productSku = productSkuLookup.findBySkuCode(skuCode);
			if (productSku == null) {
				return ExecutionResultFactory.createNotFound("Sku could not be found for code " + skuCode);
			}

			result = ExecutionResultFactory.createReadOK(productSku.getProduct().getProductType().getSkuOptions());
		} catch (EpSystemException exception) {
			LOG.debug("Exception from productSkuLookup({})", skuCode, exception);
			result = ExecutionResultFactory.createServerError("Error while loading sku options");
		}

		return result;
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.item;

import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Repository for working with the Item domain.
 */
public interface ItemRepository {

	/**
	 * Composite ID skuCode Key.
	 */
	String SKU_CODE_KEY = "S";

	/**
	 * Returns the id for an item based on the product's default sku.
	 * The product must have a default sku.
	 *
	 * @param product the product with the sku to build an item id from
	 * @return the item id for the product's default sku
	 */
	ExecutionResult<String> getDefaultItemIdForProduct(Product product);

	/**
	 * Returns the id for an item based on the sku.
	 *
	 * @param productSku product sku to encode the id from
	 * @return an id that uniquely identifies the purchasable item
	 */
	ExecutionResult<String> getItemIdForSku(ProductSku productSku);

	/**
	 * Returns the identifier for an item based on the sku.
	 *
	 * @param productSku product sku to encode the id from
	 * @return an id that uniquely identifies the purchasable item
	 */
	IdentifierPart<Map<String, String>> getItemIdForProductSku(ProductSku productSku);

	/**
	 * Returns the product sku identified by the given itemId.
	 *
	 * @param itemId the id to search with
	 * @return the product sku identified by the itemId
	 */
	ExecutionResult<ProductSku> getSkuForItemId(String itemId);

	/**
	 * Returns the product sku code identified by the given itemId.
	 *
	 * @param itemId the id to search with
	 * @return the product sku code identified by the itemId
	 */
	ExecutionResult<String> getSkuCodeForItemId(String itemId);

	/**
	 * Checks if item is bundle.
	 *
	 * @param itemId the item id
	 * @return true if the item is a bundle, false otherwise
	 */
	ExecutionResult<Boolean> isItemBundle(String itemId);

	/**
	 * Returns the product sku identified by the given sku guid.
	 *
	 * @param skuGuid the sku guid
	 * @return the product sku identified by the guid
	 */
	ExecutionResult<ProductSku> getSkuForSkuGuid(String skuGuid);

	/**
	 * Returns a ProductBundle based on the product.
	 * @param product a product
	 * @return a product bundle
	 */
	ProductBundle asProductBundle(Product product);

	/**
	 * Returns the Set of SkuOptions for the given itemId.
	 * @param itemId the item id.
	 * @return Set of Sku Options
	 */
	ExecutionResult<Set<SkuOption>> getSkuOptionsForItemId(String itemId);
}

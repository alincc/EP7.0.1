/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.sku;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Product SKU repository.
 */
public interface ProductSkuRepository {

	/**
	 * Gets the SKU with attributes based on the given sku code.
	 *
	 * @param skuCode the sku Code.
	 * @return the product sku.
	 */
	ExecutionResult<ProductSku> getProductSkuWithAttributesByCode(String skuCode);

	/**
	 * Gets the product sku with attributes by the sku guid.
	 *
	 * @param skuGuid the sku guid
	 * @return the product sku with attributes by guid
	 */
	ExecutionResult<ProductSku> getProductSkuWithAttributesByGuid(String skuGuid);

	/**
	 * Determines if a product associated to a product sku (identified by the skuGuid) is a product bundle.
	 *
	 * @param skuGuid product sku guid
	 * @return product is a product bundle
	 */
	ExecutionResult<Boolean> isProductBundle(String skuGuid);

	/**
	 * Verifies whether SKU exists for given item id.
	 *
	 * @param encodedItemId encoded item id
	 * @return true if product sku exists
	 */
	ExecutionResult<Boolean> isProductSkuExist(String encodedItemId);
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration.epcommerce.domain.wrapper;

import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.money.Money;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Wrapper for taxes consisting of a {@link TaxCategory} and its associated value {@link Money}.
 */
public interface TaxEntryWrapper extends ResourceEntity {

	/**
	 * Gets the tax category.
	 *
	 * @return the tax category
	 */
	TaxCategory getTaxCategory();

	/**
	 * Gets the tax value.
	 *
	 * @return the tax value
	 */
	Money getTaxValue();

	/**
	 * Sets the tax category.
	 *
	 * @param taxCategory the tax category
	 * @return the tax wrapper
	 */
	TaxEntryWrapper setTaxCategory(TaxCategory taxCategory);

	/**
	 * Sets the tax value.
	 *
	 * @param money the money
	 * @return the tax wrapper
	 */
	TaxEntryWrapper setTaxValue(Money money);
}

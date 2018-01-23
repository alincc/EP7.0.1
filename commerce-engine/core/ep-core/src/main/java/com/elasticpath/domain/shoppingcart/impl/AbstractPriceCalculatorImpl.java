/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.math.BigDecimal;

import com.elasticpath.domain.shoppingcart.PriceCalculator;

/**
 * Abstract class for handling common portions of PriceCalculator classes.
 */
public abstract class AbstractPriceCalculatorImpl implements PriceCalculator {

	private boolean includeCartDiscounts;
	private boolean unitPriceOnly;

	@Override
	public PriceCalculator withCartDiscounts() {
		includeCartDiscounts = true;
		return this;
	}

	@Override
	public PriceCalculator forUnitPrice() {
		unitPriceOnly = true;
		return this;
	}

	@Override
	public abstract BigDecimal getAmount();

	protected boolean isIncludeCartDiscounts() {
		return includeCartDiscounts;
	}

	protected boolean isUnitPriceOnly() {
		return unitPriceOnly;
	}
}


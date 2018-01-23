/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.impl;


import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * Test cases for {@link com.elasticpath.plugin.tax.rate.impl.TaxExclusiveRateApplier}.
 */
public class TaxExclusiveRateApplierTest {

	private static final BigDecimal ENV_TAX_RATE = new BigDecimal("0.05");
	private static final BigDecimal TEN = BigDecimal.TEN.setScale(2);
	
	private final TaxExclusiveRateApplier applier = new TaxExclusiveRateApplier();
	
	/**
	 * Simple test of the calculate tax method of DefaultTaxCalculationService.
	 */
	@Test
	public void testCalculateTaxSimple() {
		final BigDecimal inputBigDecimal = TEN;
		final BigDecimal expectedTax = new BigDecimal("0.50");
		BigDecimal calculatedTax = applier.calculateTax(inputBigDecimal, ENV_TAX_RATE);
		assertEquals(0, expectedTax.compareTo(calculatedTax));
	}

	/**
	 * Simple test of the calculate tax method of DefaultTaxCalculationService with zero amount.
	 */
	@Test
	public void testCalculateTaxZeroAmount() {
		final BigDecimal inputBigDecimal = BigDecimal.ZERO;

		final BigDecimal expectedInclusiveTax = BigDecimal.ZERO;
		final BigDecimal calculatedTax = applier.calculateTax(inputBigDecimal, ENV_TAX_RATE);
		assertEquals(0, expectedInclusiveTax.compareTo(calculatedTax));
	}

	/**
	 * Simple test of the calculate tax method of DefaultTaxCalculationService with zero amount.
	 */
	@Test
	public void testCalculateTaxZeroRate() {
		final BigDecimal inputBigDecimal = TEN;

		final BigDecimal expectedInclusiveTax = BigDecimal.ZERO;
		final BigDecimal calculatedTax = applier.calculateTax(inputBigDecimal, BigDecimal.ZERO);
		assertEquals(0, expectedInclusiveTax.compareTo(calculatedTax));
	}


}

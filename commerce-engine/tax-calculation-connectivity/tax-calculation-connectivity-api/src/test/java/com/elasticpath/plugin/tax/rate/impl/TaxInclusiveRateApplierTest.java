/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.rate.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.elasticpath.plugin.tax.common.TaxCalculationConstants;

/**
 * Test cases for tax inclusive calculation.
 */
@RunWith(Parameterized.class)
public class TaxInclusiveRateApplierTest {

	private final TaxInclusiveRateApplier applier = new TaxInclusiveRateApplier();

	@Parameters
	public static Collection<String[]> getTestValues() {
		return Arrays.asList(new String[][] { 
				{ "0.20", "0.20", "100", "16.67" }, 
				{ "0.20", "0.10", "100", "8.33" }
			});
	}

	private final String totalTax;
	private final String taxValue;
	private final String price;
	private final String expected;

	/**
	 * @param totalTax total tax
	 * @param taxValue tax value
	 * @param price price
	 * @param expected expected value
	 */
	public TaxInclusiveRateApplierTest(final String totalTax, final String taxValue, final String price, final String expected) {
		super();
		this.totalTax = totalTax;
		this.taxValue = taxValue;
		this.price = price;
		this.expected = expected;
	}

	/**
	 * Test case for tax inclusive calculation.
	 */
	@Test
	public void testInclusiveTax() {
		assertEquals(
				new BigDecimal(expected),
				applier.getTaxIncludedInPrice(new BigDecimal(totalTax), new BigDecimal(taxValue), new BigDecimal(price))
					.setScale(TaxCalculationConstants.DEFAULT_DECIMAL_SCALE, TaxCalculationConstants.DEFAULT_ROUNDING_MODE));
	}

}

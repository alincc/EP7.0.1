/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersummary.services.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for {@code OrderSummaryReportServiceImpl}.
 */
public class OrderSummaryReportServiceImplTest {

	/**
	 * Tests that the groupByDayCurrency correctly iterates through multiple currencies.
	 */
	@Test
	public void testGroupByDayCurrencyMultiCurrency() {
		OrderSummaryReportServiceImpl service = new OrderSummaryReportServiceImpl();

		List<Object[]> orders = new ArrayList<Object[]>();
		Object[] order1 = {new Date(), Currency.getInstance("USD")}; //$NON-NLS-1$
		Object[] order2 = {new Date(), Currency.getInstance("CAD")}; //$NON-NLS-1$
		orders.add(order1);
		orders.add(order2);

		service.setCurrency(Currency.getInstance("CAD")); //$NON-NLS-1$

		List<List<Object[]>> result = service.groupByDayCurrency(orders);

		assertEquals("One group in the result", 1, result.size()); //$NON-NLS-1$
		List<Object[]> innerList = result.get(0);
		assertEquals("One group in the inner list", 1, innerList.size()); //$NON-NLS-1$
		Object[] resultArray = innerList.get(0);
		assertEquals("The currency should be CAD", Currency.getInstance("CAD"), resultArray[1]); //$NON-NLS-1$ //$NON-NLS-2$
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersummary.services.impl; 

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.ordersummary.OrderSummaryReportMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;
import com.elasticpath.service.reporting.ReportService;

/**
 * Local OrderSummaryReportService. This service is a wrapper for calling the ReportService, 
 * because BIRT's javascript engine does not handle Spring proxy beans well.
 *
 */
public class OrderSummaryReportServiceImpl { //NOPMD

	private static final int START_YEAR = 1900;
	private static final String CREATED_DATE = "o.createdDate"; //$NON-NLS-1$

	private ReportService reportService;

	/** Index of currency field to be used for filtering. **/
	private Currency currency;
	
	/**
	 * Create query for order summary.
	 * 
	 * @return query string
	 */
	private JpqlQueryBuilder getOrderSummaryQueryAndParams() { //NOPMD
		final List<ReportType> allReportTypes = ReportType.getReportTypes();
		Map<String, Object> params = new HashMap<String, Object>();
		for (ReportType reportType : allReportTypes) {
			if (reportType.getName().equalsIgnoreCase(OrderSummaryReportMessages.report)) {
				params = reportType.getReport().getParameters();
			}
		}
		
		final String selectFields = "o.createdDate, o.currency, o.total, sum(skus.quantityInternal), o.uidPk"; //$NON-NLS-1$
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);	//$NON-NLS-1$ //$NON-NLS-2$	
			
		jpqlQueryBuilder.appendInnerJoin("o.shipments", "shipments");  //$NON-NLS-1$ //$NON-NLS-2$
		jpqlQueryBuilder.appendInnerJoin("shipments.shipmentOrderSkusInternal", "skus"); //$NON-NLS-1$ //$NON-NLS-2$
		
		jpqlQueryBuilder.appendOrderBy(CREATED_DATE, false);
		
		jpqlQueryBuilder.appendGroupBy("o.uidPk"); //$NON-NLS-1$
		jpqlQueryBuilder.appendGroupBy(CREATED_DATE);
		jpqlQueryBuilder.appendGroupBy("o.currency"); //$NON-NLS-1$
		jpqlQueryBuilder.appendGroupBy("o.total"); //$NON-NLS-1$
			
		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();	
		
		for (String key : params.keySet()) {
			Object param = params.get(key);
			if ("store".equalsIgnoreCase(key)) { //$NON-NLS-1$
				jpqlQueryBuilder.appendInnerJoin("StoreImpl", "s", "o.storeCode = s.code");
				whereGroup.appendWhereEquals("s.name", param.toString()); //$NON-NLS-1$
			}
			if ("isShowExchangeOnly".equalsIgnoreCase(key)  //$NON-NLS-1$ //NOPMD
						&& param.toString().equals(OrderSummaryReportMessages.exchangeOrderOnly)) {
				jpqlQueryBuilder.appendInnerJoin("o.returns", "r"); //$NON-NLS-1$ //$NON-NLS-2$
				whereGroup.appendWhereEquals("r.returnType", OrderReturnType.EXCHANGE); //$NON-NLS-1$
			}
			if ("startdate".equalsIgnoreCase(key)) { //$NON-NLS-1$
				whereGroup.appendWhere(CREATED_DATE, ">=", param, JpqlQueryBuilderWhereGroup.JpqlMatchType.AS_IS); //$NON-NLS-1$ 
				
			}
			if ("enddate".equalsIgnoreCase(key)) { //$NON-NLS-1$
				whereGroup.appendWhere(CREATED_DATE, "<=", param, JpqlQueryBuilderWhereGroup.JpqlMatchType.AS_IS); //$NON-NLS-1$
				
			}
			if ("currency".equalsIgnoreCase(key)) { //$NON-NLS-1$
				if (param != null) { //NOPMD
					currency = Currency.getInstance((String) param);
				}
			}
			if ("orderSourceParam".equalsIgnoreCase(key) && !param.toString().equalsIgnoreCase("all")) { //$NON-NLS-1$ //$NON-NLS-2$
				if (param.toString().equalsIgnoreCase("null")) {  //$NON-NLS-1$
					whereGroup.appendWhereEmpty("o.orderSource"); //$NON-NLS-1$
				} else {
					whereGroup.appendWhereEquals("o.orderSource", param); //$NON-NLS-1$
				}
			}
			if ("checkedStatuses".equalsIgnoreCase(key)) { //$NON-NLS-1$
				final List<OrderStatus> statuses = (List<OrderStatus>) param;
				whereGroup.appendWhereInCollection("o.status", statuses); //$NON-NLS-1$		
			}
		}
		
		return jpqlQueryBuilder;
	}
	
	/**
	 * List order summary based on create date and whether the order is cancelled.
	 * A start date and/or end date must be given--this will be enforced at a higher
	 * level.

	 * @return a list of order summary's.
	 */
	public List<Object[]> orderSummaryReport() {
		
		final JpqlQueryBuilder queryBuilder = getOrderSummaryQueryAndParams();
		
		final List<Object[]> dbResults = getReportService().execute(queryBuilder.toString(), queryBuilder.getParameterList().toArray());
		
		final List<List<Object[]>> orderedResults = groupByDayCurrency(dbResults);
		
		final List<Object[]> reportResults = new ArrayList<Object[]>();

		for (int i = 0; i < orderedResults.size(); i++) {
			final Object[] reportRow = getTotals(orderedResults.get(i));
			reportResults.add(reportRow);
		}
		return reportResults;
	}
	
	private ReportService getReportService() {
		if (reportService == null) {
			reportService = LoginManager.getInstance().getBean(ContextIdNames.REPORT_SERVICE);
		}
		return reportService;
	}

	/**
	 * Groups the results by day and currency.
	 * @param orders The orders to group. 
	 * @return The grouped orders.
	 */
	protected List<List<Object[]>> groupByDayCurrency(final List<Object[]> orders) {
		Date lastOrderDate = null;
		Currency lastOrderCurrency = null;
		
		final List<List<Object[]>> groupedOrders = new ArrayList<List<Object[]>>();
		List<Object[]> currentGroup = null;

		for (final Object[] orderObject : orders) {
			
			Date date = (Date) orderObject[0];
			Currency currency = Currency.getInstance(orderObject[1].toString());
			
			if (currency != null && !currency.equals(this.currency)) {								
				continue;	//Filter out any orders that aren't in the chosen currency filter.
			}
			if (isSameDay(lastOrderDate, date)
				&& currency.equals(lastOrderCurrency)) {
				currentGroup.add(orderObject);
			} else {
				if (currentGroup != null) {
					groupedOrders.add(currentGroup);
				}
				currentGroup = new ArrayList<Object[]>();
				currentGroup.add(orderObject);
			}
			lastOrderCurrency = Currency.getInstance(orderObject[1].toString());
			lastOrderDate = (Date) orderObject[0];
		}
		if (currentGroup != null) {
			groupedOrders.add(currentGroup);
		}
		return groupedOrders;
	}
	
	private boolean isSameDay(final Date dayOne, final Date dayTwo) {
		if (dayOne == null || dayTwo == null) {
			return false;
		}

		final GregorianCalendar calOne = new GregorianCalendar();
		calOne.setTime(dayOne);
		final GregorianCalendar calTwo = new GregorianCalendar();
		calTwo.setTime(dayTwo);

		return calOne.get(Calendar.DATE) == calTwo.get(Calendar.DATE)
			&& calOne.get(Calendar.MONTH) == calTwo.get(Calendar.MONTH)
			&& calOne.get(Calendar.YEAR) == calTwo.get(Calendar.YEAR);
	}

	/**
	 * Returns a daily currency order total result.
	 * @param orders grouped list of orders.
	 * @return a daily currency order total result.
	 */
	private Object[] getTotals(final List<Object[]> orders) {
		if (orders == null || orders.isEmpty()) {
			return new Object[0];
		}
		Object[] orderRow = null;
		int quantity = 0;
		
		BigDecimal salesTotal = BigDecimal.ZERO;

		final int qty = 3;
		
		for (int i = 0; i < orders.size(); i++) {
			orderRow = orders.get(i);
			quantity += Integer.valueOf(orderRow[qty].toString());
			BigDecimal total = BigDecimal.valueOf(Double.valueOf(orderRow[2].toString()));
			salesTotal = salesTotal.add(total);
		}

		final int orderDate = 0;
		final int numOrders = 1;
		final int numItems = 2;
		final int orderTotal = 3;
		final int yearMonth = 4;
		
		final Object[] result = new Object[yearMonth + 1];

		final Date date = (Date) orderRow[0];
		String yearMonthStr = StringUtils.EMPTY;
		String dateStr = StringUtils.EMPTY;
		if (date != null) {
			yearMonthStr = (date.getYear() + START_YEAR) + " / " + (date.getMonth() + 1);  //$NON-NLS-1$
			dateStr = CorePlugin.getDefault().getDefaultDateFormatter().format(date);
		}
		result[orderDate] = dateStr;
		result[numOrders] = orders.size();
		result[numItems] = quantity;
		result[orderTotal] = salesTotal;
		result[yearMonth] = yearMonthStr;
		
		return result;
	}
	
	/**
	 * Sets the currency of this report. Included for unit testing.
	 * @param currency The currency to set.
	 */
	protected void setCurrency(final Currency currency) {
		this.currency = currency;
	}
}

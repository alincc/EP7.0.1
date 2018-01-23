/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.ordersbystatus.OrdersByStatusPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersbystatus.OrdersByStatusReportMessages;
import com.elasticpath.cmclient.reporting.ordersbystatus.impl.OrdersByStatusPreparedStatementBuilderImpl;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.service.reporting.ReportService;

/**
 * Local OrdersByStatusReportService.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "restriction", "nls" })
public class OrdersByStatusReportServiceImpl {
	
	private static final int DB_TAX_VALUE = 1;
	private static final int DB_TAX_ORDER_NUMBER = 0;
	private static final int DB_SHIPMENTS = 0;
	private static final int DB_RETURN_ORDER = 0;
	private static final int DB_ORDER_NUMBER = 1;
	private static final int DB_SHIPMENT_LIST_PRICE = 2;
	private static final int DB_SHIPMENT_AMOUNT = 3;
	private static final int DB_ORDER_STATUS = 4;
	private static final int DB_CREATED_DATE = 5;
	private static final int DB_CURRENCY = 6;
	private static final int DB_TOTAL = 7;
	private static final int DB_CUSTOMER = 8;
	private static final int DB_CREATED_BY_CSR = 9;
	private static final int DB_STORE_NAME = 10;

	private static final int DB_EXCHANGE_ORDER = 0;
	
	private ReportService reportService;
	
	/** Status Index. */
	protected static final int FLATTENED_STATUS = 0;
	/** Order Number Index. */
	protected static final int FLATTENED_ORDER_NUMBER = 1;
	/** Created Date Index. */
	protected static final int FLATTENED_CREATED_DATE = 2;
	/** Store Index. */
	protected static final int FLATTENED_STORE = 3;
	/** Currency Index. */
	protected static final int FLATTENED_CURRENCY = 4;
	/** Tax Total Index. */
	protected static final int FLATTENED_TOTAL = 5;
	/** Is A Return Index. */
	protected static final int FLATTENED_RETURN_FLAG = 6;
	/** List Price Index. */
	protected static final int FLATTENED_LIST_PRICE = 7;
	/** Purchase Price Index. */
	protected static final int FLATTENED_UNIT_PRICE = 8;
	/** Discount Index. */
	protected static final int FLATTENED_DISC_PRICE = 9;
	/** Shipping Cost Index. */
	protected static final int FLATTENED_SHIPPING_COST = 10;
	/** Tax Total Index. */
	protected static final int FLATTENED_TAX_TOTAL = 11;
	/** Exchange Order Index. */
	protected static final int FLATTENED_EXCHANGE_ORDER = 12;
	/** Customer ID Index. */
	protected static final int FLATTENED_CUSTOMER_ID = 13;
	/** Last Name Index. */
	protected static final int FLATTENED_CUSTOMER_LAST_NAME = 14;
	/** First Name Index. */
	protected static final int FLATTENED_CUSTOMER_FIRST_NAME = 15;
	/** Email Index. */
	protected static final int FLATTENED_CUSTOMER_EMAIL = 16;
	/** CSR Index. */
	protected static final int FLATTENED_CREATED_BY_CSR = 17;
	/** Row Length Index. */
	protected static final int ROW_LENGTH = 18;
	
	/**
	 * This method is called by BIRT Report and should return list of Object[]. 
	 * 
	 * Array of Objects should contain next values:
	 * <ol>
	 * <li>order status</li>
	 * <li>order number</li>
	 * <li>created date</li>
	 * <li>store</li>
	 * <li>currency</li>
	 * <li>order total</li>
	 * <li>list price</li>
	 * <li>unit price</li>
	 * <li>discount on order</li>
	 * <li>shipping costs</li>
	 * <li>tax total</li>
	 * <li>is exchange</li>
	 * <li>customer's id</li>
	 * <li>customer's last name</li>
	 * <li>customer's first name</li>
	 * <li>customer's email</li>
	 * <li>CSR created</li>
	 * </ol>
	 * NOTICE: filter by store and created date should be performed.
	 *
	 * @return a list of Object[] that contain info on each order.
	 */
	public List<Object[]> orderDataSet() {
		OrdersByStatusPreparedStatementBuilder queryBuilder = new OrdersByStatusPreparedStatementBuilderImpl();
		
		//Exchanges Information
		final JpqlQueryBuilder exchangeBuilder = queryBuilder.getExchangesInfoQueryAndParams();
		List<Object[]> exchangeOrderNumberResults = getReportService().execute(exchangeBuilder.toString(), 
				exchangeBuilder.getParameterList().toArray());
		Set<Object> exchangeOrderNumberSet = convertToExchangeOrderNumberSet(exchangeOrderNumberResults);
		
		//Order Information
		final JpqlQueryBuilder shipmentInfoBuilder = queryBuilder.getOrderInfoPerShipmentQueryAndParams();
		final JpqlQueryBuilder orderTaxesBuilder = queryBuilder.getTaxesPerOrderQueryAndParams();
		
		List<Object[]> shipmentResults = getReportService().execute(shipmentInfoBuilder.toString(), 
				shipmentInfoBuilder.getParameterList().toArray()); //each row is a shipment
		List<Object[]> orderTaxResults = getReportService().execute(orderTaxesBuilder.toString(), 
				orderTaxesBuilder.getParameterList().toArray());
		Map<String, Object[]> orderMap = mergeMainAndTaxesQuery(shipmentResults, processTaxes(orderTaxResults), false, exchangeOrderNumberSet);
		
		//Return Information
		final JpqlQueryBuilder returnInfoBuilder = queryBuilder.getReturnOrderInfoQueryAndParams();
		final JpqlQueryBuilder returnTaxesBuilder = queryBuilder.getTaxesPerReturnQueryAndParams(null);
		List<Object[]> returnOrderResults = getReportService().execute(returnInfoBuilder.toString(), 
				returnInfoBuilder.getParameterList().toArray()); //each row is a return
		List<Object[]> returnTaxResults = getReportService().execute(returnTaxesBuilder.toString(), 
				returnTaxesBuilder.getParameterList().toArray());
		Map<String, Object[]> returnMap = mergeMainAndTaxesQuery(returnOrderResults, processTaxes(returnTaxResults), true, exchangeOrderNumberSet);
		
		Map<String, Object[]> finalResultsMap = new HashMap<String, Object[]>();
		finalResultsMap.putAll(orderMap);
		finalResultsMap.putAll(returnMap);
		
		return sortMapToListResults(finalResultsMap);
	}
	
	private Map<String, Object[]> mergeMainAndTaxesQuery(final List<Object[]> queryResults, final Map<String, BigDecimal> taxMap,
			final boolean isReturn, final Set<Object> exchangeOrderNumberSet) {
		Map<String, Object[]> rowMapping = new HashMap<String, Object[]>(); 
	
		for (Object[] queryResult : queryResults) {
			String key;
			String taxKey;
			
			String orderNumber = (String) queryResult[DB_ORDER_NUMBER];
			if (isReturn) {
				OrderReturn orderReturn = (OrderReturn) queryResult[DB_RETURN_ORDER];
				key = orderNumber.concat("/" + orderReturn.getRmaCode());
				taxKey = orderReturn.getRmaCode();
			} else {
				key = orderNumber;
				taxKey = orderNumber;
			}
			
			Object[] orderRow = rowMapping.get(key);
			
			if (orderRow == null) { //create new order row
				orderRow = constructNewRowElement(taxMap, queryResult, key, taxKey, isReturn, exchangeOrderNumberSet);
			}
			
			BigDecimal unitPrice = (BigDecimal) queryResult[DB_SHIPMENT_AMOUNT];
			BigDecimal listPrice = (BigDecimal) queryResult[DB_SHIPMENT_LIST_PRICE];
			if (isReturn) {
				unitPrice = unitPrice.negate();
				listPrice = listPrice.negate();
			
				OrderReturn orderReturn = (OrderReturn) queryResult[DB_RETURN_ORDER];
				orderRow[FLATTENED_SHIPPING_COST] = orderReturn.getShippingCost().negate();
			} else {
				if (queryResult[DB_SHIPMENTS] instanceof PhysicalOrderShipmentImpl) {
					PhysicalOrderShipment physicalShipment = (PhysicalOrderShipmentImpl) queryResult[DB_SHIPMENTS];
					orderRow[FLATTENED_SHIPPING_COST] = ((BigDecimal) orderRow[FLATTENED_SHIPPING_COST]).
									add(physicalShipment.getShippingCost());
				}
			}
			
			orderRow[FLATTENED_UNIT_PRICE] = ((BigDecimal) orderRow[FLATTENED_UNIT_PRICE]).
						add(unitPrice);

			orderRow[FLATTENED_LIST_PRICE] = ((BigDecimal) orderRow[FLATTENED_LIST_PRICE]).
						add(listPrice);
			
			rowMapping.put(key, orderRow);
		}
		return rowMapping;
	}
	
	/**
	 * Extracts the value collection from a map and return an array list representation. 
	 * Returns an empty List if the map is null or empty.
	 * 
	 * Performs a sort at the end
	 * 
	 * @param mapResults The map collection to be converted
	 * @return array list representation of the map (no keys)
	 */
	protected List<Object[]> sortMapToListResults(final Map<String, Object[]> mapResults) {
		if (mapResults == null) {
			return new ArrayList<Object[]>();
		}
		List<Object[]> finalResults = new ArrayList<Object[]>();
		finalResults.addAll(mapResults.values());
		
		Collections.sort(finalResults, new Comparator<Object[]>() {
			@Override
			public int compare(final Object[] row1, final Object[] row2) {
				String orderNum1 = (String) row1[FLATTENED_ORDER_NUMBER];
				String orderNum2 = (String) row2[FLATTENED_ORDER_NUMBER];
				
				return orderNum1.compareTo(orderNum2);
			}
		});
		return finalResults;
	}

	/**
	 * This method converts the exchange query result to a set of order exchange numbers.
	 *
	 * @param exchangeOrderNumberResults the list of order exchange query results
	 * @return a set of the order exchange numbers returns from the query
	 */
	protected Set<Object> convertToExchangeOrderNumberSet(final List<Object[]> exchangeOrderNumberResults) {
		Set<Object> orderNumbers = new HashSet<Object>();
		for (Object[] exchangeOrderNumberResult : exchangeOrderNumberResults) {
			orderNumbers.add(exchangeOrderNumberResult);
			orderNumbers.add(exchangeOrderNumberResult[DB_EXCHANGE_ORDER]);
		}
		return orderNumbers;
	}
	
	private Object[] constructNewRowElement(final Map<String, BigDecimal> taxMap, final Object[] queryResult, final String key, 
			final String taxKey, final boolean isReturn, final Set<Object> exchangeOrderNumberSet) {
		Object[] orderRow = new Object[ROW_LENGTH];
		
		OrderStatus orderStatus = (OrderStatus) queryResult[DB_ORDER_STATUS];
		CustomerImpl customer = (CustomerImpl) queryResult[DB_CUSTOMER];
		
		orderRow[FLATTENED_ORDER_NUMBER] = key;  
		orderRow[FLATTENED_CREATED_DATE] = queryResult[DB_CREATED_DATE];
		orderRow[FLATTENED_STATUS] = OrdersByStatusReportMessages.getLocalizedName(orderStatus);
		orderRow[FLATTENED_STORE] = queryResult[DB_STORE_NAME];
		orderRow[FLATTENED_CURRENCY] = queryResult[DB_CURRENCY];
		orderRow[FLATTENED_CREATED_BY_CSR] = queryResult[DB_CREATED_BY_CSR];  
		orderRow[FLATTENED_CUSTOMER_ID] = customer.getUidPk();
		orderRow[FLATTENED_CUSTOMER_LAST_NAME] = customer.getLastName();
		orderRow[FLATTENED_CUSTOMER_FIRST_NAME] = customer.getFirstName();
		orderRow[FLATTENED_CUSTOMER_EMAIL] = customer.getEmail();
		orderRow[FLATTENED_TAX_TOTAL] = taxMap.get(taxKey);
		orderRow[FLATTENED_DISC_PRICE] = null; //unused
		if (isReturn) {
			OrderReturn orderReturn = (OrderReturn) queryResult[DB_RETURN_ORDER];
			orderReturn.recalculateOrderReturn();
			orderRow[FLATTENED_TOTAL] = orderReturn.getReturnTotal().negate();
		} else {
			orderRow[FLATTENED_TOTAL] = queryResult[DB_TOTAL];
		}
		
		orderRow[FLATTENED_UNIT_PRICE] = BigDecimal.ZERO;
		orderRow[FLATTENED_LIST_PRICE] = BigDecimal.ZERO;
		orderRow[FLATTENED_SHIPPING_COST] = BigDecimal.ZERO;
		orderRow[FLATTENED_RETURN_FLAG] = isReturn;
		
		String exchangeOrder = OrdersByStatusReportMessages.no_as_string;
		if (exchangeOrderNumberSet.contains(key)) {
			exchangeOrder = OrdersByStatusReportMessages.yes_as_string;
		}
		orderRow[FLATTENED_EXCHANGE_ORDER] = exchangeOrder;
		
		return orderRow;
	}
	
	/**
	 * Convenience helper to make tax results easier to consume.
	 * 
	 * @param taxResults the list of results from the tax query
	 * @return taxMap mapping of order number to tax value
	 */
	private Map<String, BigDecimal> processTaxes(final List<Object[]> taxResults) {
		Map<String, BigDecimal> taxMap = new HashMap<String, BigDecimal>();
		for (Object[] taxResult : taxResults) {
			taxMap.put((String) taxResult[DB_TAX_ORDER_NUMBER], 
						(BigDecimal) taxResult[DB_TAX_VALUE]);
		}
		return taxMap;
	}

	private ReportService getReportService() {
		if (reportService == null) {
			reportService = LoginManager.getInstance().getBean(ContextIdNames.REPORT_SERVICE);
		}
		return reportService;
	}
}

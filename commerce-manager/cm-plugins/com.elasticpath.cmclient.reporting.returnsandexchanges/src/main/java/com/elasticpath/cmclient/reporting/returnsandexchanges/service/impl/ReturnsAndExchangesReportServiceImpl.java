/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.reporting.returnsandexchanges.ReturnsAndExchangesPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.returnsandexchanges.impl.ReturnsAndExchangesPreparedStatementBuilderImpl;
import com.elasticpath.cmclient.reporting.returnsandexchanges.parameters.ReturnsAndExchangesParameters;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.service.reporting.ReportService;
import com.elasticpath.service.store.WarehouseService;

/**
 * Local ReturnsAndExchangesReportService.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "restriction", "nls" })
public class ReturnsAndExchangesReportServiceImpl {

	private static ReportService reportService;

	/**
	 * The string corresponding to the Email key in the customer profile value table.
	 */
	protected static final String CP_EMAIL_KEYSTRING = "CP_EMAIL";

	/**
	 * The string corresponding to the First Name key in the customer profile value table.
	 */
	protected static final String CP_FIRST_NAME_KEYSTRING = "CP_FIRST_NAME";

	/**
	 * The string corresponding to the Last Name key in the customer profile value table.
	 */
	protected static final String CP_LAST_NAME_KEYSTRING = "CP_LAST_NAME";

	private static final int ORDER_NUMBER_INDEX = 0;
	private static final int ORDER_CREATED_DATE_INDEX = 1;
	private static final int ORDER_TOTAL_INDEX = 2;
	private static final int TAX_TOTAL_INDEX = 4;
	private static final int SHIPPING_COST_INDEX = 5;
	private static final int CREATED_BY_INDEX = 6;
	private static final int ORDER_STATUS_INDEX = 7;
	private static final int RETURN_DATE_INDEX = 8;
	private static final int REFUND_AMOUNT_INDEX = 9;
	private static final int CUSTOMER_UID = 10;
	private static final int CUSTOMER_LAST_NAME = 11;
	private static final int CUSTOMER_FIRST_NAME = 12;
	private static final int CUSTOMER_EMAIL = 13;
	private static final int RETURN_REASON_INDEX = 14;
	private static final int ORDER_RETURN_ROW_SIZE = 15;

	private static final int DB_ORDER_NUMBER_INDEX = 0;
	private static final int DB_ORDER_CREATED_DATE_INDEX = 1;
	private static final int DB_ORDER_TOTAL_INDEX = 2;
	private static final int DB_SHIPMENTS_INDEX = 3;
	private static final int DB_ORDER_STATUS_INDEX = 4;
	private static final int DB_CUSTOMER_INDEX = 5;

	private static final int DB_TAX_AMOUNT_INDEX = 1;

	private static final int DB_RETURN_REASON_INDEX = 1;
	private static final int DB_ORDER_RETURN_INDEX = 2;
	private static final int DB_RETURN_CMUSER_INDEX = 3;
	
	/**
	 * This method is called by BIRT Report and should return list of Object[]. 
	 * 
	 * Array of Objects should contain next values:
	 * <ol>
	 * <li>order number</li>
	 * <li>order created date</li>
	 * <li>order total</li>
	 * <li>order discounts</li>
	 * <li>tax total</li>
	 * <li>order shipping cost</li>
	 * <li>CM User Responsible for the Return</li>
	 * <li>order status</li>
	 * <li>return created date
	 * <li>refunded amount</li>
	 * <li>customer uid</li>
	 * <li>customer first name</li>
	 * <li>customer last name</li>
	 * <li>customer email</li>
	 * <li>return reason, compounded</li>
	 * </ol>
	 * 
	 * @return a list of Object[] that contain info about order returns on each order.
	 */
	public List<Object[]> orderReturnDataSet() {
		ReturnsAndExchangesPreparedStatementBuilder builder = getPreparedStatementBuilder();

		// Find all Return Orders and the return reasons
		JpqlQueryBuilder orderReturnSkuQuery = builder.getReturnInfoPerSkuQueryAndParams();
		final List<Object[]> orderReturnSkuResults = getReportService().execute(orderReturnSkuQuery.toString(),
				orderReturnSkuQuery.getParameterList().toArray());

		Set<String> orderNumbers = extractOrderNumbers(orderReturnSkuResults);
		JpqlQueryBuilder orderShipmentQuery = builder.getReturnOrderInfoPerShipmentQueryAndParams(orderNumbers);
		JpqlQueryBuilder orderTaxQuery = builder.getTaxesPerOrderQueryAndParams(orderNumbers);
		final List<Object[]> orderShipmentResults = getReportService().execute(orderShipmentQuery.toString(),
				orderShipmentQuery.getParameterList().toArray());
		final List<Object[]> orderTaxResults = getReportService().execute(orderTaxQuery.toString(),
				orderTaxQuery.getParameterList().toArray());

		Map<String, Object[]> flattenedResults = new HashMap<String, Object[]>();
		fillInOrderInformation(flattenedResults, orderShipmentResults);
		flattenOrderReturnResults(flattenedResults, orderReturnSkuResults);
		fillInTaxResults(flattenedResults, orderTaxResults);

		return convertMapToListResults(flattenedResults);
	}

	/**
	 * Extracts the order numbers from the query results. Each row is a sku so there may be
	 * repeated order numbers.
	 * 
	 * @param dbResults the query result
	 * @return orderNumberList the set of order numbers from the query results
	 */
	protected Set<String> extractOrderNumbers(final List<Object[]> dbResults) {
		Set<String> orderNumberList = new HashSet<String>();
		
		for (Object[] results : dbResults) {
			if (results[DB_ORDER_NUMBER_INDEX] != null) {
				orderNumberList.add((String) results[DB_ORDER_NUMBER_INDEX]);
			}
		}
		
		return orderNumberList;
	}

	private void fillInOrderInformation(final Map<String, Object[]> flattenedResults,
										final List<Object[]> orderShipmentResults) {
		// fill in shipment info
		for (Object[] orderShipmentResult : orderShipmentResults) { 
			String orderNumber = (String) orderShipmentResult[DB_ORDER_NUMBER_INDEX];
			Object[] orderRow = flattenedResults.get(orderNumber);
			
			if (orderRow == null) { //not found, create a new row for one order
				orderRow = createNewReportRow(orderShipmentResult);
			}
			if (orderShipmentResult[DB_SHIPMENTS_INDEX] instanceof PhysicalOrderShipmentImpl) {
				PhysicalOrderShipment physicalShipment = (PhysicalOrderShipmentImpl) orderShipmentResult[DB_SHIPMENTS_INDEX];
				orderRow[SHIPPING_COST_INDEX] = ((BigDecimal) orderRow[SHIPPING_COST_INDEX]).
								add(physicalShipment.getShippingCost());
			}
			flattenedResults.put(orderNumber, orderRow);
		}
	}

	/**
	 * For each return, there is one or more return skus and therefore one or more return reasons for each order.
	 * This method flattens those return reasons onto the same order return.
	 *
	 * For each return, there is a unique RMA code but may be linked to the same order.
	 * Since each row should represent one order, we need to sum up the sku refund totals per order.
	 *
	 * @param flattenedResults the mapping from order number to data row
	 * @param orderReturnSkuResults the order return data
	 */
	private void flattenOrderReturnResults(final Map<String, Object[]> flattenedResults,
											final List<Object[]> orderReturnSkuResults) {
		Set<String> usedRmaSet = new HashSet<String>();
		// fill in order return info
		final Map<String, String> concatenatedReasonMsg = concatenateReturnReasons(orderReturnSkuResults);
		for (Object[] orderReturnSkuResult : orderReturnSkuResults) { // each row is a sku
			String orderNumber = (String) orderReturnSkuResult[DB_ORDER_NUMBER_INDEX];
			Object[] orderRow = flattenedResults.get(orderNumber); //guaranteed to not be null

			CmUser cmUser = (CmUser) orderReturnSkuResult[DB_RETURN_CMUSER_INDEX];
			String cmUserName = cmUser.getFirstName() + " " + cmUser.getLastName();
			if (cmUser != null) {
				orderRow[CREATED_BY_INDEX] = cmUserName;
			}

			OrderReturn orderReturn = (OrderReturn) orderReturnSkuResult[DB_ORDER_RETURN_INDEX];
			orderReturn.recalculateOrderReturn();

			String cmUserString = (String) orderRow[CREATED_BY_INDEX];
			if (cmUserString == null && cmUserName != null) {
				cmUserString = cmUserName;
			} else if (!cmUserString.contains(cmUserName)) {
				cmUserString = cmUserString.concat(" / " + cmUserName);
			}
			orderRow[CREATED_BY_INDEX] = cmUserString;

			if (!usedRmaSet.contains(orderReturn.getRmaCode())) { // no RMAs applied yet
				usedRmaSet.add(orderReturn.getRmaCode());

				//apply Refund Amount
				orderRow[REFUND_AMOUNT_INDEX] = ((BigDecimal) orderRow[REFUND_AMOUNT_INDEX]).
						add(orderReturn.getRefundedTotal().negate());
			}

			orderRow[RETURN_DATE_INDEX] = orderReturn.getCreatedDate();
			orderRow[RETURN_REASON_INDEX] = concatenatedReasonMsg.get(orderNumber);
		}
	}

	private void fillInTaxResults(final Map<String, Object[]> flattenedResults,
									final List<Object[]> orderTaxResults) {
		// fill in order taxes
		for (Object[] result : orderTaxResults) {
			String orderNumber = (String) result[DB_ORDER_NUMBER_INDEX];
			Object[] reportRow = flattenedResults.get(orderNumber); //guaranteed to not be null
			reportRow[TAX_TOTAL_INDEX] = (BigDecimal) result[DB_TAX_AMOUNT_INDEX];
		}
	}

	private Object[] createNewReportRow(final Object[] queryResult) {
		Object[] reportRow = new Object[ORDER_RETURN_ROW_SIZE];
		reportRow[ORDER_NUMBER_INDEX] = queryResult[DB_ORDER_NUMBER_INDEX];
		reportRow[ORDER_CREATED_DATE_INDEX] = (Date) queryResult[DB_ORDER_CREATED_DATE_INDEX];
		reportRow[ORDER_TOTAL_INDEX] = queryResult[DB_ORDER_TOTAL_INDEX];
		reportRow[ORDER_STATUS_INDEX] = queryResult[DB_ORDER_STATUS_INDEX];
		reportRow[REFUND_AMOUNT_INDEX] = BigDecimal.ZERO;
		reportRow[SHIPPING_COST_INDEX] = BigDecimal.ZERO;
		
		CustomerImpl customer = (CustomerImpl) queryResult[DB_CUSTOMER_INDEX];
		reportRow[CUSTOMER_UID] = customer.getUidPk();
		reportRow[CUSTOMER_LAST_NAME] = customer.getLastName();
		reportRow[CUSTOMER_FIRST_NAME] = customer.getFirstName();
		reportRow[CUSTOMER_EMAIL] = customer.getEmail();
		
		return reportRow;
	}

	/**
	 * Maps Order Numbers to a concatenated return reason message.
	 *
	 * @param dbResults the results containing the return reasons
	 * @return the mapping
	 */
	private Map<String, String> concatenateReturnReasons(final List<Object[]> dbResults) {
		Map<String, String> reasonMsgMapping = new HashMap<String, String>();
		OrderReturnSkuReason orderReturnSkuReason = (OrderReturnSkuReason) LoginManager.getInstance().getBean(
				ContextIdNames.ORDER_RETURN_SKU_REASON);
		
		for (Object[] results : dbResults) {
			if (results[DB_ORDER_NUMBER_INDEX] != null) {
				String orderNumber = (String) results[DB_ORDER_NUMBER_INDEX];
				String reasonMessage = orderReturnSkuReason.getReasonMap().get(results[DB_RETURN_REASON_INDEX]);
				String concatenatedReasonMessage = reasonMsgMapping.get(orderNumber);
				if (concatenatedReasonMessage == null) {
					concatenatedReasonMessage = reasonMessage;
				} else {
					if (!concatenatedReasonMessage.contains(reasonMessage)) {
						concatenatedReasonMessage = concatenatedReasonMessage.concat(" / " + reasonMessage);
					}
				}

				reasonMsgMapping.put(orderNumber, concatenatedReasonMessage);
			}
		}
		return reasonMsgMapping;
	}

	/**
	 * Extracts the value collection from a map and return an array list representation. Returns an empty List if the map is null or empty.
	 * 
	 * @param mapResults The map collection to be converted
	 * @return array list representation of the map (no keys)
	 */
	protected List<Object[]> convertMapToListResults(final Map<String, Object[]> mapResults) {
		if (mapResults == null) {
			return new ArrayList<Object[]>();
		}
		List<Object[]> finalResults = new ArrayList<Object[]>();
		finalResults.addAll(mapResults.values());
		return finalResults;
	}

	/**
	 * Retrieves a list of Authorized Warehouses.
	 * 
	 * @return a list of authorized warehouses
	 */
	protected List<String> getAuthorizedWarehouses() {
		final WarehouseService warehouseService = (WarehouseService) LoginManager.getInstance().getBean(ContextIdNames.WAREHOUSE_SERVICE);
		List<String> warehouseNamesFilter;
		if (ReturnsAndExchangesParameters.getInstance().getWarehouse() == null) {
			List<Warehouse> warehouses = warehouseService.findAllWarehouses();
			AuthorizationService.getInstance().filterAuthorizedWarehouses(warehouses);
			warehouseNamesFilter = new ArrayList<String>(warehouses.size());
			for (Warehouse warehouse : warehouses) {
				warehouseNamesFilter.add(warehouse.getName());
			}		
		} else {
			warehouseNamesFilter = new ArrayList<String>(1);
			warehouseNamesFilter.add(ReturnsAndExchangesParameters.getInstance().getWarehouse().getName());
		}
		return warehouseNamesFilter;
	}

	private ReportService getReportService() {
		if (reportService == null) {
			reportService = LoginManager.getInstance().getBean(ContextIdNames.REPORT_SERVICE);
		}
		return reportService;
	}

	private ReturnsAndExchangesPreparedStatementBuilder getPreparedStatementBuilder() {
		return new ReturnsAndExchangesPreparedStatementBuilderImpl();
	}
}

/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus.impl;

import java.util.Collection;

import com.elasticpath.cmclient.reporting.common.PreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersbystatus.OrdersByStatusPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.ordersbystatus.parameters.OrdersByStatusParameters;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;

/**
 * JPA query builder for the Order Details Report.
 */
@SuppressWarnings("nls")
public class OrdersByStatusPreparedStatementBuilderImpl extends PreparedStatementBuilder 
			implements OrdersByStatusPreparedStatementBuilder {

	private static final String O_ORDER_SOURCE = "o.orderSource";
	private static final String O_ORDER_NUMBER = "o.orderNumber";
	
	/**
	 * Constructor for the Builder.
	 */
	public OrdersByStatusPreparedStatementBuilderImpl() {
		setParameters(OrdersByStatusParameters.getInstance());
	}

	@Override
	public JpqlQueryBuilder getReturnOrderInfoQueryAndParams() {
		final String selectFields = "orr, o.orderNumber, sum(skus.listUnitPriceInternal * orsku.quantity), "
					+   "sum(orsku.returnAmountInternal), o.status, o.createdDate,"
					+	"o.currency, o.total, o.customer, o.cmUserUID, o.storeCode";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderReturnImpl", "orr", selectFields);
		jpqlQueryBuilder.appendInnerJoin("orr.order", "o"); // one-to-one
		jpqlQueryBuilder.appendInnerJoin("orr.orderReturnSkus", "orsku"); //one-to-many
		jpqlQueryBuilder.appendInnerJoin("orsku.orderSku", "skus"); // one-to-one
		jpqlQueryBuilder.appendGroupBy(O_ORDER_NUMBER + ", orr, o.status, o.createdDate, o.currency, o.total, o.customer, o.cmUserUID, o.storeCode");
		jpqlQueryBuilder.appendOrderBy(O_ORDER_NUMBER, false);
		
		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		setWhereClauseAndParameters(jpqlQueryBuilder, whereGroup, ORR_CREATED_DATE);
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getTaxesPerReturnQueryAndParams(final Collection<String> rmaCodes) {
		final String selectFields = "orr.rmaCode, sum(t.taxAmount)";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderReturnImpl", "orr", selectFields);
		jpqlQueryBuilder.appendInnerJoin("TaxJournalRecordImpl", "t",
				"t.documentId = orr.taxDocumentIdInternal");
		jpqlQueryBuilder.appendGroupBy("orr.rmaCode");
		
		if (rmaCodes != null && !rmaCodes.isEmpty()) {
			JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();	
			whereGroup.appendWhereInCollection("orr.rmaCode", rmaCodes);
		}
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getOrderInfoPerShipmentQueryAndParams() {
		final String selectFields = "shipments, o.orderNumber, sum(skus.listUnitPriceInternal * skus.quantityInternal), "
				+ 	"sum(skus.amount), o.status, o.createdDate, " 
				+	"o.currency, o.total, o.customer, o.cmUserUID, o.storeCode";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);
		jpqlQueryBuilder.appendInnerJoin("o.shipments", "shipments");
		jpqlQueryBuilder.appendInnerJoin("shipments.shipmentOrderSkusInternal", "skus"); 
		jpqlQueryBuilder.appendGroupBy("shipments, o.orderNumber, o.status, o.createdDate, o.currency, "
				+ "o.total, o.customer, o.cmUserUID, o.storeCode");
		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();	
		setWhereClauseAndParameters(jpqlQueryBuilder, whereGroup, O_CREATED_DATE);
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getTaxesPerOrderQueryAndParams() {
		final String selectFields = "o.orderNumber, sum(t.taxAmount)";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);
		jpqlQueryBuilder.appendInnerJoin("o.shipments", "os");
		jpqlQueryBuilder.appendInnerJoin("TaxJournalRecordImpl", "t",
				"t.documentId = os.taxDocumentIdInternal");
		jpqlQueryBuilder.appendGroupBy("o.orderNumber");
		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		setWhereClauseAndParameters(jpqlQueryBuilder, whereGroup, O_CREATED_DATE);
		whereGroup.appendWhereEquals("t.journalType", "purchase");
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getExchangesInfoQueryAndParams() {
		final String selectFields = "r.exchangeOrder.orderNumber, r.uidPk";

		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderReturnImpl", "r", selectFields);
		
		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		
		OrdersByStatusParameters params = (OrdersByStatusParameters) getParameters();
		super.setWhereClauseAndParameters(whereGroup, "r.exchangeOrder.createdDate", "r.exchangeOrder.storeCode");
		whereGroup.appendWhereInCollection("r.exchangeOrder.status", params.getCheckedOrderStatuses()); 
		
		return jpqlQueryBuilder;
	}

	/**
	 * Sets the where clauses of the getPricingInfoQueryAndParams and getCustomerInfoQueryAndParams
	 * queries.
	 *
	 * @param jpqlQueryBuilder the querybuilder object
	 * @param whereGroup the whereGroup object for that querybuilder object
	 * @param dateField the date field
	 */
	protected void setWhereClauseAndParameters(final JpqlQueryBuilder jpqlQueryBuilder,
			final JpqlQueryBuilderWhereGroup whereGroup, final String dateField) {
		super.setWhereClauseAndParameters(whereGroup, dateField, O_STORE_CODE);
		
		OrdersByStatusParameters params = OrdersByStatusParameters.getInstance();
		
		if (params.getCurrency() != null) {
			whereGroup.appendWhereEquals("o.currency", params.getCurrency());
		}
		if (params.getOrderSource() != null && !params.getOrderSource().equalsIgnoreCase("all")) {
			if (params.getOrderSource().equalsIgnoreCase("null")) {
				whereGroup.appendWhereEmpty(O_ORDER_SOURCE);
			} else {
				whereGroup.appendWhereEquals(O_ORDER_SOURCE, params.getOrderSource());
			}
		}
		if (params.isShowExchangeOnly()) {
			jpqlQueryBuilder.appendInnerJoin("o.returns", "r"); 
			whereGroup.appendWhereEquals("r.returnType", OrderReturnType.EXCHANGE);
		}
		whereGroup.appendWhereInCollection("o.status", params.getCheckedOrderStatuses());
	}
}
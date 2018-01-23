/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges.impl;

import java.util.Set;

import com.elasticpath.cmclient.reporting.common.PreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.returnsandexchanges.ReturnsAndExchangesPreparedStatementBuilder;
import com.elasticpath.cmclient.reporting.returnsandexchanges.parameters.ReturnsAndExchangesParameters;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;

/**
 * JPA implementation for {@link ReturnsAndExchangesPreparedStatementBuilder}.
 */
@SuppressWarnings("nls")
public class ReturnsAndExchangesPreparedStatementBuilderImpl extends PreparedStatementBuilder
			implements ReturnsAndExchangesPreparedStatementBuilder {
	
	/**
	 * Constructor for the Builder.
	 */
	public ReturnsAndExchangesPreparedStatementBuilderImpl() {
		setParameters(ReturnsAndExchangesParameters.getInstance());
	}

	@Override
	public JpqlQueryBuilder getTaxesPerOrderQueryAndParams(final Set<String> orderNumber) {
		final String selectFields = "o.orderNumber, sum(t.taxAmount)";
		
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);
		jpqlQueryBuilder.appendInnerJoin("o.shipments", "os");
		jpqlQueryBuilder.appendInnerJoin("TaxJournalRecordImpl", "t", "t.documentId = os.taxDocumentIdInternal");
		jpqlQueryBuilder.appendGroupBy("o.orderNumber");
		
		JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();
		whereGroup.appendWhereInCollection("o.orderNumber", orderNumber); 
		whereGroup.appendWhereEquals("t.journalType", "purchase");
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getReturnInfoPerSkuQueryAndParams() {
		final String selectFields = "o.orderNumber, orsku.returnReason, orr, orr.createdByCmUser";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderReturnImpl", "orr", selectFields);
		jpqlQueryBuilder.appendInnerJoin("orr.order", "o"); //many-to-one
		jpqlQueryBuilder.appendInnerJoin("orr.orderReturnSkus", "orsku");
		
		final JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();	
		setWhereClauseAndParameters(jpqlQueryBuilder, whereGroup, ORR_CREATED_DATE);
		return jpqlQueryBuilder;
	}

	@Override
	public JpqlQueryBuilder getReturnOrderInfoPerShipmentQueryAndParams(final Set<String> orderNumbers) {
		final String selectFields = "o.orderNumber, o.createdDate, o.total, shipments, o.status, o.customer";
		final JpqlQueryBuilder jpqlQueryBuilder = new JpqlQueryBuilder("OrderImpl", "o", selectFields);
		jpqlQueryBuilder.appendInnerJoin("o.shipments", "shipments"); 
		jpqlQueryBuilder.appendGroupBy("o.orderNumber, o.createdDate, o.total, shipments, o.status, o.customer");
		
		final JpqlQueryBuilderWhereGroup whereGroup = jpqlQueryBuilder.getDefaultWhereGroup();	
		whereGroup.appendWhereInCollection("o.orderNumber", orderNumbers); 
		return jpqlQueryBuilder;
	}
	
	private void setWhereClauseAndParameters(final JpqlQueryBuilder jpqlQueryBuilder,
			final JpqlQueryBuilderWhereGroup whereGroup, 
			final String dateField) {
		super.setWhereClauseAndParameters(whereGroup, dateField, O_STORE_CODE);
		final ReturnsAndExchangesParameters params = ReturnsAndExchangesParameters.getInstance();
		whereGroup.appendWhereInCollection("orr.returnStatus", params.getCheckedOrderReturnStatuses()); 
		if (params.getCurrency() != null) {
			whereGroup.appendWhereEquals("o.currency", params.getCurrency());
		}

		if (params.getRmaType() != null) {
			whereGroup.appendWhereEquals("orr.returnType", params.getRmaType());
		}
		if (params.getWarehouse() != null) {
			jpqlQueryBuilder.appendInnerJoin("StoreImpl", "store", "o.storeCode = store.code"); 
			whereGroup.appendWhereEquals("store.warehouses.code", params.getWarehouse().getCode());
		}
	}
}

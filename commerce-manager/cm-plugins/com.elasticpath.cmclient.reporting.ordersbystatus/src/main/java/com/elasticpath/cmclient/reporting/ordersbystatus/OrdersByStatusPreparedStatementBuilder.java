/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus;

import java.util.Collection;

import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;

/**
 * Interface for the Order Details Statement Builder.
 */
public interface OrdersByStatusPreparedStatementBuilder {

	/**
	 * Builds the query to retrieve the order return info of an order.
	 * Each row of the result of the query is per sku.
	 * 
	 * The query is grouped by skus and later flattened.
	 * It is grouped by the RMA Code and then ordered by order number.
	 *
	 * @return a query builder object containing the querystring and parameters
	 */
	JpqlQueryBuilder getReturnOrderInfoQueryAndParams();
	
	/**
	 * Builds the query to retrieve the return taxes.
	 * @param rmaCodes a list of rmaCode to filter
	 * @return a query builder object containing the querystring and parameters
	 */
	JpqlQueryBuilder getTaxesPerReturnQueryAndParams(Collection<String> rmaCodes);
	
	/**
	 * Builds the query to retrieve the order taxes.
	 *
	 * @return a query builder object containing the querystring and parameters
	 */
	JpqlQueryBuilder getTaxesPerOrderQueryAndParams();
	
	/**
	 * Builds the query to retrieve the order info of an order.
	 * The relation from ordersku to taxjournal is one-to-many
	 * (ex, sku1 has GST, QST and PST...)
	 * 
	 * The query is grouped by skus and later flattened.
	 *
	 * @return a query builder object containing the querystring and parameters
	 */
	JpqlQueryBuilder getOrderInfoPerShipmentQueryAndParams();
	
	/**
	 * Builds the query to retrieve the exchanges info of an order.
	 *
	 * @return a query builder object containing the querystring and parameters
	 */
	JpqlQueryBuilder getExchangesInfoQueryAndParams();
}

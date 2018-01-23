/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges;

import java.util.Set;

import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;

/**
 * Implementors are responsible for building query strings to be passed to the PersistenceEngine. The query is for getting a list of returns and
 * exchanges that adhere to the specified parameters.
 */
public interface ReturnsAndExchangesPreparedStatementBuilder {

	/**
	 * Builds the query to retrieve the orders and returns information. The Return Reason is per sku (which cannot be 
	 * grouped by) so when this query gets executed, the number of rows returned is equal to the number of 
	 * return skus of all selected orders.
	 * 
	 * @param orderNumbers a collection of order numbers from order returns query
	 * @return a query builder object containing the query string and parameters
	 */
	JpqlQueryBuilder getReturnOrderInfoPerShipmentQueryAndParams(Set<String> orderNumbers);

	/**
	 * Builds the query to retrieve the order tax only. The tax table cannot be joined with the
	 * ReturnExchangesQuery because it will filter out order skus that have no returns
	 * (ie. INNER JOIN orr.orderReturnSkus)
	 * 
	 * @param orderNumbers a collection of order numbers from order returns query
	 * @return a query builder for getting the tax amount given a set of order numbers
	 */
	JpqlQueryBuilder getTaxesPerOrderQueryAndParams(Set<String> orderNumbers);
	
	/**
	 * Builds the query to retrieve the Return Reasons for each Return order.
	 * @return a query builder for getting return info per each sku
	 */
	JpqlQueryBuilder getReturnInfoPerSkuQueryAndParams();
}

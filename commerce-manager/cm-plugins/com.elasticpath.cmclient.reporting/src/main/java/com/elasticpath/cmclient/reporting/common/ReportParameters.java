/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.common;

import java.util.Date;

/**
 * Interface for Report parameters for the report navigation view.
 * 
 * All views will contain store, starting date, and ending date
 */
public interface ReportParameters {

	/**
	 * Gets the store.
	 *
	 * @return store code/name
	 */
	String getStore();
	
	/**
	 * Sets the store.
	 *
	 * @param store code/name
	 */
	void setStore(String store);
	
	/**
	 * Gets the starting date.
	 *
	 * @return starting date
	 */
	Date getStartDate();
	
	/**
	 * Sets the starting date.
	 *
	 * @param startDate starting date
	 */
	void setStartDate(Date startDate);
	
	/**
	 * Gets the ending date.
	 *
	 * @return endDate ending date
	 */
	Date getEndDate();
	
	/**
	 * Sets the ending date.
	 *
	 * @param endDate ending date
	 */
	void setEndDate(Date endDate);
}

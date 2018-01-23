/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersummary.parameters;

import java.util.Currency;
import java.util.Date;
import java.util.List;

import com.elasticpath.cmclient.reporting.ordersummary.OrderSummaryReportMessages;
import com.elasticpath.domain.order.OrderStatus;

/**
 * Model for order summary report's parameters.
 */
public class OrderSummaryParameters {
	
	private String store;
	
	private Date startDate;
	
	private Date endDate;
	
	private boolean showExchangeOnly;
	
	private Currency currency;
	
	private String orderSource;

	private String title;

	private List<OrderStatus> checkedOrderStatuses;
	
	/**
	 * Gets the store name.
	 * 
	 * @return String the store name
	 */
	public String getStore() {
		return store;
	}

	/**
	 * Sets the store name.
	 * 
	 * @param store the store name
	 */
	public void setStore(final String store) {
		this.store = store;
	}

	/**
	 * Gets the starting date.
	 * 
	 * @return Date the starting date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the starting date.
	 * 
	 * @param startDate the starting date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return Date the end date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets boolean state is exchange order only.
	 * 
	 * @return true if registration is anonymous, false otherwise
	 */
	public boolean isShowExchangeOnly() {
		return showExchangeOnly;
	}

	/**
	 * Sets the boolean state of exchange only.
	 * 
	 * @param showExchangeOnly boolean
	 */
	public void setShowExchangeOnly(final boolean showExchangeOnly) {
		this.showExchangeOnly = showExchangeOnly;
	}

	/**
	 * Gets the currency.
	 * @return currency name as String
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Sets the currency name.
	 * @param currency the name of the currency
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Gets order source.
	 * @return order source as String
	 */
	public String getOrderSource() {
		return orderSource;
	}

	/**
	 * Sets the order source.
	 * @param orderSource the order source
	 */
	public void setOrderSource(final String orderSource) {
		this.orderSource = orderSource;
	}

	/**
	 * Gets the title.
	 * @return the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * @param title the title to set.
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Gets List of Order Statuses.
	 * @return the checkedOrderStatuses.
	 */
	public List<OrderStatus> getCheckedOrderStatuses() {
		return checkedOrderStatuses;
	}

	/**
	 * Sets List of Order Statuses.
	 * @param checkedOrderStatuses the checkedOrderStatuses to set.
	 */
	public void setCheckedOrderStatuses(final List<OrderStatus> checkedOrderStatuses) {
		this.checkedOrderStatuses = checkedOrderStatuses;
	}

	/**
	 * Gets the exchange string for report title.
	 * @return the exchange string for report title
	 */
	public String getExchangeString() {
		if (showExchangeOnly) {
			return OrderSummaryReportMessages.exchangeOrderOnly;
		}
		return OrderSummaryReportMessages.emptyString;
	}

}

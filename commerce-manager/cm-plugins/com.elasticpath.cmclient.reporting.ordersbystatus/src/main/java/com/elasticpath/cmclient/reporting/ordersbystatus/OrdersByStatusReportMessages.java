/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.domain.order.OrderStatus;

/**
 * Messages class for the report plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.VariableNamingConventions" })
public final class OrdersByStatusReportMessages {

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.reporting.ordersbystatus.OrdersByStatusReportPluginResources"; //$NON-NLS-1$

	private OrdersByStatusReportMessages() {
	}

	public static String reportTitle;
	public static String report;

	// ----------------------------------------------------
	// Errors
	// ----------------------------------------------------
	public static String checkBoxNoneSelectedError;
	
	// ----------------------------------------------------
	// Registration Report params UI
	// ----------------------------------------------------
	public static String store;
	public static String fromDate;
	public static String toDate;
	public static String orderStatusGroupHeader;
	public static String selectStore;
	public static String exchangeOnly;
	public static String currency;

	// ----------------------------------------------------
	// Order Source Drop down options UI
	// ----------------------------------------------------
	public static String allsources;
	public static String selectedStore;
	public static String googleCheckout;
	public static String webServices;

	// ----------------------------------------------------
	// Order Status Text
	// ----------------------------------------------------
	public static String waitExchangeComplete;
	public static String cancelled;
	public static String complete;
	public static String created;
	public static String inProgress;
	public static String onHold;
	public static String partialShip;

	// ----------------------------------------------------
	// Others
	// ----------------------------------------------------
	public static String orderSource;
	public static String emptyString;
	public static String exchangeOrderOnly;
	public static String no_as_string;
	public static String yes_as_string;

	// Define the map of enum constants to localized names
	private static Map<OrderStatus, String> localizedEnums = new HashMap<OrderStatus, String>();

	static {
		NLS.initializeMessages(BUNDLE_NAME, OrdersByStatusReportMessages.class);
		localizedEnums.put(OrderStatus.CANCELLED, cancelled);
		localizedEnums.put(OrderStatus.COMPLETED, complete);
		localizedEnums.put(OrderStatus.CREATED, created);
		localizedEnums.put(OrderStatus.ONHOLD, onHold);
		localizedEnums.put(OrderStatus.IN_PROGRESS, inProgress);
		localizedEnums.put(OrderStatus.AWAITING_EXCHANGE, waitExchangeComplete);
		localizedEnums.put(OrderStatus.PARTIALLY_SHIPPED, partialShip);
	}

	/**
	 * Returns the localized name of the given enum constant.
	 * 
	 * @param orderStatus the orderStatus to be localized
	 * @return the localized string for the enum
	 */
	public static String getLocalizedName(final OrderStatus orderStatus) {
		return localizedEnums.get(orderStatus);
	}

}

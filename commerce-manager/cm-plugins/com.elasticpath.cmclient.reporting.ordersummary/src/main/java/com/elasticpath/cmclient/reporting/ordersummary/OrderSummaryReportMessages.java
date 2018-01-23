/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersummary;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.nls.LocalizedMessageNLS;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.domain.order.OrderStatus;

/**
 * Messages class for the report plugin.
 */
@SuppressWarnings("PMD.TooManyFields")
public final class OrderSummaryReportMessages {

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.reporting.ordersummary.OrderSummaryReportPluginResources"; //$NON-NLS-1$

	private OrderSummaryReportMessages() {
	}

	public static String reportTitle;

	public static String report;

	public static String emptyString;

	// ----------------------------------------------------
	// Order Summary Report params UI
	// ----------------------------------------------------
	public static String store;

	public static String fromDate;

	public static String toDate;

	public static String currency;

	public static String orderSource;

	public static String exchangeOnly;

	public static String orderStatusGroupHeader;

	public static String waitExchangeComplete;

	public static String cancelled;

	public static String complete;

	public static String created;

	public static String inProgress;

	public static String onHold;

	public static String partialShip;
	
	public static String failed;

	public static String allStores;

	public static String allsources;

	public static String selectedStore;

	public static String googleCheckout;

	public static String webServices;

	public static String checkBoxNoneSelectedError;

	public static String exchangeOrderOnly;

	static {
		NLS.initializeMessages(BUNDLE_NAME, OrderSummaryReportMessages.class);
	}

	// Define the map of enum constants to localized names
	private static Map<OrderStatus, String> localizedEnums = new HashMap<OrderStatus, String>();

	static {
		localizedEnums.put(OrderStatus.CANCELLED, cancelled);
		localizedEnums.put(OrderStatus.COMPLETED, complete);
		localizedEnums.put(OrderStatus.CREATED, created);
		localizedEnums.put(OrderStatus.ONHOLD, onHold);
		localizedEnums.put(OrderStatus.IN_PROGRESS, inProgress);
		localizedEnums.put(OrderStatus.AWAITING_EXCHANGE, waitExchangeComplete);
		localizedEnums.put(OrderStatus.PARTIALLY_SHIPPED, partialShip);
		localizedEnums.put(OrderStatus.FAILED, failed);
	}

	static{
		load();
	}

	/**
	 * loads localized messages for this plugin.
	 */
	public static void load() {
		LocalizedMessageNLS.getUTF8Encoded(BUNDLE_NAME, OrderSummaryReportMessages.class);
	}
	/**
	 * Returns the localized name of the given enum constant.
	 * 
	 * @param enumValue the enum to be localized
	 * @return the localized string for the enum
	 */
	public static String getLocalizedName(final OrderStatus enumValue) {
		return localizedEnums.get(enumValue);
	}

}
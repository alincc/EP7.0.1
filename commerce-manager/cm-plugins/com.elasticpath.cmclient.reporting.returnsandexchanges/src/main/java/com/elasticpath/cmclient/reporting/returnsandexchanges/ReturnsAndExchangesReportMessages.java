/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;

/**
 * Messages class for the report plugin.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.VariableNamingConventions" })
public final class ReturnsAndExchangesReportMessages {

	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.reporting.returnsandexchanges." + //$NON-NLS-1$
			"ReturnsAndExchangesReportPluginResources"; //$NON-NLS-1$

	private ReturnsAndExchangesReportMessages() {
	}

	public static String reportTitle;
	public static String report;

	// ----------------------------------------------------
	// Registration Report params UI
	// ----------------------------------------------------
	public static String store;
	public static String currency;
	public static String warehouse;
	public static String fromDate;
	public static String toDate;
	public static String rmaType;
	public static String statusGroupHeader;

	public static String waitExchangeComplete;
	public static String cancelled;
	public static String complete;
	public static String awaitingStockReturn;
	public static String awatingCompletion;

	public static String allWarehouse;
	public static String allStore;

	public static String returnsAndExchanges;
	public static String returnsOnly;
	public static String exchangesOnly;
	public static String checkBoxNoneSelectedError;
	
	// ----------------------------------------------------
	// Registration Report service
	// ----------------------------------------------------
	public static String no_as_string;
	public static String yes_as_string;

	// ----------------------------------------------------
	// Order Return Status Text
	// ----------------------------------------------------
	public static String OrderReturnStatus_Cancelled;
	public static String OrderReturnStatus_Completed;
	public static String OrderReturnStatus_AwaitingCompletion;
	public static String OrderReturnStatus_AwaitingStockReturn;
	public static String OrderReturnType_Exchange;
	public static String OrderReturnType_Return;

	static {
		NLS.initializeMessages(BUNDLE_NAME, ReturnsAndExchangesReportMessages.class);
	}
	
	/**
	 * Converts OrderReturnStatus to the localized string.
	 * 
	 * @param orderReturnStatus the order return status
	 * @return the localized name of the order return status
	 */
	public static String getLocalizedOrderReturnStatus(final OrderReturnStatus orderReturnStatus) {
		return CoreMessages.get().getMessage(orderReturnStatus.getPropertyKey());
	}

	/**
	 * Converts OrderReturnType to the localized string.
	 * 
	 * @param orderReturnType the order return status
	 * @return the localized name of the order return type
	 */
	public static String getLocalizedOrderReturnType(final OrderReturnType orderReturnType) {
		return CoreMessages.get().getMessage(orderReturnType.getPropertyKey());
	}

}

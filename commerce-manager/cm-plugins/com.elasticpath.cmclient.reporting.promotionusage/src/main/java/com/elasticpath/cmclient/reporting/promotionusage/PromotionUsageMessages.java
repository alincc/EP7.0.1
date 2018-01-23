/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.promotionusage;

import com.elasticpath.cmclient.core.nls.LocalizedMessageNLS;
import org.eclipse.osgi.util.NLS;

/**
 * Messages class for the report plugin.
 *
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class PromotionUsageMessages {

	private static final String BUNDLE_NAME = 
		"com.elasticpath.cmclient.reporting.promotionusage.PromotionUsageMessages"; //$NON-NLS-1$
	
	private PromotionUsageMessages() {
	}

	public static String reportTitle;
	public static String report;
	
	public static String selectAll;
	public static String selectCurrency;
	public static String store;
	public static String currency;
	public static String fromdate;
	public static String todate;
	public static String promotype;
	public static String promotypeCart;
	public static String promotypeCatalog;
	public static String onlyWithCoupons;

	static {
		load();
	}

	/**
	 * loads localized messages for this plugin.
	 */
	public static void load() {
		LocalizedMessageNLS.getUTF8Encoded(BUNDLE_NAME, PromotionUsageMessages.class);
	}
	
}

/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.reporting;

import com.elasticpath.cmclient.core.nls.LocalizedMessageNLS;
import org.eclipse.osgi.util.NLS;

/**
 * Messages class for the admin plugin.
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class ReportingMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.reporting.ReportingPluginResources"; //$NON-NLS-1$
	
	private ReportingMessages() { }
	
	public static String monitorRunning;
		
	public static String reportTypeRequired;
	
	public static String reportType;
	
	public static String selectReportType;
	
	public static String runReport;
	
	public static String clear;
	
	public static String parameters;
	
	public static String emptyString;
	
	public static String exportExcel;
	
	public static String exportPDF;
	
	public static String exportCSV;
	
	public static String exportPDFTip;
	
	public static String exportCSVTip;
	
	public static String exportExcelTip;
	
	public static String csv;
	
	public static String filterCsv;
	
	public static String pdf;
	
	public static String filterPdf;
	
	public static String xls;
	
	public static String filterXls;
	
	// ----------------------------------------------------
	//  Default package
	// ----------------------------------------------------

	static {
		load();
	}

	/**
	 * loads localized messages for this plugin.
	 */
	public static void load() {
		LocalizedMessageNLS.getUTF8Encoded(BUNDLE_NAME, ReportingMessages.class);
	}
}
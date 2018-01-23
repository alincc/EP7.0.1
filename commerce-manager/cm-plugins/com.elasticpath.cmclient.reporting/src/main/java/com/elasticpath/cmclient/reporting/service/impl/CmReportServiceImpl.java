/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IDataExtractionTask;
import org.eclipse.birt.report.engine.api.IDataIterator;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IExtractionResults;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IResultMetaData;
import org.eclipse.birt.report.engine.api.IResultSetItem;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.impl.RenderTask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.reporting.ReportingMessages;
import com.elasticpath.cmclient.reporting.ReportingPlugin;
import com.elasticpath.cmclient.reporting.service.CmReportService;
import com.lowagie.tools.ConcatPdf;

/**
 * 
 * Provides services to create reports with the 
 * Eclipse Business Intelligence Reporting Tool (BIRT).
 */
@SuppressWarnings({"PMD.AvoidUsingShortType", "PMD.GodClass"})
public class CmReportServiceImpl implements CmReportService {

	private static final Logger LOG = Logger.getLogger(CmReportServiceImpl.class);	
	
	/**
	 * specifies the temp folder.
	 */
	private static final String TEMPFILENAME = "tempReport"; //$NON-NLS-1$
	
	/**
	 * specifies the buffer size.
	 */
	private static final int BUFFER_SIZE = 1024;
	
	/**
	 * For POI excel spreadsheet column width, the unit is 1/256 of a character.
	 */
	private static final int NORMALIZE_FACTOR = 256;
	
	private static final double ONEPOINTFIVE = 1.5;
	
	private IEngineTask task;

	private static final String NAME_TO_BE_FILTERED = "TOTAL_COLUMN"; //$NON-NLS-1$
	
	private IReportEngine engine;

	/**
	 * Initialize the reporting task.  Must be called once for every report.
	 * 
	 * @param isCSVorExcel true if wants to export report in csv or excel format, false otherwise
	 * @param reportLocation the report location in disk
	 * @param classLoader the classloader of each reporting service
	 * @param params parameters of the report
	 */
	public void initializeTask(final boolean isCSVorExcel,
		final URL reportLocation, final ClassLoader classLoader, final Map<String, Object> params) {
		try {
			engine = ReportingPlugin.initializeEngine();
		} catch (BirtException e) {
			LOG.error("Report Engine failed to initialize", e); //$NON-NLS-1$ //NOPMD
		}		
		try {
			task = createTask(isCSVorExcel, reportLocation, classLoader, params);
		} catch (IOException e) {
			LOG.error(e);
		}
	}
	
	/**
	 * Closes the engine task when finishes.
	 */
	public void closeTask() {
		IReportEngine engine = task.getEngine();
		task.close();
		engine.destroy();	
	}
	
	/**
	 * Creates the IEngineTask to run and render reports.
	 * @param isCSV for csv file
	 * @param reportLocation absolute path of the report
	 * @return IEngineTask the task created
	 * @throws IOException thrown when IO exceptions occur.
	 */
	private IEngineTask createTask(final boolean isCSVorExcel,
			final URL reportLocation, final ClassLoader classLoader, final Map<String, Object> params) throws IOException {
		
		File tempFile = null;
		InputStream urlstream = null;
		IRunTask runtask = null;
		
		try {
			urlstream = reportLocation.openStream();
			IReportRunnable design = null;
			try {
				design = engine.openReportDesign(urlstream);
			} catch (final Exception e) {
				LOG.error(new StringBuffer("Engine not initialized"), e); //$NON-NLS-1$
			}

			runtask = engine.createRunTask(design);
			HashMap<String, ClassLoader> contextMap = new HashMap<String, ClassLoader>();
			contextMap.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, new RhinoClassLoader(classLoader)); 
			setTaskParameters(runtask, params);
			engine.getConfig().setAppContext(contextMap);


			try {
				tempFile = File.createTempFile(TEMPFILENAME, ".rptdocument"); //$NON-NLS-1$
			} catch (IOException e1) {
				LOG.error(new StringBuffer("IOException thrown when creating temp rptdocument file"), e1); //$NON-NLS-1$
			}
			
			try {
				runtask.run(tempFile.getAbsolutePath());
			} catch (final Exception e) {
				LOG.error(new StringBuffer("Error running engine task"), e); //$NON-NLS-1$
			}
		} finally {
			if (runtask != null) {
				runtask.close();
			}
			if (urlstream != null) {
				urlstream.close();
			}
		}

		IReportDocument doc = null;
		try {
			doc = engine.openReportDocument(tempFile.getAbsolutePath());
		} catch (final Exception e) {
			LOG.error(new StringBuffer("Error opening report document"), e); //$NON-NLS-1$
		}
		IEngineTask task = engine.createRenderTask(doc);
		if (isCSVorExcel) {
			task = engine.createDataExtractionTask(doc);
		}
		tempFile.deleteOnExit();
		return task;
	}
	
	/**
	 * Set parameter values on report.  Don't set values if null.  Package-level
	 * for testing purposes only.
	 * @param task task to set params.
	 * @param params map of params.
	 */
	void setTaskParameters(final IEngineTask task, final Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return;
		}
		for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
			String name = i.next();
			task.setParameterValue(name, params.get(name));
		}
	}
	
	/**
	 * Views the HTML report in a browser.
	 * @param browser the browser object where HTML report can be viewed
	 */
	public void viewHTMLReport(final Browser browser) {

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
//			final IReportEngine engine = ReportingPlugin.getEngine();
//			final IRenderTask task = (IRenderTask) createTask(engine, false, reportLocation, classLoader, params);
			
			final IRenderOption options = new HTMLRenderOption();
			options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_HTML);
			options.setOutputStream(bos);

			IRenderTask renderTask = (IRenderTask) task;
			
			renderTask.setRenderOption(options);
			
			renderTask.render();
//			renderTask.close();

			browser.setText(bos.toString("UTF-8"));
//			engine.destroy();
			
			

		
		} catch (final BirtException birtException) {
			LOG.error(new StringBuffer("Engine Exception thrown")); //$NON-NLS-1$
			LOG.error(new StringBuffer("Birt Exception thrown")); //$NON-NLS-1$ //NOPMD
		} catch (UnsupportedEncodingException e) {
			LOG.error("Encoding is not supported", e); //$NON-NLS-1$ //NOPMD
		}

	}
	
	private String saveFile(final String filterExtensionType, final String dialogTitle) {
		final StringBuilder result = new StringBuilder();
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				final FileDialog fDialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.SAVE);
				fDialog.setText(dialogTitle);
				fDialog.setFilterExtensions(new String[] {filterExtensionType});
				result.append(fDialog.open());
			}
		});
		return result.toString();
	}
	
	/**
	 * Saves the report in Excel (XLS) format.
	 */
	public void makeExcel() {
		try {
//			final IReportEngine engine = ReportingPlugin.getEngine();
			final IDataExtractionTask dataExtractionTask = (IDataExtractionTask) task;
			final List<?> resultSetList = dataExtractionTask.getResultSetList();
			final IResultSetItem resultItem = (IResultSetItem) resultSetList.get(0);
			final String dispName = resultItem.getResultSetName();
			dataExtractionTask.selectResultSet(dispName);
			final IExtractionResults iExtractResults = dataExtractionTask.extract();
			
			//File tempFile = new File(TEMPDIRECTORY + "//tempReport.xls"); //$NON-NLS-1$
			File tempFile = File.createTempFile(TEMPFILENAME, ReportingMessages.xls);
			final FileOutputStream file = new FileOutputStream(tempFile);
			
			HSSFWorkbook spreadSheetWorkBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = spreadSheetWorkBook.createSheet("page 1"); //$NON-NLS-1$
			
			if (iExtractResults != null) {
				writeToXLS(iExtractResults, spreadSheet);
			}
			
			spreadSheetWorkBook.write(file);
			if (!Program.launch(tempFile.toString())) {
				String savePath = saveFile(ReportingMessages.filterXls, ReportingMessages.xls);
				final FileOutputStream fileToSave = new FileOutputStream(savePath);
				spreadSheetWorkBook.write(fileToSave);
				fileToSave.close();
			}
			file.close();
			iExtractResults.close();
			tempFile.deleteOnExit();
//			task.close();
//			engine.destroy();
		} catch (final FileNotFoundException exception) {
			LOG.error(new StringBuffer("File Not Found")); //$NON-NLS-1$
		} catch (final IOException exception) {
			LOG.error(new StringBuffer("IOException thrown")); //$NON-NLS-1$
		} catch (final BirtException exception) {
			LOG.error(new StringBuffer("Birt Exception thrown")); //$NON-NLS-1$
		}
	
	}

	/**
	 * Writes the report output to xls report spreadsheet.
	 * @param iExtractResults the result output from report
	 * @param spreadSheet the spreadsheet to write to
	 * @throws BirtException exception thrown
	 */
	private void writeToXLS(final IExtractionResults iExtractResults, final HSSFSheet spreadSheet) throws BirtException {
		final IDataIterator iData = iExtractResults.nextResultIterator();
		if (iData != null) {
			HSSFRow columnRow = spreadSheet.createRow((short) 0);
			
			//Get metadata on retrieved results
			final IResultMetaData irmd = iData.getResultMetaData();
			int avgColumnLength = 0;
			List<Integer> filteredIndices = new ArrayList<Integer>();
			for (int i = 0; i < irmd.getColumnCount(); i++) {
				//*** NOTICE: Workaround for MSC-6327.
				String columnName = irmd.getColumnName(i);
				if (isRightColumn(columnName)) {
					continue;
				}
				filteredIndices.add(i);
				//************************************ 
				HSSFCell cell = columnRow.createCell((short) filteredIndices.indexOf(i));
				cell.setCellValue(new HSSFRichTextString(columnName));
				avgColumnLength += irmd.getColumnLabel(i).length();
			}
			avgColumnLength = avgColumnLength / filteredIndices.size();
			int dataRowCount = 1;
			while (iData.next()) {
				HSSFRow dataRow = spreadSheet.createRow((short) dataRowCount);
				for (int index : filteredIndices) {
					Object temp = iData.getValue(index);
					int columnNumber = filteredIndices.indexOf(index);
					int dataLength = 0;
					if (temp != null) { //NOPMD
						dataLength = temp.toString().length();
					} else {
						dataLength = avgColumnLength;
					}
					if (iData.getValue(index) == null) {
						HSSFCell dataCell = dataRow.createCell((short) columnNumber);
						dataCell.setCellValue(new HSSFRichTextString("")); //$NON-NLS-1$
						spreadSheet.setColumnWidth((short) columnNumber, 
							(short) (avgColumnLength * NORMALIZE_FACTOR * ONEPOINTFIVE));
					} else {
						HSSFCell dataCell = dataRow.createCell((short) columnNumber);
						setCellValue(iData.getValue(index), dataCell);
						if (dataLength > avgColumnLength) {
							spreadSheet.setColumnWidth((short) columnNumber, 
								(short) (dataLength * NORMALIZE_FACTOR * ONEPOINTFIVE));
						} else {
							spreadSheet.setColumnWidth((short) columnNumber, 
								(short) (avgColumnLength * NORMALIZE_FACTOR * ONEPOINTFIVE));
						}
					}
				}
				dataRowCount++;
			}
			iData.close();
		}
	}

	/**
	 * Sets the data cell value of the excel spreadsheet. Default case is to turn the data into a
	 * HSSFRichTextString format.
	 *
	 * @param value the value to input into the cell
	 * @param dataCell the cell in the spreadsheet
	 * @throws BirtException throws a BirtException
	 */
	private void setCellValue(final Object value, final HSSFCell dataCell) throws BirtException {
		if (value instanceof Integer) {
			dataCell.setCellValue(Double.valueOf(value.toString()));
		} else if (value instanceof Double) {
			dataCell.setCellValue((Double) value);
		} else if (value instanceof Boolean) {
			dataCell.setCellValue((Boolean) value);
		} else {
			dataCell.setCellValue(new HSSFRichTextString(value.toString()));
		}
	}

	/**
	 * Saves the report in PDF format.
	 */
	public void makePdf() {

		try {
//			final IReportEngine engine = ReportingPlugin.getEngine();
			final IRenderTask renderTask = (IRenderTask) task;
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();

			final IRenderOption options = new HTMLRenderOption();
			options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
			options.setOutputStream(bos);

			renderTask.setRenderOption(options);
			renderTask.render();

			//File tempFile = new File(TEMPDIRECTORY + "//tempReport.pdf"); //$NON-NLS-1$
			File tempFile = File.createTempFile(TEMPFILENAME, ReportingMessages.pdf);
			final FileOutputStream file = new FileOutputStream(tempFile);
			bos.writeTo(file);
			
			if (!Program.launch(tempFile.toString())) {
				String savePath = saveFile(ReportingMessages.filterPdf, ReportingMessages.pdf);
				final FileOutputStream fileToSave = new FileOutputStream(savePath);
				bos.writeTo(fileToSave);
				fileToSave.close();
			}
			bos.close();
			file.close();
			tempFile.deleteOnExit();
//			task.close();
//			engine.destroy();
		} catch (final FileNotFoundException exception) {
			LOG.error(new StringBuffer("File Not Found")); //$NON-NLS-1$
		} catch (final IOException exception) {
			LOG.error(new StringBuffer("IOException thrown")); //$NON-NLS-1$
		} catch (final Exception exception) {
			LOG.error(new StringBuffer("Birt Exception thrown")); //$NON-NLS-1$
		}

	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void makePdf(final List<URL> reportLocations, 
		final List<Map<String, Object>> params, final ClassLoader classLoader) {

		if (reportLocations.size() != params.size()) {
			LOG.error(new StringBuffer("Wrong parameters")); //$NON-NLS-1$
			return;
		}

		try {
//			final IReportEngine engine = ReportingPlugin.getEngine();

			List<String> files = new ArrayList<String>();
			for (int i = 0; i < reportLocations.size(); ++i) {

				initializeTask(false, reportLocations.get(i), classLoader, params.get(i));
				
				final IRenderTask renderTask = (RenderTask) task;
					
				final ByteArrayOutputStream bos = new ByteArrayOutputStream();

				final IRenderOption options = new HTMLRenderOption();
				options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
				options.setOutputStream(bos);

				renderTask.setRenderOption(options);
				renderTask.render();

				//File tempFile = new File(TEMPDIRECTORY + "//tempReport.pdf"); //$NON-NLS-1$
				File tempFile = File.createTempFile(TEMPFILENAME, ReportingMessages.pdf);
				final FileOutputStream file = new FileOutputStream(tempFile);
				bos.writeTo(file);

				files.add(tempFile.toString());

				bos.close();
				file.close();
				task.close();
			}
			
			File resFile = File.createTempFile(TEMPFILENAME, ReportingMessages.pdf);
			final String fileName = resFile.toString(); 
			
			files.add(fileName);

			ConcatPdf.main(files.toArray(new String[files.size()]));

			if (!Program.launch(fileName)) {
				String savePath = saveFile(ReportingMessages.filterPdf, ReportingMessages.pdf);				
				
				final FileInputStream inFile  = new FileInputStream(fileName);
				final FileOutputStream outFile = new FileOutputStream(savePath);

				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead;
				while ((bytesRead = inFile.read(buffer)) >= 0) {					
					outFile.write(buffer, 0, bytesRead);
				}
				outFile.close();
				inFile.close();
			}

//			engine.destroy();
			
		} catch (final FileNotFoundException exception) {
			LOG.error(new StringBuffer("File Not Found3")); //$NON-NLS-1$
		} catch (final IOException exception) {
			LOG.error(new StringBuffer("IOException thrown3")); //$NON-NLS-1$
		} catch (final Exception exception) {
			LOG.error(new StringBuffer("Birt Exception thrown3")); //$NON-NLS-1$
		}
	}
	
	
	/**
	 * Saves report in CSV format.
	 */
	public void makeCSV() {
		try {
//			final IReportEngine engine = ReportingPlugin.getEngine();
			final IDataExtractionTask dataExtractionTask = (IDataExtractionTask) task;
			final List<?> resultSetList = dataExtractionTask.getResultSetList();
			final IResultSetItem resultItem = (IResultSetItem) resultSetList.get(0);
			final String dispName = resultItem.getResultSetName();
			dataExtractionTask.selectResultSet(dispName);
			final IExtractionResults iExtractResults = dataExtractionTask.extract();
			
			//File tempFile = new File(TEMPDIRECTORY + "//tempReport.csv"); //$NON-NLS-1$
			File tempFile = File.createTempFile(TEMPFILENAME, ReportingMessages.csv);
			final FileWriter file = new FileWriter(tempFile);
			
			printCSV(iExtractResults, file);
			if (!Program.launch(tempFile.toString())) {
				String savePath = saveFile(ReportingMessages.filterCsv, ReportingMessages.csv);
				final FileWriter fileToSave = new FileWriter(savePath);
				if (iExtractResults != null) {
					printCSV(iExtractResults, fileToSave);
				}
				fileToSave.close();
			}
			file.close();
			iExtractResults.close();
			tempFile.deleteOnExit();
//			task.close();
//			engine.destroy();
		} catch (final FileNotFoundException exception) {
			LOG.error(new StringBuffer("File Not Found")); //$NON-NLS-1$
		} catch (final IOException exception) {
			LOG.error(new StringBuffer("IOException thrown")); //$NON-NLS-1$
		} catch (final Exception exception) {
			LOG.error(new StringBuffer("Birt Exception thrown")); //$NON-NLS-1$
		
		} 
	}

	private void printCSV(final IExtractionResults iExtractResults, final FileWriter file) {
		try {
			final IDataIterator iData = iExtractResults.nextResultIterator();
			if (iData != null) {
				PrintWriter writer = new PrintWriter(file);
				//Get metadata on retrieved results
				final IResultMetaData irmd = iData.getResultMetaData();
				List<Integer> filteredIndices = new ArrayList<Integer>();
				for (int i = 0; i < irmd.getColumnCount(); i++) {
					String columnName = irmd.getColumnLabel(i);
					if (isRightColumn(columnName)) {
						continue;
					}
					filteredIndices.add(i);
					writer.print("\"" + columnName.replaceAll("\"", "\"\"") + "\","); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
				}
				writer.println();
				while (iData.next()) {
					printRow(iData, filteredIndices, writer);
				}
				writer.close();
				iData.close();
			}
		} catch (final Exception exception) {
			LOG.error(new StringBuffer("Birt Exception thrown")); //$NON-NLS-1$
		} 
	}
	
	private void printRow(final IDataIterator dataitr, final List<Integer> filteredIndices, final PrintWriter writer) {
		try {
			for (int index : filteredIndices) {
				if (dataitr.getValue(index) == null) {
					writer.print(","); //$NON-NLS-1$
				} else {
					writer
							.print("\"" + dataitr.getValue(index).toString().replaceAll(//$NON-NLS-1$
													"\"", "\"\"") + "\","); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		} catch (final Exception exception) {
			LOG.error(new StringBuffer("Failed to get column value.")); //$NON-NLS-1$
		}
		writer.println();
	}

	/*
	 * This methods should return if column defined by columnName must be displayed on report. 
	 */
	private boolean isRightColumn(final String columnName) {
		return columnName.contains(NAME_TO_BE_FILTERED.subSequence(0, NAME_TO_BE_FILTERED.length()));
	}

	
	
	/**
	 * Forces Birt to work with Rhino from the OSGi class-realm, rather than finding the
	 * Rhino classes from cmclientlibs (currently 1.6R2 as part of spring modules.  The 
	 * upgrade to RCP 3.6 requried us to do this as there is a class check in Kit.class
	 * (testIfCanLoadRhinoClasses) which would fail to recognize two copies of 
	 * ContextFactory as the same class. 
	 */
	private static class RhinoClassLoader extends ClassLoader {

		private final ClassLoader reportPluginClassLoader;
		private final ClassLoader classLoaderForRhino;

		/**
		 * Create an instance to fool Rhino into working.
		 * @param applicationClassLoader the report-specific classloader.
		 */
		RhinoClassLoader(final ClassLoader applicationClassLoader) {
			this.reportPluginClassLoader = applicationClassLoader;
			this.classLoaderForRhino = org.eclipse.birt.report.engine.api.DataID.class.getClassLoader();
		}

		@Override
		protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			// Force loading of this class through the Rhino classloader to trick Birt to work.
			if ("org.mozilla.javascript.ContextFactory".equals(name)) { //$NON-NLS-1$
				return classLoaderForRhino.loadClass(name);
			}			
			return reportPluginClassLoader.loadClass(name);
		}
	}	
	
}

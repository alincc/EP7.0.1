/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.reporting.views;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.reporting.IReport;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.ReportingImageRegistry;
import com.elasticpath.cmclient.reporting.ReportingMessages;
import com.elasticpath.cmclient.reporting.service.CmReportService;

/**
 * The view that allows a user to select a specific report from a list.
 */
public class ReportingNavigationView extends ViewPart implements SelectionListener, IRunnableWithProgress {

	private static final Logger LOG = Logger.getLogger(ReportingNavigationView.class);

	/** View ID. */
	public static final String VIEW_ID = ReportingNavigationView.class.getName();

	private IEpLayoutComposite buttonComposite;

	private IEpLayoutComposite parametersGroup;

	//buttons
	private Button runReportButton;

	private Button exportCSVButton;

	private Button exportExcelButton;

	private Button exportPDFButton;

	private IEpLayoutComposite mainComposite;

	private Map<IReport, DataBindingContext> bindingContextMap;

	private DataBindingContext reportBindingContext;

	private IReport currentReport;

	private IReport previousReport;

	private URL currentReportLocation;

	private Label separator;

	private CmReportService birt;

	private SelectionEvent eventSource;

	private Map<String, Object> reportParams;

	private Composite formBody;

	@Override
	public void createPartControl(final Composite parent) {
		FormToolkit toolkit = CorePlugin.getDefault().getFormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		formBody = form.getBody();
		formBody.setLayout(new TableWrapLayout());
		formBody.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

		bindingContextMap = new HashMap<IReport, DataBindingContext>();

		if (birt != null) {
			birt.closeTask();
		}

		mainComposite = CompositeFactory.createGridLayoutComposite(formBody, 1, false);
		mainComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));
		showReportTypeGroup(mainComposite);
	}

	private void showReportTypeGroup(final IEpLayoutComposite parent) {

		final IEpLayoutComposite reportTypeGroup =
			parent.addGroup(ReportingMessages.reportTypeRequired, 1, false,
					parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		reportTypeGroup.addLabelBoldRequired(ReportingMessages.reportType, EpState.READ_ONLY,
				reportTypeGroup.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER));

		final CCombo reportCombo = reportTypeGroup.addComboBox(EpState.EDITABLE,
				reportTypeGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, true));

		int comboIndex = 0;
		reportCombo.add(ReportingMessages.selectReportType, comboIndex);

		// Add all the reports to the combo box
		final List<ReportType> allReportTypes = ReportType.getReportTypes();
		for (ReportType reportType : allReportTypes) {
			if (!reportType.getReport().isAuthorized()) {
				continue;
			}
			comboIndex++;
			reportCombo.add(reportType.getName(), comboIndex);
		}

		reportCombo.setVisibleItemCount(reportCombo.getItemCount());

		reportCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(final SelectionEvent event) {

				disposeNonNullComponents();

				int selectionIndex = reportCombo.getSelectionIndex();
				if (selectionIndex != 0) {
					previousReport = currentReport;
					String selectedText = reportCombo.getText();
					ReportType selectedReportType = ReportType.findReportTypeByName(selectedText);
					currentReport = selectedReportType.getReport();

					currentReportLocation = selectedReportType.getFileLocation();
					showParameters(parent, currentReport);
					separator = parent.addHorizontalSeparator(
						parent.createLayoutData(
						IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));
					showButtons(parent);
					currentReport.bindControls(EpControlBindingProvider.getInstance(), bindingContextMap.get(currentReport));
					formBody.layout(true, true);
				}
			}
		});

		reportCombo.select(0);


	}

	private void disposeNonNullComponents() {
		if (reportBindingContext != null) {
			bindingContextMap.remove(currentReport);
			reportBindingContext.dispose();
			reportBindingContext = null;
		}
		if (parametersGroup != null) {
			parametersGroup.getSwtComposite().dispose();
			parametersGroup = null;
		}
		if (buttonComposite != null) {
			buttonComposite.getSwtComposite().dispose();
			buttonComposite = null;
		}
		if (separator != null) {
			separator.dispose();
			separator = null;
		}
	}

	/**
	 * Create and show the Parameters pane for a given report.
	 *
	 * @param report the report for which to show the parameters screen
	 */
	private void showParameters(final IEpLayoutComposite parent, final IReport report) {
		parametersGroup = parent.addGroup(ReportingMessages.parameters, 1, true,
					parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		report.createControl(parent.getFormToolkit(), parametersGroup.getSwtComposite(), getSite());
		reportBindingContext = new DataBindingContext();
		bindingContextMap.put(report, reportBindingContext);

	}

	private void showButtons(final IEpLayoutComposite parent) {
		final int numButtons = 5;
		buttonComposite = parent.addGridLayoutComposite(numButtons, false,
				parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, true, false));

		runReportButton = buttonComposite.addPushButton(ReportingMessages.runReport,
				ReportingImageRegistry.getImage(ReportingImageRegistry.RUN_REPORT),
				EpState.EDITABLE,
				buttonComposite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.CENTER));
		exportCSVButton = buttonComposite.addPushButton(ReportingMessages.emptyString,
				ReportingImageRegistry.getImage(ReportingImageRegistry.EXPORT_CSV),
				EpState.EDITABLE,
				buttonComposite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.CENTER));
		exportExcelButton = buttonComposite.addPushButton(ReportingMessages.emptyString,
				ReportingImageRegistry.getImage(ReportingImageRegistry.EXPORT_EXCEL),
				EpState.EDITABLE,
				buttonComposite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.CENTER));
		exportPDFButton = buttonComposite.addPushButton(ReportingMessages.emptyString,
				ReportingImageRegistry.getImage(ReportingImageRegistry.EXPORT_PDF),
				EpState.EDITABLE,
				buttonComposite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.CENTER));

		setButtonsToolTips();
		addSelectionListeners();
		disableButtons();
	}

	/**
	 * Enables the buttons.
	 *
	 */
	public void enableButtons() {
		runReportButton.setEnabled(true);
		exportCSVButton.setEnabled(true);
		exportExcelButton.setEnabled(true);
		exportPDFButton.setEnabled(true);

	}

	/**
	 * Disables the buttons.
	 */
	public void disableButtons() {
		runReportButton.setEnabled(false);
		exportCSVButton.setEnabled(false);
		exportExcelButton.setEnabled(false);
		exportPDFButton.setEnabled(false);

	}

	private void addSelectionListeners() {
		runReportButton.addSelectionListener(this);
		exportCSVButton.addSelectionListener(this);
		exportExcelButton.addSelectionListener(this);
		exportPDFButton.addSelectionListener(this);

	}

	private void setButtonsToolTips() {
		exportCSVButton.setToolTipText(ReportingMessages.exportCSVTip);
		exportPDFButton.setToolTipText(ReportingMessages.exportPDFTip);
		exportExcelButton.setToolTipText(ReportingMessages.exportExcelTip);
	}

	@Override
	public void setFocus() {
		// nothing

	}

	/**
	 * Not Used.
	 *
	 * @param event the selection event.
	 */
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing

	}

	private void startEngineTask(final Map<String, Object> params, final SelectionEvent event) {
		if (event.getSource() == runReportButton || event.getSource() == exportPDFButton) {
			if (birt == null) {
				birt = (CmReportService) LoginManager.getInstance().getBean("cmReportService"); //$NON-NLS-1$
				birt.initializeTask(false, currentReportLocation, currentReport.getClass().getClassLoader(), params);
			} else if (previousReport != null && !previousReport.getReportTitle().equals(currentReport.getReportTitle())) { //NOPMD
				previousReport = currentReport;
				birt.initializeTask(false, currentReportLocation, currentReport.getClass().getClassLoader(), params);
			} else {
				birt.initializeTask(false, currentReportLocation, currentReport.getClass().getClassLoader(), params);
			}
		}
	}

	/**
	 * Invoked when combo box and buttons are selected.
	 *
	 * @param event the selection event
	 */
	public void widgetSelected(final SelectionEvent event) {

		bindingContextMap.get(currentReport).updateModels();
		reportParams = currentReport.getParameters();
		eventSource = event;

		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(getSite()
					.getShell());
			dialog.setCancelable(true);
			dialog.run(true, true, this);
		} catch (InvocationTargetException exception) {
			LOG.error("reporting progress monitor failed", exception); //$NON-NLS-1$
		} catch (InterruptedException exception) {
			LOG.error("Cancelled", exception); //$NON-NLS-1$
		}
	}

	private void cancelMonitor(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return;
		}
	}

	/**
	 * Runs reporting tasks with progress monitor.
	 * @param monitor the progress monitor which is of type IProgressMonitor.UNKNOWN
	 * @throws InvocationTargetException exception
	 * @throws InterruptedException exception
	 */
	public void run(final IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask(ReportingMessages.monitorRunning, IProgressMonitor.UNKNOWN);
		startEngineTask(reportParams, eventSource);
		cancelMonitor(monitor);
		if (eventSource.getSource() == runReportButton) {
			//creates a UI thread that handles creating a view, otherwise it throws an NPE because
			//progress monitor runs on a non UI thread, and non UI thread cannot create UI change
			Display.getDefault().syncExec(new Runnable() {

			public void run() {
				try {
					IWorkbenchPage page = getSite().getPage();
					ReportingView view = (ReportingView) page.showView(ReportingView.REPORTVIEWID, null,
							IWorkbenchPage.VIEW_ACTIVATE);
					view.setReportingViewTitle(currentReport.getReportTitle());
					cancelMonitor(monitor);
					birt.viewHTMLReport(view.getBrowser());
					monitor.done();

				} catch (PartInitException exception) {
					LOG.error("Failed to create reporting view", exception); //$NON-NLS-1$
				}
			}
			});
			return;

		}
		if (eventSource.getSource() == exportPDFButton) {
			cancelMonitor(monitor);
			birt.makePdf();
			monitor.done();
			return;
		}
		if (eventSource.getSource() == exportExcelButton || eventSource.getSource() == exportCSVButton) {

			CmReportService birtExcelOrCsv =
				(CmReportService) LoginManager.getInstance().getBean("cmReportService"); //$NON-NLS-1$
			birtExcelOrCsv.initializeTask(true, currentReportLocation, currentReport.getClass().getClassLoader(), reportParams);
			//if cancel pressed, at least initial the task so it will be faster next time user runs the report
			cancelMonitor(monitor);
			if (eventSource.getSource() == exportExcelButton) {
				birtExcelOrCsv.makeExcel();

			} else {
				birtExcelOrCsv.makeCSV();

			}
			monitor.done();
			return;
		}

	}



}

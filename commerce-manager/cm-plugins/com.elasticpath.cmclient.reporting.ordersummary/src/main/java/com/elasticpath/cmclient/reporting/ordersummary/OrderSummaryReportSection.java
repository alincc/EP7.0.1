/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersummary; //NOPMD -- ExcessiveImports

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.Bundle;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.reporting.AbstractReportSection;
import com.elasticpath.cmclient.reporting.ordersummary.parameters.OrderSummaryParameters;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Represents the UI for the customer registration report parameter section.
 * 
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class OrderSummaryReportSection extends AbstractReportSection {
	
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrderSummaryReportSection.class);

	private CCombo storeCombo;
	
	private IEpDateTimePicker fromDatePicker;

	private IEpDateTimePicker toDatePicker;

	private CCombo currencyCombo;
	
	private CCombo orderSourceCombo;
	
	private final OrderSummaryParameters orderSummaryParameters  = new OrderSummaryParameters();
	
	private final List<String> orderSourceOptionKeys = new ArrayList<String>();
	
	private IEpLayoutComposite orderStatusGroup;
	
	private Composite errorComposite;

	private Composite parentComposite;
	
	private Properties orderSourceProperties;
	
	private static final String ORDER_SOURCE_DIR = "resources/orderSource.properties"; //$NON-NLS-1$
	
	private Map<String, String> dictionary;
	
	private Button isShowExchangeOnly;
	
	private CheckboxTableViewer checkboxTableViewer;
	
	private IEpLayoutComposite parentEpComposite;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	private void getOrderSourceKeys() {
		
		if (orderSourceProperties != null) {
			orderSourceOptionKeys.clear();
		}
		final Bundle bundle = OrderSummaryReportPlugin.getDefault().getBundle();
		URL url = null;
		orderSourceProperties = new Properties();
		List<String> ordering = new ArrayList<String>();
		dictionary = new HashMap<String, String>();
		InputStream urlStream = null;
		
		try {
			url = FileLocator.resolve(FileLocator.find(bundle, new Path(ORDER_SOURCE_DIR), null));
			urlStream = url.openStream();
			orderSourceProperties.load(urlStream);
			for (Iterator<Object> iterator = orderSourceProperties.keySet().iterator(); iterator.hasNext();) {
				String key = iterator.next().toString();
				ordering.add(key);
			}
		} catch (final IOException e) {
			LOG.error("Cannot load order source properties file", e); //$NON-NLS-1$
		} finally {
			IOUtils.closeQuietly(urlStream);
		}
		
		Collections.sort(ordering);
		
		for (String option : ordering) {
			final String unformatedValue = orderSourceProperties.get(option).toString();
			final String orderSourceElement = unformatedValue.substring(0, unformatedValue.indexOf('|'));
			final String orderSourceSQLCriteria = unformatedValue.substring(unformatedValue.indexOf('|') + 1, unformatedValue.length());
			try {
				Field field = OrderSummaryReportMessages.class.getField(orderSourceElement);
				String key = field.get(null).toString();
				orderSourceOptionKeys.add(key);
				dictionary.put(key, orderSourceSQLCriteria);
			} catch (SecurityException e) {
				LOG.error("Security Exception", e); //$NON-NLS-1$
			} catch (NoSuchFieldException e) {
				LOG.error("No Such Field Exception", e); //$NON-NLS-1$
			} catch (IllegalArgumentException e) {
				LOG.error("Illegal Argument Exception", e); //$NON-NLS-1$
			} catch (IllegalAccessException e) {
				LOG.error("Illegal Access Exception", e); //$NON-NLS-1$
			}
		}
	}
	
	
	
	/**
	 * Creates the parameter controls specified by the Report.
	 * 
	 * @param toolkit the top level toolkit which contains the Report configuration pane
	 * @param parent the parent composite which is the container for this specific Report Parameters section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 */
	public void createControl(final FormToolkit toolkit, final Composite parent,
			final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}
		
		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);

		parentComposite = parentEpComposite.getSwtComposite();
		
		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBoldRequired(OrderSummaryReportMessages.store, state, null); 
		storeCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBoldRequired(OrderSummaryReportMessages.fromDate, state, null); 
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(OrderSummaryReportMessages.toDate, state, data); 
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBold(OrderSummaryReportMessages.currency, data); 
		currencyCombo = parentEpComposite.addComboBox(state, data);
		
		parentEpComposite.addLabelBold(OrderSummaryReportMessages.orderSource, data); 
		orderSourceCombo = parentEpComposite.addComboBox(state, data);
		
		isShowExchangeOnly = parentEpComposite.addCheckBoxButton(OrderSummaryReportMessages.exchangeOnly, state, data);
		
		final IEpLayoutComposite epErrorComposite = parentEpComposite.addTableWrapLayoutComposite(2, false,
				parentEpComposite.createLayoutData(IEpLayoutData.CENTER,
						IEpLayoutData.FILL, true, false));
		errorComposite = epErrorComposite.getSwtComposite();
		errorComposite.setVisible(false);
		
		epErrorComposite.addImage(OrderSummaryReportImageRegistry.getImage(OrderSummaryReportImageRegistry.IMAGE_ERROR_SMALL), null);

		// adding a wrapping label
		controlFactory.formToolkitCreateLabel(epErrorComposite.getFormToolkit(),
			epErrorComposite.getSwtComposite(),
			OrdersByStatusReportMessages.checkBoxNoneSelectedError,
			SWT.WRAP & SWT.BOLD, CmClientResources.COLOR_RED)
		);
		
		orderStatusGroup = parentEpComposite.addGroup(OrderSummaryReportMessages.orderStatusGroupHeader, 1, false,
				null);

		final IEpLayoutData groupData = orderStatusGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		checkboxTableViewer = orderStatusGroup.addCheckboxTableViewer(state, groupData, false);
		checkboxTableViewer.getTable().setHeaderVisible(false);
		checkboxTableViewer.getTable().setLinesVisible(false);
		checkboxTableViewer.getTable().setBackgroundMode(SWT.INHERIT_FORCE);
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		checkboxTableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				OrderStatus orderStatus = (OrderStatus) element;
				return OrderSummaryReportMessages.getLocalizedName(orderStatus);
			}
		});
		checkboxTableViewer.setInput(OrderStatus.values());
		checkboxTableViewer.setSorter(new ViewerSorter() {
		});

		populateControls();
	}
	
	
	/**
	 * Binds inputs to controls.
	 * 
	 * @param bindingProvider the binding provider
	 * @param context the data binding context
	 */
	public void bindControlsInternal(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
			final boolean hideDecorationOnFirstValidation = true;
		
			// store binding
			final ObservableUpdateValueStrategy storeUpdateStrategy = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					Store store = (Store) storeCombo.getData(storeCombo.getText());
					if (store == null) {
						orderSummaryParameters.setStore(null);
					} else {
						orderSummaryParameters.setStore(store.getName());
					}
					updateButtonsStatus();
					return Status.OK_STATUS;
				}
			};
			bindingProvider.bind(context, storeCombo, null, null, storeUpdateStrategy,
					hideDecorationOnFirstValidation);

		bindDates(context);

			// currency binding
			final ObservableUpdateValueStrategy currencyUpdateStrategy = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					Currency currency = (Currency) currencyCombo.getData(currencyCombo.getText());
					orderSummaryParameters.setCurrency(currency);
					return Status.OK_STATUS;
				}
			};
		bindingProvider.bind(context, currencyCombo, null, null, currencyUpdateStrategy, hideDecorationOnFirstValidation);

		// others
		bindingProvider.bind(context, orderSourceCombo, orderSummaryParameters,
				"orderSource", null, null, hideDecorationOnFirstValidation); //$NON-NLS-1$
			bindingProvider.bind(context, isShowExchangeOnly, orderSummaryParameters,
					"showExchangeOnly", null, null, hideDecorationOnFirstValidation); //$NON-NLS-1$

		addSelectionListeners();
	}

	private void bindDates(final DataBindingContext context) {
		// from-to date interbinding for from before to date validation
		final ModifyListener updateModels = new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				context.updateModels(); // re-validate bound events
				updateButtonsStatus();
			}
		};
		fromDatePicker.getSwtText().addModifyListener(updateModels);
		toDatePicker.getSwtText().addModifyListener(updateModels);


		fromDatePicker.bind(context, EpValidatorFactory.DATE_TIME_REQUIRED, orderSummaryParameters, "startDate"); //$NON-NLS-1$

		IValidator toValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME_REQUIRED,
				EpValidatorFactory.createToDateValidator(fromDatePicker, toDatePicker) });
		toDatePicker.bind(context, toValidator, orderSummaryParameters, "endDate"); //$NON-NLS-1$
	}
	
	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		StoreService storeService = (StoreService) LoginManager.getInstance().getBean(ContextIdNames.STORE_SERVICE);
		List<Store> stores = storeService.findAllCompleteStores();
		for (Store store : stores) {
			if (AuthorizationService.getInstance().isAuthorizedForStore(store)) {
				this.storeCombo.setData(store.getName(), store);
				this.storeCombo.add(store.getName());
			}
		}

		storeCombo.setText(OrderSummaryReportMessages.allStores);
		toDatePicker.setDate(new Date());
		
		getOrderSourceKeys();
		
		orderSourceCombo.setItems(orderSourceOptionKeys.toArray(new String[orderSourceOptionKeys.size()]));
		orderSourceCombo.select(0);
		
		checkboxTableViewer.setChecked(OrderStatus.COMPLETED, true);
		updateParameters();
	}

	/**
	 * Returns whether the user is authorized to view the Report.
	 *
	 * @return <code>true</code> if the user authorized to view the Report, <code>false</code> otherwise
	 */
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(OrderSummaryReportPermissions.REPORTING_ORDER_SUMMARY_MANAGE);
	}
	
	private void addSelectionListeners() {
		storeCombo.addSelectionListener(new SelectionAdapter() {
			/*
			 * Refreshes All currencies when sore is selected.
			 */
			@Override
			public void widgetSelected(final SelectionEvent event) {
				currencyCombo.removeAll();
				Store store = (Store) storeCombo.getData(storeCombo.getText());
				if (store != null) {
					Collection<Currency> supportedCurrencies = store.getSupportedCurrencies();
					for (Currency currency : supportedCurrencies) {
						currencyCombo.setData(currency.getCurrencyCode(), currency);
						currencyCombo.add(currency.getCurrencyCode());
					}
					currencyCombo.select(0);
				}
			}
		});

		checkboxTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				updateParameters();
				updateButtonsStatus();
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void updateParameters() {
		orderSummaryParameters.setTitle(generateOrderStatuses());
		orderSummaryParameters.setCheckedOrderStatuses((List) Arrays.asList(checkboxTableViewer.getCheckedElements()));
	}
	
	/**
	 * Gets the report's parameters and stores them in a map.
	 * 
	 * @return map that stores parameter keys and values
	 */
	public Map<String, Object> getParameters() {
		
		Map<String, Object> paramsMap = new LinkedHashMap<String, Object>();
		
		paramsMap.put("store", orderSummaryParameters.getStore()); //$NON-NLS-1$
		paramsMap.put("isShowExchangeOnly", orderSummaryParameters.getExchangeString()); //$NON-NLS-1$
		paramsMap.put("startDate", orderSummaryParameters.getStartDate()); //$NON-NLS-1$
		paramsMap.put("endDate", orderSummaryParameters.getEndDate()); //$NON-NLS-1$
		paramsMap.put("currency", orderSummaryParameters.getCurrency().getCurrencyCode()); //$NON-NLS-1$
		
		/*
		 * FIXME: Simplify Logic using get CCombo.setData(String, Option)
		 * Remove dictionary field
		 */
		int index = Integer.valueOf(orderSummaryParameters.getOrderSource());
		String option = orderSourceOptionKeys.get(index);
		String orderSourceParamValue = dictionary.get(option);
		String orderSourceTitleValue = orderSourceParamValue;
		if ("null".equalsIgnoreCase(orderSourceParamValue)) { //$NON-NLS-1$
			orderSourceTitleValue = option;
		}
		paramsMap.put("orderSourceParam", orderSourceParamValue); //$NON-NLS-1$
		paramsMap.put("orderSource", orderSourceTitleValue); //$NON-NLS-1$
		
		paramsMap.put("checkedStatuses", orderSummaryParameters.getCheckedOrderStatuses()); //$NON-NLS-1$
		paramsMap.put("internationalizedStatuses", orderSummaryParameters.getTitle()); //$NON-NLS-1$

		return paramsMap;
	}

	/**
	 * Gets the title of the report.
	 * @return String the title of the report
	 */
	public String getReportTitle() {
		return OrderSummaryReportMessages.reportTitle;
	}
	
	@Override
	public boolean isInputValid() {
		boolean storeSelected = storeCombo.getSelectionIndex() >= 0;
		return storeSelected && isAnyStatusChecked() && super.isInputValid();
	}

	private boolean isAnyStatusChecked() {
		return checkboxTableViewer.getCheckedElements().length != 0;
	}

	@Override
	public void refreshLayout() {
		if (isAnyStatusChecked()) {
			errorComposite.setVisible(false);
			parentComposite.getParent().layout();
		} else {
			errorComposite.setVisible(true);
			parentComposite.getParent().layout();
		}
	}

	/*
	 * This method should generate order return statuses internationalized header.
	 */
	private String generateOrderStatuses() {
		StringBuffer internationalizedStatuses = new StringBuffer();
		if (!isInputValid()) {
			return ""; //$NON-NLS-1$
		}
		Object[] statuses =  checkboxTableViewer.getCheckedElements();
		for (Object object : statuses) {
			OrderStatus status = (OrderStatus) object;
			internationalizedStatuses.append(OrderSummaryReportMessages.getLocalizedName(status)); 
			internationalizedStatuses.append(", "); //$NON-NLS-1$
		}
		internationalizedStatuses.deleteCharAt(internationalizedStatuses.length() - 2);
		return internationalizedStatuses.toString();
	}
	
}

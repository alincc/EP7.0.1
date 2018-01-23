/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.ordersbystatus;

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
import com.elasticpath.cmclient.reporting.ordersbystatus.parameters.OrdersByStatusParameters;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Represents the UI for the Order Details report parameter section.
 */
public class OrdersByStatusReportSection extends AbstractReportSection {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrdersByStatusReportSection.class);

	private IEpDateTimePicker fromDatePicker;
	private IEpDateTimePicker toDatePicker;

	private CCombo storeCombo;
	private CCombo currencyCombo;
	private CCombo orderSourceCombo;
	private Button isShowExchangeOnly;

	private final List<String> orderSourceOptionKeys = new ArrayList<String>();
	private Properties orderSourceProperties;
	private static final String ORDER_SOURCE_DIR = "resources/orderSource.properties"; //$NON-NLS-1$
	private Map<String, String> dictionary;

	private final OrdersByStatusParameters controlParameters = OrdersByStatusParameters.getInstance();

	private IEpLayoutComposite orderStatusGroup;
	private IEpLayoutComposite errorEpComposite;
	private IEpLayoutComposite parentEpComposite;
	private CheckboxTableViewer checkboxTableViewer;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	private void getOrderSourceKeys() {
		if (orderSourceProperties != null) {
			orderSourceOptionKeys.clear();
		}
		final Bundle bundle = ReportingOrdersByStatusPlugin.getDefault().getBundle();
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
				Field field = OrdersByStatusReportMessages.class.getField(orderSourceElement);
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
	public void createControl(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}

		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);

		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBoldRequired(OrdersByStatusReportMessages.store, state, null);
		storeCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBoldRequired(OrdersByStatusReportMessages.fromDate, state, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBoldRequired(OrdersByStatusReportMessages.toDate, state, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);

		parentEpComposite.addLabelBold(OrdersByStatusReportMessages.currency, data);
		currencyCombo = parentEpComposite.addComboBox(state, data);

		parentEpComposite.addLabelBold(OrdersByStatusReportMessages.orderSource, data);
		orderSourceCombo = parentEpComposite.addComboBox(state, data);

		isShowExchangeOnly = parentEpComposite.addCheckBoxButton(OrdersByStatusReportMessages.exchangeOnly, state, data);

		errorEpComposite = parentEpComposite.addTableWrapLayoutComposite(2, false, parentEpComposite.createLayoutData(IEpLayoutData.CENTER,
				IEpLayoutData.FILL, true, false));
		errorEpComposite.getSwtComposite().setVisible(false);

		errorEpComposite.addImage(OrdersByStatusReportImageRegistry.getImage(OrdersByStatusReportImageRegistry.IMAGE_ERROR_SMALL), null);

		// adding a wrapping label
		controlFactory.formToolkitCreateLabel(errorEpComposite.getFormToolkit(),
			errorEpComposite.getSwtComposite(),
			OrdersByStatusReportMessages.checkBoxNoneSelectedError,
			SWT.WRAP & SWT.BOLD, CmClientResources.COLOR_RED)
		);

		orderStatusGroup = parentEpComposite.addGroup(OrdersByStatusReportMessages.orderStatusGroupHeader, 1, false, null);

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
				return OrdersByStatusReportMessages.getLocalizedName(orderStatus);
			}
		});

		Collection<OrderStatus> orderStatuses = new ArrayList<OrderStatus>();
		orderStatuses.addAll(OrderStatus.values());
		orderStatuses.remove(OrderStatus.FAILED);
		checkboxTableViewer.setInput(orderStatuses);
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
	protected void bindControlsInternal(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		final boolean hideDecorationOnFirstValidation = true;

		bindStores(bindingProvider, context, hideDecorationOnFirstValidation);
		bindDates(context);
		bindCurrency(bindingProvider, context, hideDecorationOnFirstValidation);
		bindOrderSource(bindingProvider, context, hideDecorationOnFirstValidation);

		// store and order status listeners
		addSelectionListeners();

		// Show Exchanges only
		bindingProvider.bind(context, isShowExchangeOnly, controlParameters,
				"showExchangeOnly", null, null, hideDecorationOnFirstValidation); //$NON-NLS-1$
	}

	private void bindOrderSource(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy sourceUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String orderSourceOption = orderSourceOptionKeys.get((Integer) newValue);
				controlParameters.setOrderSource(dictionary.get(orderSourceOption));
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, orderSourceCombo, null, null, sourceUpdateStrategy, hideDecorationOnFirstValidation);
	}

	private void bindCurrency(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy currencyUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Currency currency = (Currency) currencyCombo.getData(currencyCombo.getText());
				controlParameters.setCurrency(currency);
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, currencyCombo, null, null, currencyUpdateStrategy, hideDecorationOnFirstValidation);
	}

	private void bindStores(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy storeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Store store = (Store) storeCombo.getData(storeCombo.getText());
				if (store == null) {
					controlParameters.setStore(null);
				} else {
					controlParameters.setStore(store.getCode());
				}

				updateButtonsStatus();
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, storeCombo, null, null, storeUpdateStrategy, hideDecorationOnFirstValidation);
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


		fromDatePicker.bind(context, EpValidatorFactory.DATE_TIME_REQUIRED, controlParameters, "startDate"); //$NON-NLS-1$

		IValidator toValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME_REQUIRED,
				EpValidatorFactory.createToDateValidator(fromDatePicker, toDatePicker) });
		toDatePicker.bind(context, toValidator, controlParameters, "endDate"); //$NON-NLS-1$
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateParameters() {
		controlParameters.setTitle(generateOrderStatuses());
		controlParameters.setCheckedOrderStatuses((List) Arrays.asList(checkboxTableViewer.getCheckedElements()));
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
		storeCombo.setText(OrdersByStatusReportMessages.selectStore);

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
		return AuthorizationService.getInstance()
				.isAuthorizedWithPermission(OrdersByStatusReportPermissions.REPORTING_ORDERS_BY_STATUS_MANAGE);
	}

	/**
	 * Gets the report's parameters and stores them in a map.
	 *
	 * @return map that stores parameter keys and values
	 */
	public Map<String, Object> getParameters() {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put(OrdersByStatusParameters.STORE, controlParameters.getStore());
		paramsMap.put(OrdersByStatusParameters.START_DATE, controlParameters.getStartDate());
		paramsMap.put(OrdersByStatusParameters.END_DATE, controlParameters.getEndDate());
		paramsMap.put(OrdersByStatusParameters.CURRENCY, controlParameters.getCurrency());
		paramsMap.put(OrdersByStatusParameters.EXCHANGE_ONLY, controlParameters.getExchangeString());
		paramsMap.put("orderSourceParam", controlParameters.getOrderSource()); //$NON-NLS-1$
		paramsMap.put("checkedStatuses", controlParameters.getCheckedOrderStatuses()); //$NON-NLS-1$
		paramsMap.put("internationalizedStatuses", controlParameters.getTitle()); //$NON-NLS-1$

		return paramsMap;
	}

	/*
	 * This method should generate order return statuses internationalized header.
	 */
	private String generateOrderStatuses() {
		if (!isInputValid()) {
			return ""; //$NON-NLS-1$
		}

		StringBuilder internationalizedStatuses = new StringBuilder();
		Object[] statuses =  checkboxTableViewer.getCheckedElements();
		for (Object object : statuses) {
			OrderStatus status = (OrderStatus) object;
			internationalizedStatuses.append(OrdersByStatusReportMessages.getLocalizedName(status));
			internationalizedStatuses.append(", "); //$NON-NLS-1$
		}
		internationalizedStatuses.deleteCharAt(internationalizedStatuses.length() - 2);
		return internationalizedStatuses.toString();
	}

	private void addSelectionListeners() {
		storeCombo.addSelectionListener(new SelectionAdapter() {
			/*
			 * Refreshes All currencies when store is selected.
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

		// status binding
		checkboxTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				updateParameters();
				updateButtonsStatus();
			}
		});
	}

	/**
	 * Gets the title of the report.
	 *
	 * @return String the title of the report
	 */
	public String getReportTitle() {
		return OrdersByStatusReportMessages.reportTitle;
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
			errorEpComposite.getSwtComposite().setVisible(false);
			parentEpComposite.getSwtComposite().getParent().layout();
		} else {
			errorEpComposite.getSwtComposite().setVisible(true);
			parentEpComposite.getSwtComposite().getParent().layout();
		}
	}
}

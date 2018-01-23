/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.returnsandexchanges;

import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

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
import com.elasticpath.cmclient.reporting.returnsandexchanges.parameters.ReturnsAndExchangesParameters;
import com.elasticpath.cmclient.reporting.returnsandexchanges.parameters.ReturnsAndExchangesParameters.Controls;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;

/**
 * Represents the UI for the Returns and Exchanges parameter section.
 */
public class ReturnsAndExchangesReportSection extends AbstractReportSection {

	private CCombo storeCombo;
	private CCombo currencyCombo;
	private CCombo warehouseCombo;
	private CCombo returnTypesCombo;

	private IEpDateTimePicker fromDatePicker;
	private IEpDateTimePicker toDatePicker;

	private final ReturnsAndExchangesParameters controlParameters = ReturnsAndExchangesParameters.getInstance();

	private IEpLayoutComposite orderStatusGroup;
	private IEpLayoutComposite parentEpComposite;
	private IEpLayoutComposite errorEpComposite;

	private CheckboxTableViewer checkboxTableViewer;
	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	@Override
	public void createControl(final FormToolkit toolkit, final Composite parent, final IWorkbenchPartSite site) {
		EpState state = EpState.EDITABLE;
		if (!isAuthorized()) {
			state = EpState.DISABLED;
		}

		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);

		final IEpLayoutData data = parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		parentEpComposite.addLabelBold(ReturnsAndExchangesReportMessages.store, null);
		storeCombo = parentEpComposite.addComboBox(state, data);
		parentEpComposite.addLabelBold(ReturnsAndExchangesReportMessages.currency, data); 
		currencyCombo = parentEpComposite.addComboBox(state, data);
		parentEpComposite.addLabelBold(ReturnsAndExchangesReportMessages.warehouse, null);
		warehouseCombo = parentEpComposite.addComboBox(state, data);
		parentEpComposite.addLabelBoldRequired(ReturnsAndExchangesReportMessages.fromDate, state, null);
		fromDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);
		parentEpComposite.addLabelBoldRequired(ReturnsAndExchangesReportMessages.toDate, state, data);
		toDatePicker = parentEpComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, state, data);
		parentEpComposite.addLabelBold(ReturnsAndExchangesReportMessages.rmaType, null);
		returnTypesCombo = parentEpComposite.addComboBox(state, data);
		errorEpComposite = parentEpComposite.addTableWrapLayoutComposite(2, false,
				parentEpComposite.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.FILL, true, false));
		errorEpComposite.getSwtComposite().setVisible(false);
		errorEpComposite.addImage(ReturnsAndExchangesReportImageRegistry.getImage(ReturnsAndExchangesReportImageRegistry.IMAGE_ERROR_SMALL), null);

		// adding a wrapping label
		controlFactory.formToolkitCreateLabel(errorEpComposite.getFormToolkit(), errorEpComposite.getSwtComposite(),
			ReturnsAndExchangesReportMessages.checkBoxNoneSelectedError,
			SWT.WRAP & SWT.BOLD, CmClientResources.COLOR_RED
		);

		orderStatusGroup = parentEpComposite.addGroup(ReturnsAndExchangesReportMessages.statusGroupHeader, 1, false, null);
		final IEpLayoutData groupData = orderStatusGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		checkboxTableViewer = orderStatusGroup.addCheckboxTableViewer(state, groupData, false);
		checkboxTableViewer.getTable().setHeaderVisible(false);
		checkboxTableViewer.getTable().setLinesVisible(false);
		checkboxTableViewer.getTable().setBackgroundMode(SWT.INHERIT_FORCE);
		checkboxTableViewer.setContentProvider(new ArrayContentProvider());
		checkboxTableViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				OrderReturnStatus orderReturnStatus = (OrderReturnStatus) element;
				return ReturnsAndExchangesReportMessages.getLocalizedOrderReturnStatus(orderReturnStatus);
			}
		});
		checkboxTableViewer.setInput(OrderReturnStatus.values());
		checkboxTableViewer.setSorter(new ViewerSorter() {
		});

		populateControls();
	}

	@Override
	protected void bindControlsInternal(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		final boolean hideDecorationOnFirstValidation = true;
		bindStore(bindingProvider, context, hideDecorationOnFirstValidation);
		bindCurrency(bindingProvider, context, hideDecorationOnFirstValidation);
		bindWarehouse(bindingProvider, context, hideDecorationOnFirstValidation);
		bindDates(context);
		bindReturnType(bindingProvider, context, hideDecorationOnFirstValidation);
		bindOrderStatus();

		// store control listener
		addSelectionListeners();
	}

	private void bindOrderStatus() {
		checkboxTableViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				updateParameters();
				updateButtonsStatus();
			}
		});
	}

	private void bindReturnType(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy rmaTypeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				OrderReturnType orderReturnType = (OrderReturnType) returnTypesCombo.getData(returnTypesCombo.getText());
				controlParameters.setRmaType(orderReturnType);
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, returnTypesCombo, null, null, rmaTypeUpdateStrategy, hideDecorationOnFirstValidation);
	}

	private void bindWarehouse(final EpControlBindingProvider bindingProvider,
			final DataBindingContext context,
			final boolean hideDecorationOnFirstValidation) {
		final ObservableUpdateValueStrategy warehouseUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Warehouse warehouse = (Warehouse) warehouseCombo.getData(warehouseCombo.getText());
				controlParameters.setWarehouse(warehouse);
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, warehouseCombo, null, null, warehouseUpdateStrategy, hideDecorationOnFirstValidation);
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

	private void bindStore(final EpControlBindingProvider bindingProvider, final DataBindingContext context,
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
	
	private void addSelectionListeners() {
		storeCombo.addSelectionListener(new SelectionAdapter() {
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
	}

	/**
	 * Populates the controls.
	 */
	protected void populateControls() {
		//store
		StoreService storeService = (StoreService) LoginManager.getInstance().getBean(ContextIdNames.STORE_SERVICE);
		List<Store> stores = storeService.findAllCompleteStores();
		storeCombo.setData(ReturnsAndExchangesReportMessages.allStore, null);
		storeCombo.add(ReturnsAndExchangesReportMessages.allStore);
		for (Store store : stores) {
			if (AuthorizationService.getInstance().isAuthorizedForStore(store)) {
				storeCombo.setData(store.getName(), store);
				storeCombo.add(store.getName());
			}
		}
		storeCombo.select(0);
		
		// warehouse
		warehouseCombo.setData(ReturnsAndExchangesReportMessages.allWarehouse, null);
		warehouseCombo.add(ReturnsAndExchangesReportMessages.allWarehouse);
		
		WarehouseService warehouseService = (WarehouseService) LoginManager.getInstance().getBean(ContextIdNames.WAREHOUSE_SERVICE);
		List<Warehouse> warehouses = warehouseService.findAllWarehouses();
		for (Warehouse warehouse : warehouses) {
			if (AuthorizationService.getInstance().isAuthorizedForWarehouse(warehouse)) {
				warehouseCombo.setData(warehouse.getName(), warehouse);				
				warehouseCombo.add(warehouse.getName());
			}
		}
		warehouseCombo.select(0);

		// endDate
		toDatePicker.setDate(new Date());

		// rmaType
		this.returnTypesCombo.setData(ReturnsAndExchangesReportMessages.returnsAndExchanges, null);
		this.returnTypesCombo.add(ReturnsAndExchangesReportMessages.returnsAndExchanges);
		this.returnTypesCombo.setData(ReturnsAndExchangesReportMessages.returnsOnly, OrderReturnType.RETURN);
		this.returnTypesCombo.add(ReturnsAndExchangesReportMessages.returnsOnly);
		this.returnTypesCombo.setData(ReturnsAndExchangesReportMessages.exchangesOnly, OrderReturnType.EXCHANGE);
		this.returnTypesCombo.add(ReturnsAndExchangesReportMessages.exchangesOnly);
		returnTypesCombo.select(0);

		// orderReturnStatuses
		for (OrderReturnStatus orderReturnStatus : OrderReturnStatus.values()) {
			checkboxTableViewer.setChecked(orderReturnStatus, true);
		}
		updateParameters();
	}

	@Override
	public boolean isAuthorized() {
		return AuthorizationService.getInstance().
				isAuthorizedWithPermission(ReturnsAndExchangesReportPermissions.REPORTING_RETURNS_AND_EXCHANGES_MANAGE);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateParameters() {
		controlParameters.setTitle(generateOrderReturnStatuses());
		controlParameters.setCheckedOrderStatuses((List) Arrays.asList(checkboxTableViewer.getCheckedElements()));
	}

	@Override
	public Map<String, Object> getParameters() {
		Map<String, Object> paramsMap = new HashMap<String, Object>();

		if (controlParameters.getWarehouse() == null) {
			paramsMap.put(Controls.WAREHOUSE.name(), "All Warehouses"); //$NON-NLS-1$
		} else {
			paramsMap.put(Controls.WAREHOUSE.name(), controlParameters.getWarehouse().getName());
		}
		paramsMap.put(Controls.START_DATE.name(), controlParameters.getStartDate());
		paramsMap.put(Controls.END_DATE.name(), controlParameters.getEndDate());

		if (controlParameters.getRmaType() == null) {
			paramsMap.put(Controls.RMA_TYPE.name(), null);
		} else {
			paramsMap.put(Controls.RMA_TYPE.name(), controlParameters.getRmaType().name());
		}

		paramsMap.put(Controls.CHECKED_STATUSES.name(), controlParameters.getCheckedOrderReturnStatuses());
		paramsMap.put(Controls.TITLE.name(), controlParameters.getTitle());
		return paramsMap;
	}

	/*
	 * This method should generate order return statuses internationalized header.
	 */
	private String generateOrderReturnStatuses() {
		if (!isInputValid()) {
			return ""; //$NON-NLS-1$
		}
		StringBuffer internationalizedStatuses = new StringBuffer();
		Object[] statuses =  checkboxTableViewer.getCheckedElements();
		for (Object object : statuses) {
			OrderReturnStatus status = (OrderReturnStatus) object;
			internationalizedStatuses.append(ReturnsAndExchangesReportMessages.getLocalizedOrderReturnStatus(status)); 
			internationalizedStatuses.append(", "); //$NON-NLS-1$
		}
		internationalizedStatuses.deleteCharAt(internationalizedStatuses.length() - 2);
		return internationalizedStatuses.toString();
	}

	@Override
	public String getReportTitle() {
		return ReturnsAndExchangesReportMessages.reportTitle;
	}

	@Override
	public boolean isInputValid() {
		return isAnyStatusChecked() && super.isInputValid();
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

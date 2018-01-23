package com.elasticpath.cucumber.definitions;

import java.util.Map;
import java.util.UUID;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import com.elasticpath.selenium.dialogs.CreateWarehouseDialog;
import com.elasticpath.selenium.framework.util.SeleniumDriverSetup;
import com.elasticpath.selenium.resultspane.WarehousesPane;
import com.elasticpath.selenium.toolbars.ConfigurationActionToolbar;

/**
 * Warehouse step definitions.
 */
public class WarehouseDefinition {
	private final ConfigurationActionToolbar configurationActionToolbar;
	private WarehousesPane warehousesPane;
	private CreateWarehouseDialog createWarehouseDialog;
	private String warehouseName;
	private static final int UID_END_INDEX = 5;

	/**
	 * Constructor.
	 */
	public WarehouseDefinition() {
		configurationActionToolbar = new ConfigurationActionToolbar(SeleniumDriverSetup.getDriver());
	}

	/**
	 * Click user roles.
	 */
	@When("^I go to Warehouses$")
	public void clickUserRoles() {
		warehousesPane = configurationActionToolbar.clickWarehouses();
	}

	/**
	 * Create warehouse.
	 *
	 * @param warehouseMap the warehouse map.
	 */
	@When("^I create warehouse with following values$")
	public void createWarehouse(final Map<String, String> warehouseMap) {
		createWarehouseDialog = warehousesPane.clickCreateWarehouseButton();

		String warehouseCode = "wh" + UUID.randomUUID().toString().substring(0, UID_END_INDEX);
		this.warehouseName = warehouseMap.get("warehouse name") + "-" + warehouseCode;
		createWarehouseDialog.enterWarehouseCode(warehouseCode);
		createWarehouseDialog.enterWarehouseName(this.warehouseName);
		createWarehouseDialog.enterAddressLine1(warehouseMap.get("address line 1"));
		createWarehouseDialog.enterCity(warehouseMap.get("city"));
		createWarehouseDialog.selectCountry(warehouseMap.get("country"));
		createWarehouseDialog.selectState(warehouseMap.get("state"));
		createWarehouseDialog.enterZip(warehouseMap.get("zip"));
		createWarehouseDialog.clickSaveButton();
	}

	/**
	 * Delete new warehouse.
	 */
	@And("^I delete newly created warehouse$")
	public void deleteNewWarehouse() {
		warehousesPane.deleteWarehouse(this.warehouseName);
	}

	/**
	 * Verify new ware house no longer exists.
	 */
	@Then("^newly created warehouse no longer exists$")
	public void verifyNewWarehouseIsDeleted() {
		warehousesPane.verifyWarehouseIsNotInList(this.warehouseName);
	}

}

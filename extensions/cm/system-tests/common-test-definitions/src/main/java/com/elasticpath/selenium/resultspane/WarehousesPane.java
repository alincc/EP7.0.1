package com.elasticpath.selenium.resultspane;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.elasticpath.selenium.common.AbstractPageObject;
import com.elasticpath.selenium.dialogs.ConfirmDialog;
import com.elasticpath.selenium.dialogs.CreateWarehouseDialog;

/**
 * Warehouse pane.
 */
public class WarehousesPane extends AbstractPageObject {

	private static final String WAREHOUSE_LIST_PARENT_CSS = "div[widget-id='Warehouse List'][widget-type='Table'] ";
	private static final String WAREHOUSE_LIST_CSS = WAREHOUSE_LIST_PARENT_CSS + "div[column-id='%s']";
	private static final String CREATE_WAREHOUSE_BUTTON_CSS = "div[widget-id='Create Warehouse'][seeable='true']";
	private static final String DELETE_WAREHOUSE_BUTTON_CSS = "div[widget-id='Delete Warehouse'][seeable='true']";

	/**
	 * Constructor.
	 *
	 * @param driver WebDriver which drives this page.
	 */
	public WarehousesPane(final WebDriver driver) {
		super(driver);
	}

	/**
	 * Verifies if warehouse exists.
	 *
	 * @param warehouseName String
	 */
	public void verifyWarehouseExists(final String warehouseName) {
		assertThat(selectItemInCenterPaneWithoutPagination(WAREHOUSE_LIST_PARENT_CSS, WAREHOUSE_LIST_CSS, warehouseName, "Warehouse Name"))
				.as("Warehouse does not exist in the list - " + warehouseName)
				.isTrue();
	}

	/**
	 * Verifies warehouse is not in the list.
	 *
	 * @param warehouseName String
	 */
	public void verifyWarehouseIsNotInList(final String warehouseName) {
		getWaitDriver().adjustWaitInterval(1);
		assertThat(selectItemInCenterPaneWithoutPagination(WAREHOUSE_LIST_PARENT_CSS, WAREHOUSE_LIST_CSS, warehouseName, "Warehouse Name"))
				.as("Delete failed, warehouse does is still in the list - " + warehouseName)
				.isFalse();
		getWaitDriver().adjustWaitBackToDefault();
	}

	/**
	 * Selects and deletes the warehouse.
	 *
	 * @param warehouseName String
	 */
	public void deleteWarehouse(final String warehouseName) {
		verifyWarehouseExists(warehouseName);
		clickDeleteWarehouseButton();
		new ConfirmDialog(getDriver()).clickOKButton();
	}

	/**
	 * Clicks Create Warehouse button.
	 *
	 * @return WarehousePane
	 */
	public CreateWarehouseDialog clickCreateWarehouseButton() {
		getWaitDriver().waitForPageLoad();
		getDriver().findElement(By.cssSelector(CREATE_WAREHOUSE_BUTTON_CSS)).click();
		return new CreateWarehouseDialog(getDriver());
	}

	/**
	 * Clicks Delete Warehouse button.
	 */
	public void clickDeleteWarehouseButton() {
		getDriver().findElement(By.cssSelector(DELETE_WAREHOUSE_BUTTON_CSS)).click();
		getWaitDriver().waitForPageLoad();
	}

}

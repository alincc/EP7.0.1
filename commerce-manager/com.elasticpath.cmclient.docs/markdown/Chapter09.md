# Chapter 10: Shipping/Receiving

Shipping/Receiving allows users to complete shipments. It is accessible from the main tool bar:

![](images/Ch09-01.png)

## Shipment Workflow Process

![](images/Appendix_A-Ch10-Shipment_States.png)

## Importing Inventory

Authorized users can increase the stock of existing products by importing inventory data from comma-separated value (CSV) files. This is an alternative to manually updating inventory levels for multiple products, especially if you have an external system to manage your inventory and you need to be able to import a large number of inventory changes in a batch.

Before you can import inventory, you must create a warehouse import job. The import job describes the column structure of the import data files and how those columns are mapped to the data fields.

> **Note:** CSV files may use characters other than commas to separate values if specified in Elastic Path Commerce's Configuration Settings.

### Creating a Warehouse Import Job

1. Create a CSV template file that describes the structure of the data you want to import.

    - This file must contain one line of comma-separated values.
    - Each value should be either the column name or a description of the data in the column.

    For example, the CSV files that you need to import are structured as follows:

    `<product sku>,<quantity on hand>,<reorder quantity>,<reorder minimum>`

    The line might look similar to the following:

    `Product SKU,Qty on Hand,Reorder Qty,Reorder Minimum`

2. In the main toolbar, click the **Shipping/Receiving** icon.

3. Click the **View Warehouse Import Jobs** icon in the main toolbar, on the right hand side.  The **Import Jobs** tab opens in the upper right panel.

    ![](images/Ch09-05.png)

4. In the **Import Jobs** tab toolbar click the **Create Import Job** button.

    ![](images/Ch09-06.png)

5. Configure the settings as follows:

    | Field | Description |
    | --- | --- |
    | **Warehouse** | The warehouse whose inventory needs to be updated. |
    | **Data Type** | This must be set to _Inventory_. |
    | **Import Type** | Select one of the following options: <br/><br/> - **Insert &amp; Replace**: inserts inventory data for the product SKUs specified in the data file. If there is already inventory data for a SKU, it is overwritten with the data that is being imported. <br/>- **Update**: updates all inventory data for the product SKUs specified in the data file. <br/>- **Insert**: inserts inventory data for the product SKUs specified in the data file. If there is already inventory data for a SKU, the data is not imported. <br/>- **Delete**: deletes all inventory data for the product SKUs specified in the data file. <br/>- **Clear then Insert**: clears data for the product SKUs specified then inserts inventory data for the product SKUs. |
    | **Import Name** | The name to display in the list of warehouse import jobs. |
    | **CSV Import File** | The CSV import file that was created in step 1. |
    | **Column Delimiter** | The character that separates each value in a line. All text values must begin and end with the text delimiter character. |
    | **Text Delimiter** | The character that surrounds each text value in a line. |
    | **Max errors during import (default)** | The number of non-fatal errors that can occur before an import of this type is terminated. |

6. Click **Next**. Elastic Path Commerce will verify that the CSV file contains valid data and report any errors. If there are any errors, correct them and click the **Back** button to repeat this step.

7. In the _Map Data Fields_ screen, you must specify how the data in the CSV file is mapped to inventory fields. For each data field to be mapped, select the field in the **Data Fields** list, select the appropriate column in the **CSV Columns** list, and click **Map**.

    ![](images/Ch09-07.png)

    > **Note:** It is not necessary to map all data fields to all CSV columns. Only the _productSku_ field must be mapped.

8. Click **Finish**.

### Executing a Warehouse Import Job

1. In the main toolbar, click the **Shipping/Receiving** icon.

2. Click the **View Warehouse Import Jobs** icon in the main toolbar, on the right hand side.

    ![](images/Ch09-08.png)

3. In the list of jobs displayed in the **Import Jobs** tab, select the job you want to run.  

4. In the **Import Jobs** tab toolbar click the **Run Import Job** button.

    ![](images/Ch09-09.png)

5. Click the browse button next to the **CSV Import File** box and locate the CSV file that contains the inventory data you want to import.

6. Click the **Next** button. Elastic Path Commerce verifies that the CSV file contains valid data and reports any errors. Verification may take a few minutes, depending on the amount of data in the file. If there are any errors, correct them and click the **Back** button to repeat this step.

7. Click the **Finish** button. The import may take a few minutes, depending on the amount of data in the file.

## Completing a Shipment

After the package has shipped, shipping personnel must complete the shipment in Elastic Path Commerce. Retailers will not receive payment for the transaction until the shipment has been completed (unless the store has been configured for collection of payment at the time of purchase.)

1. In the main toolbar, click the **Shipping/Receiving** icon.

2. Click the **Complete Shipment** icon in the main toolbar, on the right hand side.

    ![](images/Ch09-10.png)

3. Enter the **Shipment ID** of the order you want to complete,

4. Click the **Validate** button to the right of the Shipment ID box. If it is a valid shipment ID, you can complete the order.

5. Click **Complete**.

## Switching Warehouses

1. In the main toolbar, click the **Shipping/Receiving** icon.

2. Click the **Switch Warehouse** icon's downward arrow.

3. Select the warehouse you want to switch to.

    ![](images/Ch09-11.png)

## Retrieving SKU Inventory Numbers

1. In the main toolbar, click the **Shipping/Receiving** icon.

    ![](images/Ch09-12.png)

2. Either enter a SKU Code or click the icon next to the **SKU Code** field to display a dialog box for selecting valid SKU Code.

    ![](images/Ch09-13.png)

3. Click the **Retrieve** button.

    ![](images/Ch09-14.png)

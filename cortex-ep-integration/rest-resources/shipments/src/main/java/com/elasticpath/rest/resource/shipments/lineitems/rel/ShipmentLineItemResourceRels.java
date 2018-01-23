/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.rel;

import com.elasticpath.rest.resource.shipments.LineItems;

/**
 * Rels for shipment line item resources.
 */
public final class ShipmentLineItemResourceRels {

	/**
	 * Shipment line items REL.
	 */
	public static final String LINE_ITEMS_REL = LineItems.URI_PART;

	/**
	 * Shipment line items REV.
	 */
	public static final String LINE_ITEMS_REV = LINE_ITEMS_REL;

	/**
	 * Shipment line items REL.
	 */
	public static final String LINE_ITEM_REL = "lineitem";

	/**
	 * Shipment line item tax REV.
	 */
	public static final String LINE_ITEM_TAXES_REV = "tax";
}

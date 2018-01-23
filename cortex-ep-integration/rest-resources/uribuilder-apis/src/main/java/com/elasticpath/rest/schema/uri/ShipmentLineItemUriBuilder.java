/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * URI Builder for discounts resource.
 */
public interface ShipmentLineItemUriBuilder extends ReadFromOtherUriBuilder<ShipmentLineItemUriBuilder> {

	/**
	 * Sets the line item id.
	 *
	 * @param lineItemId the line item id
	 * @return this builder
	 */
	ShipmentLineItemUriBuilder setLineItemId(String lineItemId);
	
}
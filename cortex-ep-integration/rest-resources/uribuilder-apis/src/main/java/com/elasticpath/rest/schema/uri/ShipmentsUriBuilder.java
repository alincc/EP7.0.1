/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds a URI to a specific shipment.
 */
public interface ShipmentsUriBuilder extends ReadFromOtherUriBuilder<ShipmentsUriBuilder> {

	/**
	 * Set the shipment ID.
	 * 
	 * @param shipmentId the shipment ID
	 * @return this builder
	 */
	ShipmentsUriBuilder setShipmentId(String shipmentId);

}

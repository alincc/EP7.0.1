/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.dto;

import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Adaptable DTO for shipment details.
 */
public interface ShipmentDetailsDto extends ResourceEntity {

	/**
	 * Gets the order correlation id.
	 *
	 * @return the order correlation id
	 */
	String getOrderCorrelationId();

	/**
	 * Sets the order correlation id.
	 *
	 * @param orderCorrelationId the order correlation id
	 * @return the shipment details dto
	 */
	ShipmentDetailsDto setOrderCorrelationId(String orderCorrelationId);

	/**
	 * Gets the delivery correlation id.
	 *
	 * @return the delivery correlation id
	 */
	String getDeliveryCorrelationId();

	/**
	 * Sets the delivery correlation id.
	 *
	 * @param deliveryCorrelationId the delivery correlation id
	 * @return the shipment details dto
	 */
	ShipmentDetailsDto setDeliveryCorrelationId(String deliveryCorrelationId);
}

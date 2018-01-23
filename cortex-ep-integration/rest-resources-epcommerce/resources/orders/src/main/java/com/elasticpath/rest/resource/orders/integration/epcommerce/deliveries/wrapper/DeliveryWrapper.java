/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.integration.epcommerce.deliveries.wrapper;

import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Wraps a delivery code and shipment type.
 */
public interface DeliveryWrapper extends ResourceEntity {

	/**
	 * Gets the delivery code.
	 *
	 * @return the delivery code
	 */
	String getDeliveryCode();

	/**
	 * Gets the shipment type.
	 *
	 * @return the delivery type
	 */
	String getShipmentType();

	/**
	 * Sets the delivery code.
	 *
	 * @param deliveryCode the delivery code
	 * @return the delivery code
	 */
	DeliveryWrapper setDeliveryCode(String deliveryCode);

	/**
	 * Sets the shipment type.
	 *
	 * @param shipmentType the shipment type
	 * @return the delivery type
	 */
	DeliveryWrapper setShipmentType(String shipmentType);

}

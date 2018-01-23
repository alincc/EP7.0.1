/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration.epcommerce.transform;

import java.util.Locale;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.rest.definition.taxes.TaxesEntity;

/**
 * Transforms an {@link OrderShipment} into a {@link TaxesEntity}.
 */
public interface OrderShipmentTaxTransformer {

	/**
	 * Returns a new {@link TaxesEntity} object based on the given {@link OrderShipment}.
	 *
	 * @param orderShipment the order shipment
	 * @param locale the locale
	 * @return the TaxesEntity
	 */
	TaxesEntity transformToEntity(OrderShipment orderShipment, Locale locale);
}

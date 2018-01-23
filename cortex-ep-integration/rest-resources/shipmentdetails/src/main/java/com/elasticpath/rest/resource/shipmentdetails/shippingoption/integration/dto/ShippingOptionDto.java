/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto;

import java.util.Collection;

import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Adaptable dto.
 */
public interface ShippingOptionDto extends ResourceEntity {

	/**
	 * Gets the correlation id.
	 *
	 * @return the correlation id
	 */
	String getCorrelationId();

	/**
	 * Sets the correlation id.
	 *
	 * @param correlationId the correlation id
	 * @return the {@link ShippingOptionDto}
	 */
	ShippingOptionDto setCorrelationId(String correlationId);

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Sets the name.
	 *
	 * @param name the name
	 * @return the delivery method dto
	 */
	ShippingOptionDto setName(String name);

	/**
	 * Sets the display name.
	 *
	 * @param displayName the display name
	 * @return the delivery method dto
	 */
	ShippingOptionDto setDisplayName(String displayName);

	/**
	 * Gets the display name.
	 *
	 * @return the display name
	 */
	String getDisplayName();

	/**
	 * Sets the carrier.
	 *
	 * @param carrierName the carrier name
	 * @return the delivery method dto
	 */
	ShippingOptionDto setCarrier(String carrierName);

	/**
	 * Gets the carrier.
	 *
	 * @return the carrier
	 */
	String getCarrier();

	/**
	 * Sets the costs.
	 *
	 * @param costs the costs
	 * @return the delivery method dto
	 */
	ShippingOptionDto setCosts(Collection<CostEntity> costs);

	/**
	 * Gets the costs.
	 *
	 * @return the costs
	 */
	Collection<CostEntity> getCosts();
}

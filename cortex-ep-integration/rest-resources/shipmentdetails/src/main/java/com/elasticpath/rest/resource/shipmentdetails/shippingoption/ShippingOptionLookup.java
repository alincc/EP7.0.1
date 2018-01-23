/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption;

import java.util.Collection;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.schema.ResourceState;

/**
 * The Shipping Option Lookup.
 */
public interface ShippingOptionLookup {

	/**
	 * Gets the shipping option.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details Id.
	 * @param shippingOptionId the shiping option id
	 * @return the shipping option
	 */
	ExecutionResult<ResourceState<ShippingOptionEntity>> getShippingOption(String scope, String shipmentDetailsId, String shippingOptionId);

	/**
	 * Gets the shipping option ids for shipment detail.
	 *
	 * @param scope the scope
	 * @param shipmentDetailId the shipment detail id
	 * @return the shipping option ids for shipment detail
	 */
	ExecutionResult<Collection<String>> getShippingOptionIdsForShipmentDetail(String scope, String shipmentDetailId);

	/**
	 * Gets the selected shipment option id for shipment detail.
	 *
	 * @param scope the scope
	 * @param shipmentDetailsId the shipment details id
	 * @return the delivery method
	 */
	ExecutionResult<String> getSelectedShipmentOptionIdForShipmentDetails(String scope, String shipmentDetailsId);

	/**
	 * Checks if shipping destination is selected for shipment detail.
	 *
	 * @param scope the scope
	 * @param shipmentDetailId the shipment detail id
	 * @return the execution result
	 */
	ExecutionResult<Boolean> isShippingDestinationSelectedForShipmentDetail(String scope, String shipmentDetailId);

	/**
	 * Checks if is supported delivery type.
	 *
	 * @param deliveryType the delivery type
	 * @return the execution result
	 */
	ExecutionResult<Boolean> isSupportedDeliveryType(String deliveryType);
}

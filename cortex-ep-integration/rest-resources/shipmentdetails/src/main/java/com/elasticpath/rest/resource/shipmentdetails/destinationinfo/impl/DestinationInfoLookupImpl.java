/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoLookup;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration.DestinationInfoLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.ShipmentDetailsLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.integration.dto.ShipmentDetailsDto;

/**
 * Lookup class for destination info.
 */
@Singleton
@Named("destinationInfoLookup")
public final class DestinationInfoLookupImpl implements DestinationInfoLookup {

	private final DestinationInfoLookupStrategy destinationInfoLookupStrategy;
	private final ShipmentDetailsLookupStrategy shipmentDetailsLookupStrategy;


	/**
	 * Default constructor.
	 *
	 * @param destinationInfoLookupStrategy the destination info lookup strategy
	 * @param shipmentDetailsLookupStrategy the shipment details lookup strategy
	 */
	@Inject
	public DestinationInfoLookupImpl(
			@Named("destinationInfoLookupStrategy")
			final DestinationInfoLookupStrategy destinationInfoLookupStrategy,
			@Named("shipmentDetailsLookupStrategy")
			final ShipmentDetailsLookupStrategy shipmentDetailsLookupStrategy) {

		this.destinationInfoLookupStrategy = destinationInfoLookupStrategy;
		this.shipmentDetailsLookupStrategy = shipmentDetailsLookupStrategy;
	}


	@Override
	public ExecutionResult<String> findSelectedAddressIdForShipment(final String scope, final String shipmentId) {

		ShipmentDetailsDto shipmentDetailsDto = Assign.ifSuccessful(
				shipmentDetailsLookupStrategy.getShipmentDetail(scope, shipmentId));
		String orderId = shipmentDetailsDto.getOrderCorrelationId();
		String addressId = Assign.ifSuccessful(
				destinationInfoLookupStrategy.findSelectedAddressIdForShipment(scope, orderId, shipmentId));
		return ExecutionResultFactory.createReadOK(Base32Util.encode(addressId));
	}
}

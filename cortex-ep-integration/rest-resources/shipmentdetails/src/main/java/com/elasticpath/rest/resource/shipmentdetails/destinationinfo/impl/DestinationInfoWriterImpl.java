/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.DestinationInfoWriter;
import com.elasticpath.rest.resource.shipmentdetails.destinationinfo.integration.DestinationInfoWriterStrategy;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Writer for destination info.
 */
@Singleton
@Named("destinationInfoWriter")
public final class DestinationInfoWriterImpl implements DestinationInfoWriter {

	private final DestinationInfoWriterStrategy destinationInfoWriterStrategy;

	/**
	 * Instantiates a new destination info writer impl.
	 *
	 * @param destinationInfoWriterStrategy the destination info writer strategy
	 */
	@Inject
	public DestinationInfoWriterImpl(
			@Named("destinationInfoWriterStrategy")
			final DestinationInfoWriterStrategy destinationInfoWriterStrategy) {

		this.destinationInfoWriterStrategy = destinationInfoWriterStrategy;
	}


	@Override
	public ExecutionResult<Void> updateShippingAddressForShipment(final String scope, final String shipmentDetailsId,
			final ResourceState<AddressEntity> address) {
		String decodedAddressId = address.getEntity().getAddressId();
		return destinationInfoWriterStrategy.updateShippingAddressForShipment(scope, shipmentDetailsId,
				decodedAddressId);
	}
}

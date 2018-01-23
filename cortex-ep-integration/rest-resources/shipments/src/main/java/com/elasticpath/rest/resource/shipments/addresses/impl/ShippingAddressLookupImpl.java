/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.addresses.ShippingAddressLookup;
import com.elasticpath.rest.resource.shipments.addresses.integration.ShippingAddressLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Lookup for a shipping address.
 */
@Singleton
@Named("shippingAddressLookup")
public final class ShippingAddressLookupImpl implements ShippingAddressLookup {

	private final ShippingAddressLookupStrategy shippingAddressLookupStrategy;

	private final TransformRfoToResourceState<AddressEntity, AddressEntity, ShipmentEntity> shippingAddressTransformer;

	/**
	 * Default constructor.
	 *
	 * @param shippingAddressLookupStrategy the lookup strategy
	 * @param shippingAddressTransformer    the transformer
	 */
	@Inject
	ShippingAddressLookupImpl(
			@Named("shippingAddressLookupStrategy")
			final ShippingAddressLookupStrategy shippingAddressLookupStrategy,
			@Named("shippingAddressTransformer")
			final TransformRfoToResourceState<AddressEntity, AddressEntity, ShipmentEntity>
					shippingAddressTransformer) {

		this.shippingAddressLookupStrategy = shippingAddressLookupStrategy;
		this.shippingAddressTransformer = shippingAddressTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<AddressEntity>> getShippingAddress(
			final ResourceState<ShipmentEntity> shipmentRepresentation) {

		ShipmentEntity shipmentEntity = shipmentRepresentation.getEntity();
		AddressEntity addressEntity = Assign.ifSuccessful(shippingAddressLookupStrategy.getShippingAddress(
				shipmentRepresentation.getScope(), shipmentEntity.getPurchaseId(), shipmentEntity.getShipmentId()));
		ResourceState<AddressEntity> representation =
				shippingAddressTransformer.transform(addressEntity, shipmentRepresentation);

		return ExecutionResultFactory.createReadOK(representation);
	}
}

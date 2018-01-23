/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.integration.epcommerce.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.shipments.addresses.integration.ShippingAddressLookupStrategy;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * DCE implementation of {@link ShippingAddressLookupStrategy}.
 */
@Singleton
@Named("shippingAddressLookupStrategy")
public class ShippingAddressLookupStrategyImpl implements ShippingAddressLookupStrategy {

	private final ShipmentRepository shipmentRepository;
	private final AbstractDomainTransformer<Address, AddressEntity> shippingAddressTransformer;

	/**
	 * Default constructor.
	 *
	 * @param shipmentRepository         the shipment repository
	 * @param shippingAddressTransformer the shipping address transformer
	 */
	@Inject
	public ShippingAddressLookupStrategyImpl(
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository,
			@Named("shippingAddressTransformer")
			final AbstractDomainTransformer<Address, AddressEntity> shippingAddressTransformer) {

		this.shipmentRepository = shipmentRepository;
		this.shippingAddressTransformer = shippingAddressTransformer;
	}

	@Override
	public ExecutionResult<AddressEntity> getShippingAddress(
			final String scope,
			final String purchaseId, final String shipmentId) {

		PhysicalOrderShipment orderShipment = Assign.ifSuccessful(shipmentRepository.find(purchaseId, shipmentId));
		OrderAddress address = Assign.ifNotNull(orderShipment.getShipmentAddress(), OnFailure.returnNotFound("Shipment address not found"));
		AddressEntity data = shippingAddressTransformer.transformToEntity(address);

		return ExecutionResultFactory.createReadOK(data);
	}
}
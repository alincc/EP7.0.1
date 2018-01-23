/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.integration.epcommerce.impl;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.transform.OrderShipmentTaxTransformer;
import com.elasticpath.rest.resource.taxes.shipment.integration.ShipmentTaxesLookupStrategy;

/**
 * Implementation of {@link ShipmentTaxesLookupStrategy} for shipment taxes.
 */
@Singleton
@Named("shipmentTaxesLookupStrategy")
public class ShipmentTaxesLookupStrategyImpl implements ShipmentTaxesLookupStrategy {

	private final ResourceOperationContext resourceOperationContext;
	private final ShipmentRepository shipmentRepository;
	private final OrderShipmentTaxTransformer orderShipmentTaxTransformer;

	/**
	 * Constructor.
	 * 
	 * @param resourceOperationContext the resource operation context
	 * @param shipmentRepository the shipment repository
	 * @param orderShipmentTaxTransformer the orderShipmentTaxTransformer
	 */
	@Inject
	public ShipmentTaxesLookupStrategyImpl(
			@Named("resourceOperationContext") final ResourceOperationContext resourceOperationContext,
			@Named("shipmentRepository") final ShipmentRepository shipmentRepository,
			@Named("orderShipmentTaxTransformer") final OrderShipmentTaxTransformer orderShipmentTaxTransformer) {
		this.resourceOperationContext = resourceOperationContext;
		this.shipmentRepository = shipmentRepository;
		this.orderShipmentTaxTransformer = orderShipmentTaxTransformer;
	}


	@Override
	public ExecutionResult<TaxesEntity> getTaxes(final String decodedPurchaseId, final String decodedShipmentId) {

		PhysicalOrderShipment orderShipment = Assign.ifSuccessful(shipmentRepository
				.find(decodedPurchaseId, decodedShipmentId));

		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());

		TaxesEntity taxesEntity = orderShipmentTaxTransformer.transformToEntity(orderShipment, locale);

		return ExecutionResultFactory.createReadOK(taxesEntity);
	}
	
}

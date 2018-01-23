/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.impl;

import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.resource.taxes.shipment.integration.ShipmentTaxesLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;

/**
 * TaxesLookup.
 */
@Named("shipmentTaxesLookup")
public class ShipmentTaxesLookupImpl implements TaxesLookup<ShipmentEntity> {

	private final ShipmentTaxesLookupStrategy shipmentTaxesLookupStrategy;
	private final TaxesUriBuilderFactory taxesUriBuilderFactory;

	/**
	 * Constructor.
	 *
	 * @param shipmentTaxesLookupStrategy a {@link ShipmentTaxesLookupStrategy}
	 * @param taxesUriBuilderFactory      a {@link TaxesUriBuilderFactory}
	 */
	@Inject
	public ShipmentTaxesLookupImpl(
			@Named("shipmentTaxesLookupStrategy")
			final ShipmentTaxesLookupStrategy shipmentTaxesLookupStrategy,
			@Named("taxesUriBuilderFactory")
			final TaxesUriBuilderFactory taxesUriBuilderFactory) {
		this.shipmentTaxesLookupStrategy = shipmentTaxesLookupStrategy;
		this.taxesUriBuilderFactory = taxesUriBuilderFactory;
	}

	@Override
	public ExecutionResult<ResourceState<TaxesEntity>> getTaxes(final ResourceState<ShipmentEntity> shipment) {

		ShipmentEntity shipmentEntity = shipment.getEntity();
		TaxesEntity taxesEntity = Assign.ifSuccessful(
				shipmentTaxesLookupStrategy.getTaxes(shipmentEntity.getPurchaseId(), shipmentEntity.getShipmentId()));
		ResourceState<TaxesEntity> taxesRepresentation = resourceState(taxesEntity, shipment.getSelf().getUri());

		return ExecutionResultFactory.createReadOK(taxesRepresentation);
	}

	private ResourceState<TaxesEntity> resourceState(final TaxesEntity taxesEntity,
													final String shipmentUri) {
		return ResourceState.Builder
				.create(taxesEntity)
				.withScope(null)
				.withSelf(self(shipmentUri))
				.build();
	}

	private Self self(final String shipmentUri) {
		String taxesUri = taxesUriBuilderFactory.get()
				.setSourceUri(shipmentUri)
				.build();
		return createSelf(taxesUri);
	}
}
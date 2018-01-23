/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.shipment.lineitem.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.taxes.TaxesLookup;
import com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.ShipmentLineItemTaxesLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.TaxesUriBuilderFactory;

/**
 * TaxesLookup.
 */
@Singleton
@Named("shipmentLineItemTaxesLookup")
public class ShipmentLineItemTaxesLookupImpl implements TaxesLookup<ShipmentLineItemEntity> {

	private final ShipmentLineItemTaxesLookupStrategy taxesLookupStrategy;
	private final TaxesUriBuilderFactory taxesUriBuilderFactory;

	/**
	 * Constructor. 
	 *
	 * @param taxesLookupStrategy a {@link com.elasticpath.rest.resource.taxes.shipment.lineitem.integration.ShipmentLineItemTaxesLookupStrategy}
	 * @param taxesUriBuilderFactory {@link TaxesUriBuilderFactory}
	 */
	@Inject
	public ShipmentLineItemTaxesLookupImpl(
			@Named("shipmentLineItemTaxesLookupStrategy")
			final ShipmentLineItemTaxesLookupStrategy taxesLookupStrategy,
			@Named("taxesUriBuilderFactory")
			final TaxesUriBuilderFactory taxesUriBuilderFactory) {
		this.taxesLookupStrategy = taxesLookupStrategy;
		this.taxesUriBuilderFactory = taxesUriBuilderFactory;
	}

	@Override
	public ExecutionResult<ResourceState<TaxesEntity>> getTaxes(final ResourceState<ShipmentLineItemEntity> lineItem) {

		String scope = lineItem.getScope();
		ShipmentLineItemEntity lineItemEntity = lineItem.getEntity();
		String shipmentId = lineItemEntity.getShipmentId();
		String purchaseId = lineItemEntity.getPurchaseId();
		String lineItemId = lineItemEntity.getLineItemId();

		TaxesEntity taxesEntity = Assign.ifSuccessful(taxesLookupStrategy.getTaxes(scope, purchaseId, shipmentId, lineItemId));

		ResourceState<TaxesEntity> taxesRepresentation = resourceState(taxesEntity, lineItem.getSelf().getUri());

		return ExecutionResultFactory.createReadOK(taxesRepresentation);
	}

	private ResourceState<TaxesEntity> resourceState(final TaxesEntity taxesEntity,
													final String shipmentLineItemUri) {
		return ResourceState.Builder
				.create(taxesEntity)
				.withScope(null)
				.withSelf(self(shipmentLineItemUri))
				.build();
	}

	private Self self(final String shipmentLineItemUri) {
		String taxesUri = taxesUriBuilderFactory.get()
				.setSourceUri(shipmentLineItemUri)
				.build();
		return SelfFactory.createSelf(taxesUri);
	}
}
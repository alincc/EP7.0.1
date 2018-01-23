/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.resource.shipments.ShipmentLookup;
import com.elasticpath.rest.resource.shipments.integration.ShipmentLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Loads a shipment's details based on shipment id and/or purchase id.
 */
@Singleton
@Named("shipmentLookup")
public final class ShipmentLookupImpl implements ShipmentLookup {

	private final ShipmentLookupStrategy shipmentLookupStrategy;

	private final TransformToResourceState<ShipmentEntity, ShipmentEntity> shipmentTransformer;

	private final TransformRfoToResourceState<LinksEntity, Collection<String>,	PurchaseEntity> shipmentLinksTransformer;

	/**
	 * Default constructor.
	 *
	 * @param shipmentLookupStrategy   the lookup strategy.
	 * @param shipmentTransformer      the representation transformer.
	 * @param shipmentLinksTransformer the shipmentLinksTransformer
	 */
	@Inject
	ShipmentLookupImpl(
			@Named("shipmentLookupStrategy")
			final ShipmentLookupStrategy shipmentLookupStrategy,
			@Named("shipmentTransformer")
			final TransformToResourceState<ShipmentEntity, ShipmentEntity> shipmentTransformer,
			@Named("shipmentLinksTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>,	PurchaseEntity> shipmentLinksTransformer) {

		this.shipmentLookupStrategy = shipmentLookupStrategy;
		this.shipmentTransformer = shipmentTransformer;
		this.shipmentLinksTransformer = shipmentLinksTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<ShipmentEntity>> getShipmentForPurchase(
			final ResourceState<PurchaseEntity> purchase,
			final String shipmentId) {

		final String purchaseId = purchase.getEntity().getPurchaseId();

		ShipmentEntity searchEntity = ShipmentEntity.builder()
				.withShipmentId(shipmentId)
				.withPurchaseId(purchaseId)
				.build();

		ShipmentEntity shipmentEntity = Assign.ifSuccessful(shipmentLookupStrategy.find(searchEntity));

		ResourceState<ShipmentEntity> shipmentRepresentation =
				shipmentTransformer.transform(purchase.getScope(), shipmentEntity);

		return ExecutionResultFactory.createReadOK(shipmentRepresentation);
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getShipmentsForPurchase(
			final ResourceState<PurchaseEntity> purchase) {

		final String purchaseId = purchase.getEntity().getPurchaseId();

		Collection<String> shipmentIds = Assign.ifSuccessful(shipmentLookupStrategy.findShipmentIds(
				purchase.getScope(), purchaseId));
		ResourceState<LinksEntity> representation = shipmentLinksTransformer.transform(shipmentIds, purchase);

		return ExecutionResultFactory.createReadOK(representation);
	}

}

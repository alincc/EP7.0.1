/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.resource.shipments.lineitems.ShipmentLineItemsLookup;
import com.elasticpath.rest.resource.shipments.lineitems.integration.ShipmentLineItemsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * The {@link ShipmentLineItemsLookup} implementation.
 */
@Singleton
@Named("shipmentLineItemsLookup")
public final class ShipmentLineItemsLookupImpl implements ShipmentLineItemsLookup {

	private final ShipmentLineItemsLookupStrategy shipmentLineItemsLookupStrategy;
	private final TransformToResourceState<ShipmentLineItemEntity, ShipmentLineItemEntity> shipmentLineItemTransformer;
	private final TransformRfoToResourceState<LinksEntity, Collection<String>, ShipmentEntity> shipmentLineItemLinksTransformer;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemsLookupStrategy  the shipment line item lookup strategy
	 * @param shipmentLineItemTransformer      the shipment line item transformer
	 * @param shipmentLineItemLinksTransformer the shipment line item links transformer
	 */
	@Inject
	public ShipmentLineItemsLookupImpl(
			@Named("shipmentLineItemsLookupStrategy")
			final ShipmentLineItemsLookupStrategy shipmentLineItemsLookupStrategy,
			@Named("shipmentLineItemTransformer")
			final TransformToResourceState<ShipmentLineItemEntity, ShipmentLineItemEntity> shipmentLineItemTransformer,
			@Named("shipmentLineItemLinksTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>, ShipmentEntity> shipmentLineItemLinksTransformer) {

		this.shipmentLineItemsLookupStrategy = shipmentLineItemsLookupStrategy;
		this.shipmentLineItemTransformer = shipmentLineItemTransformer;
		this.shipmentLineItemLinksTransformer = shipmentLineItemLinksTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<ShipmentLineItemEntity>> find(
			final ResourceState<ShipmentEntity> shipmentRepresentation,
			final String lineItemId) {
		ShipmentLineItemEntity requestEntity = createLineItemRequestEntity(shipmentRepresentation, lineItemId);
		ShipmentLineItemEntity resultEntity =
				Assign.ifSuccessful(shipmentLineItemsLookupStrategy.find(shipmentRepresentation.getScope(), requestEntity));
		ResourceState<ShipmentLineItemEntity> representation =
				shipmentLineItemTransformer.transform(shipmentRepresentation.getScope(), resultEntity);

		return ExecutionResultFactory.createReadOK(representation);
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> findAll(
			final ResourceState<ShipmentEntity> shipmentRepresentation) {

		ShipmentLineItemEntity requestEntity = createLineItemRequestEntity(shipmentRepresentation, null);
		Collection<String> lineItemIDs =
				Assign.ifSuccessful(shipmentLineItemsLookupStrategy.findLineItemIds(shipmentRepresentation.getScope(), requestEntity));
		ResourceState<LinksEntity> representation =
				shipmentLineItemLinksTransformer.transform(lineItemIDs, shipmentRepresentation);

		return ExecutionResultFactory.createReadOK(representation);
	}

	private ShipmentLineItemEntity createLineItemRequestEntity(final ResourceState<ShipmentEntity> shipmentRepresentation, final String lineItemId) {

		ShipmentEntity shipmentEntity = shipmentRepresentation.getEntity();
		return ShipmentLineItemEntity.builder()
				.withLineItemId(lineItemId)
				.withShipmentId(shipmentEntity.getShipmentId())
				.withPurchaseId(shipmentEntity.getPurchaseId())
				.withParentUri(ResourceStateUtil.getSelfUri(shipmentRepresentation))
				.build();
	}
}

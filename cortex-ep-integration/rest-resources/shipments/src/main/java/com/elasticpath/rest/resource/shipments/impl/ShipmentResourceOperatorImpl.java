/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ResourceName;
import com.elasticpath.rest.resource.dispatch.operator.annotation.SingleResourceUri;
import com.elasticpath.rest.resource.shipments.ShipmentLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Processes the resource operation on shipments.
 */
@Singleton
@Named("shipmentResourceOperator")
@Path({ ResourceName.PATH_PART })
public final class ShipmentResourceOperatorImpl implements ResourceOperator {

	private final ShipmentLookup shipmentLookup;

	/**
	 * Constructor.
	 *  @param shipmentLookup the shipment lookup
	 *
	 */
	@Inject
	ShipmentResourceOperatorImpl(
			@Named("shipmentLookup")
			final ShipmentLookup shipmentLookup) {

		this.shipmentLookup = shipmentLookup;
	}

	/**
	 * Handles the READ all shipments operation.
	 *
	 * @param operation the resource operation
	 * @param purchaseRepresentation the purchase representation to read shipments for
	 * @return the operation result
	 */
	@Path(AnyResourceUri.PATH_PART)
	@OperationType(Operation.READ)
	public OperationResult processReadShipments(
			@SingleResourceUri
			final ResourceState<PurchaseEntity> purchaseRepresentation,
			final ResourceOperation operation) {

		ResourceState<PurchaseEntity> decodedPurchase = decodePurchaseRepresentationIds(purchaseRepresentation);
		ExecutionResult<ResourceState<LinksEntity>> result = shipmentLookup.getShipmentsForPurchase(decodedPurchase);
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}

	/**
	 * Handles the READ shipment operations.
	 *
	 * @param purchaseRepresentation the purchase representation to read shipments for
	 * @param shipmentId shipment ID
	 * @param operation the resource operation
	 * @return the operation result
	 */
	@Path({ SingleResourceUri.PATH_PART, ResourceId.PATH_PART })
	@OperationType(Operation.READ)
	public OperationResult processRead(
			@SingleResourceUri
			final ResourceState<PurchaseEntity> purchaseRepresentation,
			@ResourceId
			final String shipmentId,
			final ResourceOperation operation) {

		ResourceState<PurchaseEntity> decodedPurchase = decodePurchaseRepresentationIds(purchaseRepresentation);
		ExecutionResult<ResourceState<ShipmentEntity>> result =
				shipmentLookup.getShipmentForPurchase(decodedPurchase, Base32Util.decode(shipmentId));
		return com.elasticpath.rest.legacy.operationresult.OperationResultFactory.create(result, operation);
	}


	private ResourceState<PurchaseEntity> decodePurchaseRepresentationIds(final ResourceState<PurchaseEntity> purchaseRepresentation) {
		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
				.withPurchaseId(
						Base32Util.decode(
								purchaseRepresentation.getEntity().getPurchaseId()))
				.build();

		return ResourceState.Builder.create(purchaseEntity)
				.withScope(purchaseRepresentation.getScope())
				.withSelf(purchaseRepresentation.getSelf())
				.build();
	}
}

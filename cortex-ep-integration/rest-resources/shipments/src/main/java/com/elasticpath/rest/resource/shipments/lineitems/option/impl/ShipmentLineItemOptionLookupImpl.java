/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.lineitems.option.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.resource.shipments.lineitems.option.ShipmentLineItemOptionLookup;
import com.elasticpath.rest.resource.shipments.lineitems.option.integration.ShipmentLineItemOptionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * The {@link ShipmentLineItemOptionLookup} implementation.
 */
@Singleton
@Named("shipmentLineItemOptionsLookup")
public final class ShipmentLineItemOptionLookupImpl implements ShipmentLineItemOptionLookup {

	private final ShipmentLineItemOptionsLookupStrategy shipmentLineItemOptionsLookupStrategy;
	private final TransformRfoToResourceState<ShipmentLineItemOptionEntity, ShipmentLineItemOptionEntity, ShipmentLineItemEntity>
			shipmentLineItemOptionTransformer;
	private final TransformRfoToResourceState<LinksEntity,	Collection<String>, ShipmentLineItemEntity> shipmentLineItemOptionLinksTransformer;
	private final TransformRfoToResourceState<ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionEntity>
			shipmentLineItemOptionValueTransformer;

	/**
	 * Constructor.
	 *
	 * @param shipmentLineItemOptionsLookupStrategy  the shipment line item option lookup strategy
	 * @param shipmentLineItemOptionTransformer      the shipment line item option transformer
	 * @param shipmentLineItemOptionLinksTransformer the shipment line item option links transformer
	 * @param shipmentLineItemOptionValueTransformer the shipment line item option value transformer
	 */
	@Inject
	public ShipmentLineItemOptionLookupImpl(
			@Named("shipmentLineItemOptionsLookupStrategy")
			final ShipmentLineItemOptionsLookupStrategy shipmentLineItemOptionsLookupStrategy,
			@Named("shipmentLineItemOptionTransformer")
			final TransformRfoToResourceState<ShipmentLineItemOptionEntity, ShipmentLineItemOptionEntity, ShipmentLineItemEntity>
					shipmentLineItemOptionTransformer,
			@Named("shipmentLineItemOptionLinksTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>,	ShipmentLineItemEntity> shipmentLineItemOptionLinksTransformer,
			@Named("shipmentLineItemOptionValueTransformer")
			final TransformRfoToResourceState<ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionValueEntity, ShipmentLineItemOptionEntity>
					shipmentLineItemOptionValueTransformer) {

		this.shipmentLineItemOptionsLookupStrategy = shipmentLineItemOptionsLookupStrategy;
		this.shipmentLineItemOptionTransformer = shipmentLineItemOptionTransformer;
		this.shipmentLineItemOptionLinksTransformer = shipmentLineItemOptionLinksTransformer;
		this.shipmentLineItemOptionValueTransformer = shipmentLineItemOptionValueTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> findAll(
			final ResourceState<ShipmentLineItemEntity> shipmentLineItem) {

		Collection<String> lineItemOptonIDs = Assign.ifSuccessful(
				shipmentLineItemOptionsLookupStrategy.findLineItemOptionIds(shipmentLineItem.getEntity()));
		ResourceState<LinksEntity> representation =
				shipmentLineItemOptionLinksTransformer.transform(lineItemOptonIDs, shipmentLineItem);

		return ExecutionResultFactory.createReadOK(representation);
	}

	@Override
	public ExecutionResult<ResourceState<ShipmentLineItemOptionEntity>> find(
			final ResourceState<ShipmentLineItemEntity> shipmentLineItem,
			final String lineItemOptionId) {

		ShipmentLineItemOptionEntity resultEntity =
				Assign.ifSuccessful(shipmentLineItemOptionsLookupStrategy
						.findLineItemOption(shipmentLineItem.getEntity(), lineItemOptionId));
		ResourceState<ShipmentLineItemOptionEntity> representation = shipmentLineItemOptionTransformer
				.transform(resultEntity, shipmentLineItem);

		return ExecutionResultFactory.createReadOK(representation);
	}

	@Override
	public ExecutionResult<ResourceState<ShipmentLineItemOptionValueEntity>> findOptionValues(
			final ResourceState<ShipmentLineItemOptionEntity> shipmentLineItemOption,
			final String lineItemOptionValueId) {


		ShipmentLineItemOptionEntity shipmentLineItemOptionEntity = shipmentLineItemOption.getEntity();

		ShipmentLineItemOptionValueEntity resultEntity = Assign.ifSuccessful(shipmentLineItemOptionsLookupStrategy
				.findLineItemOptionValue(shipmentLineItemOptionEntity, lineItemOptionValueId));
		ResourceState<ShipmentLineItemOptionValueEntity> representation =
				shipmentLineItemOptionValueTransformer.transform(resultEntity, shipmentLineItemOption);

		return ExecutionResultFactory.createReadOK(representation);
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.impl;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipmentdetails.ShippingOption;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.ShippingOptionLookup;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.ShippingOptionLookupStrategy;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.transform.ShippingOptionTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link ShippingOptionLookup}.
 */
@Singleton
@Named("shippingOptionLookup")
public final class ShippingOptionLookupImpl implements ShippingOptionLookup {

	private final String resourceName;
	private final ShippingOptionLookupStrategy shippingOptionLookupStrategy;
	private final ShippingOptionTransformer shippingOptionTransformer;


	/**
	 * Constructor.
	 *
	 * @param resourceName                 the resource name
	 * @param shippingOptionLookupStrategy the delivery method lookup strategy
	 * @param shippingOptionTransformer    the shipping option transformer
	 */
	@Inject
	public ShippingOptionLookupImpl(
			@Named("resourceServerName")
			final String resourceName,
			@Named("shippingOptionLookupStrategy")
			final ShippingOptionLookupStrategy shippingOptionLookupStrategy,
			@Named("shippingOptionTransformer")
			final ShippingOptionTransformer shippingOptionTransformer) {

		this.resourceName = resourceName;
		this.shippingOptionLookupStrategy = shippingOptionLookupStrategy;
		this.shippingOptionTransformer = shippingOptionTransformer;
	}


	@Override
	public ExecutionResult<Collection<String>> getShippingOptionIdsForShipmentDetail(final String scope, final String shipmentDetailId) {

		Collection<String> shipmentIds = Assign.ifSuccessful(
				shippingOptionLookupStrategy.getShippingOptionIdsForShipmentDetails(scope, shipmentDetailId));
		return ExecutionResultFactory.createReadOK(Base32Util.encodeAll(shipmentIds));

	}

	@Override
	public ExecutionResult<Boolean> isShippingDestinationSelectedForShipmentDetail(final String scope, final String shipmentDetailId) {
		return shippingOptionLookupStrategy.isShippingDestinationSelectedForShipmentDetails(scope, shipmentDetailId);
	}

	@Override
	public ExecutionResult<Boolean> isSupportedDeliveryType(final String deliveryType) {
		return shippingOptionLookupStrategy.isSupportedShippingOptionType(deliveryType);
	}

	@Override
	public ExecutionResult<ResourceState<ShippingOptionEntity>> getShippingOption(final String scope,
			final String shipmentDetailsId, final String shippingOptionId) {

		String decodedShippingOptionId = Base32Util.decode(shippingOptionId);
		ShippingOptionDto shippingOptionDto = Assign.ifSuccessful(
				shippingOptionLookupStrategy.getShippingOptionForShipmentDetails(scope, shipmentDetailsId, decodedShippingOptionId));
		String shippingOptionUri = URIUtil.format(resourceName, scope, shipmentDetailsId, ShippingOption.URI_PART, shippingOptionId);
		ResourceState<ShippingOptionEntity> representation = shippingOptionTransformer.transformToRepresentation(
				shippingOptionDto, shippingOptionUri);
		return ExecutionResultFactory.createReadOK(representation);

	}

	@Override
	public ExecutionResult<String> getSelectedShipmentOptionIdForShipmentDetails(final String scope,
																					final String shipmentDetailsId) {
		String selectedShippingOptionId = Assign.ifSuccessful(shippingOptionLookupStrategy
																.getSelectedShipmentOptionIdForShipmentDetails(scope, shipmentDetailsId));
		return ExecutionResultFactory.createReadOK(Base32Util.encode(selectedShippingOptionId));

	}
}

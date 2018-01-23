/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.ShippingOptionPromotionsLookup;
import com.elasticpath.rest.resource.promotions.integration.AppliedShippingOptionPromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Lookup class for shipping option promotions.
 */
@Singleton
@Named("shippingOptionPromotionsLookup")
public final class ShippingOptionPromotionsLookupImpl implements ShippingOptionPromotionsLookup {

	private static final int INDEX_OF_SHIPMENT_DETAILS_ID = 2;
	private static final int INDEX_OF_SCOPE = 1;
	private final AppliedShippingOptionPromotionsLookupStrategy appliedShippingOptionPromotionsLookupStrategy;
	private final TransformRfoToResourceState<LinksEntity, Collection<String>, ShippingOptionEntity> appliedPromotionsTransformer;

	/**
	 * Constructor.
	 *
	 * @param appliedShippingOptionPromotionsLookupStrategy the shipping option promotions lookup strategy.
	 * @param appliedPromotionsTransformer                  the promotions group transformer.
	 */
	@Inject
	ShippingOptionPromotionsLookupImpl(
			@Named("appliedShippingOptionPromotionsLookupStrategy")
			final AppliedShippingOptionPromotionsLookupStrategy appliedShippingOptionPromotionsLookupStrategy,
			@Named("appliedPromotionsTransformer")
			final TransformRfoToResourceState<LinksEntity, Collection<String>, ShippingOptionEntity> appliedPromotionsTransformer) {

		this.appliedShippingOptionPromotionsLookupStrategy = appliedShippingOptionPromotionsLookupStrategy;
		this.appliedPromotionsTransformer = appliedPromotionsTransformer;
	}

	@Override
	public ExecutionResult<ResourceState<LinksEntity>> getAppliedPromotionsForShippingOption(
			final ResourceState<ShippingOptionEntity> shippingOptionRepresentation) {

		ShippingOptionEntity shippingOptionEntity = shippingOptionRepresentation.getEntity();
		// TODO: Since the "other" here is a child ShippingOption we have lost the important
		// URI information on the "parent" shippingdetails object.  As an brand new resource
		// here, promotions is forced to either parse the self URI or modify the original
		// ShippingOptions resource to make the parent state available.  We have take the
		// latter road of parsing the self URI because we want to fix the framework in a
		// general way rather than patching the symptom by modifying shipmentdetails.
		String selfUri = shippingOptionRepresentation.getSelf().getUri();
		List<String> pathParts = URIUtil.SLASH_SPLITTER.splitToList(selfUri);

		String shipmentDetailsId = getParentShipmentDetailsId(pathParts);
		String decodedShippingOptionGuid = Base32Util.decode(shippingOptionEntity.getShippingOptionId());
		String scope = getParentScope(pathParts);

		Collection<String> promotionIds = Assign.ifSuccessful(appliedShippingOptionPromotionsLookupStrategy
				.getAppliedPromotionsForShippingOption(scope, shipmentDetailsId, decodedShippingOptionGuid));

		// Since the ShippingOption subresource representation is not scoped,
		// we do this slightly odd type conversion on it here to make the scope
		// available to the transformer.  Don't be (too) alarmed...
		ResourceState<ShippingOptionEntity> scopedResourceState = ResourceState.builderFrom(shippingOptionRepresentation)
				.withScope(scope)
				.build();

		ResourceState<LinksEntity> linksRepresentation =
				appliedPromotionsTransformer.transform(promotionIds, scopedResourceState);

		return ExecutionResultFactory.createReadOK(linksRepresentation);
	}

	private String getParentShipmentDetailsId(final List<String> pathParts) {
		// "shipmentdetails", Scope.PATH_PART, ResourceId.PATH_PART, ShippingOption.PATH_PART, ShippingOptionId.PATH_PART
		// This is composite: see ShippingServiceLevelRepositoryImpl.findShippingServiceLevelsForShipment()
		return pathParts.get(INDEX_OF_SHIPMENT_DETAILS_ID);
	}

	private String getParentScope(final List<String> pathParts) {
		// "shipmentdetails", Scope.PATH_PART, ResourceId.PATH_PART, ShippingOption.PATH_PART, ShippingOptionId.PATH_PART
		// This is composite: see ShippingServiceLevelRepositoryImpl.findShippingServiceLevelsForShipment()
		return pathParts.get(INDEX_OF_SCOPE);
	}
}

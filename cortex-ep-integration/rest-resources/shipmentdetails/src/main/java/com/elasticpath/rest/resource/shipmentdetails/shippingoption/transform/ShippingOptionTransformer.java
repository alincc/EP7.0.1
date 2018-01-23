/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.transform;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * The ShippingOption Transformer.
 */
@Singleton
@Named("shippingOptionTransformer")
public final class ShippingOptionTransformer {

	/**
	 * Transform to representation.
	 *
	 * @param shippingOptionDto the shipping option dto
	 * @param shippingOptionUri the shipping option uri
	 * @return the shipping option representation
	 */
	public ResourceState<ShippingOptionEntity> transformToRepresentation(final ShippingOptionDto shippingOptionDto, final String shippingOptionUri) {

		String shippingOptionId = Base32Util.encode(shippingOptionDto.getCorrelationId());
		return ResourceState.Builder
				.create(ShippingOptionEntity.builder()
						.withName(shippingOptionDto.getName())
						.withShippingOptionId(shippingOptionId)
						.withDisplayName(shippingOptionDto.getDisplayName())
						.withCarrier(shippingOptionDto.getCarrier())
						.withCost(shippingOptionDto.getCosts())
						.build())
				.withSelf(SelfFactory.createSelf(shippingOptionUri))
				.build();
	}
}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a {@link SkuOptionValue}, and its {@link SkuOption}, into {@link ShipmentLineItemOptionEntity}, and vice versa.
 */
@Named("skuOptionTransformer")
@Singleton
public class SkuOptionTransformer extends AbstractDomainTransformer<SkuOptionValue, ShipmentLineItemOptionEntity> {

	@Override
	public SkuOptionValue transformToDomain(final ShipmentLineItemOptionEntity deliveryLineItemOptionDto, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public ShipmentLineItemOptionEntity transformToEntity(final SkuOptionValue skuOptionValue, final Locale locale) {

		SkuOption skuOption = skuOptionValue.getSkuOption();

		ShipmentLineItemOptionEntity dto = ShipmentLineItemOptionEntity.builder()
			.withName(skuOption.getOptionKey())
			.withDisplayName(skuOption.getDisplayName(locale, true))
			.withLineItemOptionId(skuOption.getGuid())
			.withLineItemOptionValueId(skuOptionValue.getOptionValueKey())
			.build();

		return dto;
	}
}

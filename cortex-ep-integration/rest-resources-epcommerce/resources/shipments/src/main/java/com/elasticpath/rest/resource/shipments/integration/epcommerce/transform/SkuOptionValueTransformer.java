/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipments.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transform between a {@link SkuOptionValue} and {@link ShipmentLineItemOptionValueEntity}, and vice versa.
 */
@Named("skuOptionValueTransformer")
@Singleton
public class SkuOptionValueTransformer extends AbstractDomainTransformer<SkuOptionValue, ShipmentLineItemOptionValueEntity> {

	@Override
	public SkuOptionValue transformToDomain(final ShipmentLineItemOptionValueEntity deliveryLineItemOptionValueDto, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public ShipmentLineItemOptionValueEntity transformToEntity(final SkuOptionValue skuOptionValue, final Locale locale) {
		ShipmentLineItemOptionValueEntity dto = ShipmentLineItemOptionValueEntity.builder()
				.withName(skuOptionValue.getOptionValueKey())
				.withDisplayName(skuOptionValue.getDisplayName(locale, true))
				.build();

		return dto;
	}

}

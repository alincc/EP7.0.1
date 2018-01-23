/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.transform;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;

/**
 * Transforms {@link ShippingServiceLevel} to {@link ShippingOptionDto}, and vice versa.
 */
@Singleton
@Named("shippingServiceLevelTransformer")
public class ShippingServiceLevelTransformer {

	private final MoneyTransformer moneyTransformer;

	/**
	 * Default constructor.
	 *
	 * @param moneyTransformer the money transformer
	 */
	@Inject
	public ShippingServiceLevelTransformer(
			@Named("moneyTransformer")
			final MoneyTransformer moneyTransformer) {

		this.moneyTransformer = moneyTransformer;
	}

	/**
	 * Transforms the given domain objects to a new {@link ShippingOptionDto}.  Includes locale for localized properties.
	 *
	 * @param shippingServiceLevel the shipping service level to transform
	 * @param shippingPricingSnapshot the pricing snapshot corresponding to the shipping service level
	 * @param locale the locale
	 * @return the new entity instance
	 */
	public ShippingOptionDto transformToEntity(final ShippingServiceLevel shippingServiceLevel,
												final ShippingPricingSnapshot shippingPricingSnapshot,
												final Locale locale) {
		return ResourceTypeFactory.createResourceEntity(ShippingOptionDto.class)
				.setCorrelationId(shippingServiceLevel.getGuid())
				.setCarrier(shippingServiceLevel.getCarrier())
				.setDisplayName(shippingServiceLevel.getDisplayName(locale, false))
				.setName(shippingServiceLevel.getCode())
				.setCosts(getCosts(shippingPricingSnapshot, locale));
	}

	private Collection<CostEntity> getCosts(final ShippingPricingSnapshot shippingPricingSnapshot, final Locale locale) {
		Money shippingCost = shippingPricingSnapshot.getShippingPromotedPrice();
		if (shippingCost == null) {
			return Collections.emptyList();
		}

		CostEntity cost = moneyTransformer.transformToEntity(shippingCost, locale);
		return Collections.singleton(cost);
	}

}

/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShippingPricingSnapshot;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.shipmentdetails.shippingoption.integration.dto.ShippingOptionDto;


/**
 * Tests the {@link ShippingServiceLevelTransformer}.
 */
public class ShippingServiceLevelTransformerTest {

	private static final Locale LOCALE_CA = Locale.CANADA;
	private static final String SHIPPING_SERVICE_LEVEL_GUID = "SHIPPING_SERVICE_LEVEL_GUID";
	private static final String CARRIER = "CARRIER";
	private static final String DISPLAY_NAME = "DISPLAY_NAME";
	private static final String NAME = "NAME";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final CostEntity costEntity = ResourceTypeFactory.createResourceEntity(CostEntity.class);

	private final ShippingPricingSnapshot shippingPricingSnapshot = context.mock(ShippingPricingSnapshot.class);
	private final MoneyTransformer moneyTransformer = context.mock(MoneyTransformer.class);
	private final ShippingServiceLevelTransformer transformer = new ShippingServiceLevelTransformer(moneyTransformer);

	/**
	 * Test transform to entity with valid shipping cost.
	 */
	@Test
	public void testTransformToEntityWithValidShippingCost() {
		final Money money = Money.valueOf(BigDecimal.ONE, Currency.getInstance("CAD"));

		ShippingServiceLevel shippingServiceLevel = createMockShippingServiceLevel(money);

		context.checking(new Expectations() {
			{
				oneOf(moneyTransformer).transformToEntity(money, LOCALE_CA);
				will(returnValue(costEntity));
			}
		});

		ShippingOptionDto expectedShippingOptionDto = createShippingOptionDto(Collections.singletonList(costEntity));

		ShippingOptionDto shippingOptionDto = transformer.transformToEntity(shippingServiceLevel, shippingPricingSnapshot, LOCALE_CA);

		assertEquals("The shipping option dtos should be the same.", expectedShippingOptionDto, shippingOptionDto);
	}

	/**
	 * Test transform to entity with no shipping cost set on the shipping option.
	 */
	@Test
	public void testTransformToEntityWithNoShippingCost() {
		ShippingServiceLevel shippingServiceLevel = createMockShippingServiceLevel(null);

		ShippingOptionDto expectedShippingOptionDto = createShippingOptionDto(Collections.<CostEntity>emptyList());

		ShippingOptionDto shippingOptionDto = transformer.transformToEntity(shippingServiceLevel, shippingPricingSnapshot, LOCALE_CA);

		assertEquals("The shipping option dtos should be the same.", expectedShippingOptionDto, shippingOptionDto);
	}

	private ShippingOptionDto createShippingOptionDto(final Collection<CostEntity> costs) {
		ShippingOptionDto shippingOptionDto = ResourceTypeFactory.createResourceEntity(ShippingOptionDto.class)
				.setCorrelationId(SHIPPING_SERVICE_LEVEL_GUID)
				.setCarrier(CARRIER)
				.setDisplayName(DISPLAY_NAME)
				.setName(NAME)
				.setCosts(costs);

		return shippingOptionDto;
	}

	private ShippingServiceLevel createMockShippingServiceLevel(final Money shippingCost) {
		final ShippingServiceLevel shippingServiceLevel = context.mock(ShippingServiceLevel.class);

		context.checking(new Expectations() {
			{
				oneOf(shippingServiceLevel).getGuid();
				will(returnValue(SHIPPING_SERVICE_LEVEL_GUID));

				oneOf(shippingServiceLevel).getCarrier();
				will(returnValue(CARRIER));

				oneOf(shippingServiceLevel).getDisplayName(LOCALE_CA, false);
				will(returnValue(DISPLAY_NAME));

				oneOf(shippingServiceLevel).getCode();
				will(returnValue(NAME));

				oneOf(shippingPricingSnapshot).getShippingPromotedPrice();
				will(returnValue(shippingCost));
			}
		});

		return shippingServiceLevel;
	}

}

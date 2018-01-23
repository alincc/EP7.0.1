/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Tests for {@link TotalMoneyTransformer}.
 */
public class TotalMoneyTransformerTest {

	private static final BigDecimal AMOUNT = BigDecimal.TEN;
	private static final Currency CURRENCY = Currency.getInstance(Locale.CANADA);
	private static final Locale LOCALE = Locale.ENGLISH;

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final MoneyTransformer mockMoneyTransformer = context.mock(MoneyTransformer.class);
	private final TotalMoneyTransformer transformer = new TotalMoneyTransformer(mockMoneyTransformer);

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		transformer.transformToDomain(null);
	}

	/**
	 * Test transformer.
	 */
	@Test
	public void testTransformer() {
		final Money money = Money.valueOf(AMOUNT, CURRENCY);
		final CostEntity totalCostEntity = ResourceTypeFactory.createResourceEntity(CostEntity.class);

		context.checking(new Expectations() {
			{
				allowing(mockMoneyTransformer).transformToEntity(money, LOCALE);
				will(returnValue(totalCostEntity));
			}
		});

		TotalEntity totalDto = transformer.transformToEntity(money, LOCALE);
		assertEquals("totalDto should have 1 cost entity", 1, totalDto.getCost().size());
		CostEntity costEntity = CollectionUtil.first(totalDto.getCost());

		assertEquals("Total Cost does not match expected cost entity.", totalCostEntity, costEntity);
	}

}

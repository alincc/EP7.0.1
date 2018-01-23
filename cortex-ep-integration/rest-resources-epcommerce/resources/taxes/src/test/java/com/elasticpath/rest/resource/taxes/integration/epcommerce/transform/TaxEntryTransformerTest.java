/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.domain.wrapper.TaxEntryWrapper;

/**
 * Test class for {@link TaxEntryTransformer}.
 */
public class TaxEntryTransformerTest {

	private static final String TAX_CATEGORY_NAME = "TAX_CATEGORY_NAME";
	private static final Locale LOCALE = Locale.ENGLISH;
	private static final BigDecimal SINGLE_TAX = BigDecimal.TEN;
	private static final Currency CURRENCY = Currency.getInstance(Locale.CANADA);

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final MoneyTransformer moneyTransformer = context.mock(MoneyTransformer.class);
	private final TaxEntryTransformer taxEntryTransformer = new TaxEntryTransformer(moneyTransformer);

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		taxEntryTransformer.transformToDomain(null);
	}

	/**
	 * Test transform to entity.
	 */
	@Test
	public void testTransformToEntity() {
		TaxCategory taxCategory = createMockTaxCategory(TAX_CATEGORY_NAME);
		Money singleTaxMoney = Money.valueOf(SINGLE_TAX, CURRENCY);
		TaxEntryWrapper taxEntryWrapper = createTaxWrapper(taxCategory, singleTaxMoney);

		NamedCostEntity singleTaxCostEntity = ResourceTypeFactory.createResourceEntity(NamedCostEntity.class);
		NamedCostEntity expectedTaxEntity = createTaxEntity(TAX_CATEGORY_NAME, singleTaxCostEntity);

		shouldTransformMoneyToCostEntity(singleTaxMoney, singleTaxCostEntity);

		NamedCostEntity taxEntity = taxEntryTransformer.transformToEntity(taxEntryWrapper, Locale.ENGLISH);

		assertEquals("The tax entities should be equal.", expectedTaxEntity, taxEntity);
	}

	private void shouldTransformMoneyToCostEntity(final Money totalTaxesMoney, final CostEntity costEntity) {
		context.checking(new Expectations() {
			{
				oneOf(moneyTransformer).transformToEntity(totalTaxesMoney, LOCALE);
				will(returnValue(costEntity));
			}
		});
	}

	private TaxCategory createMockTaxCategory(final String name) {
		final TaxCategory taxCategory = context.mock(TaxCategory.class);

		context.checking(new Expectations() {
			{
				oneOf(taxCategory).getName();
				will(returnValue(name));
			}
		});

		return taxCategory;
	}

	private TaxEntryWrapper createTaxWrapper(final TaxCategory taxCategory, final Money money) {
		TaxEntryWrapper taxEntryWrapper = ResourceTypeFactory.createResourceEntity(TaxEntryWrapper.class);
		taxEntryWrapper.setTaxCategory(taxCategory)
				.setTaxValue(money);

		return taxEntryWrapper;
	}

	private NamedCostEntity createTaxEntity(final String displayName, final NamedCostEntity costEntity) {
		return NamedCostEntity.builderFrom(costEntity)
				.withTitle(displayName)
				.build();
	}
}

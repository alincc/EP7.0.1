/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.integration.epcommerce.transform;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.base.NamedCostEntity;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.domain.wrapper.TaxEntryWrapper;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Tests the {@link TaxCalculationResultTransformer}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaxCalculationResultTransformerTest {
	private static final String TAX_CATEGORY_NAME = "TAX_CATEGORY_NAME";
	private static final Currency CURRENCY = Currency.getInstance(Locale.CANADA);
	private static final Locale LOCALE = Locale.ENGLISH;
	private static final BigDecimal TOTAL_TAX = BigDecimal.TEN;
	private static final BigDecimal SINGLE_TAX = BigDecimal.ONE;

	@Mock
	private MoneyTransformer moneyTransformer;
	@Mock
	private TaxEntryTransformer taxEntryTransformer;

	@InjectMocks
	private TaxCalculationResultTransformer taxCalculationResultTransformer;

	/**
	 * Test transform to domain.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testTransformToDomain() {
		taxCalculationResultTransformer.transformToDomain(null);
	}

	/**
	 * Test successful transform to entity containing a valid tax calculation result with tax categories with associated values within.
	 */
	@Test
	public void testSuccessfulTransformToEntity() {
		TaxCategory taxCategory = mock(TaxCategory.class);

		Money singleTaxMoney = Money.valueOf(SINGLE_TAX, CURRENCY);
		Money totalTaxesMoney = Money.valueOf(TOTAL_TAX, CURRENCY);
		TaxCalculationResult taxCalculationResult = createMockTaxCalculationResult(taxCategory, totalTaxesMoney, singleTaxMoney);

		CostEntity costEntity = ResourceTypeFactory.createResourceEntity(CostEntity.class);

		NamedCostEntity singleTaxCostEntity = ResourceTypeFactory.createResourceEntity(NamedCostEntity.class);
		NamedCostEntity taxEntity = createTaxEntity(TAX_CATEGORY_NAME, singleTaxCostEntity);

		TaxEntryWrapper taxEntryWrapper = createTaxWrapper(taxCategory, singleTaxMoney);

		shouldTransformMoneyToCostEntity(totalTaxesMoney, costEntity);
		shouldTransformTaxToTaxEntity(taxEntryWrapper, taxEntity);

		TaxesEntity expectedTaxesEntity = createTaxesEntity(costEntity, Collections.singletonList(taxEntity));

		TaxesEntity taxesEntity = taxCalculationResultTransformer.transformToEntity(taxCalculationResult, LOCALE);

		assertEquals("The taxes dtos should be the same.", expectedTaxesEntity, taxesEntity);
	}

	private void shouldTransformTaxToTaxEntity(final TaxEntryWrapper taxEntryWrapper, final NamedCostEntity taxEntity) {
		when(taxEntryTransformer.transformToEntity(taxEntryWrapper, LOCALE)).thenReturn(taxEntity);
	}

	private void shouldTransformMoneyToCostEntity(final Money totalTaxesMoney, final CostEntity costEntity) {
		when(moneyTransformer.transformToEntity(totalTaxesMoney, LOCALE)).thenReturn(costEntity);
	}

	private TaxCalculationResult createMockTaxCalculationResult(final TaxCategory taxCategory,
			final Money totalTaxMoney,
			final Money singleTaxMoney) {
		TaxCalculationResult taxCalculationResult = mock(TaxCalculationResult.class);
		Map<TaxCategory, Money> taxMap = new HashMap<>();
		taxMap.put(taxCategory, singleTaxMoney);

		stub(taxCalculationResult.getTotalTaxes()).toReturn(totalTaxMoney);
		stub(taxCalculationResult.getTaxMap()).toReturn(taxMap);
		stub(taxCategory.getName()).toReturn(TAX_CATEGORY_NAME);
		return taxCalculationResult;
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

	private TaxesEntity createTaxesEntity(final CostEntity costEntity,
											final Collection<NamedCostEntity> taxEntities) {
		return TaxesEntity.builder()
				.withCost(taxEntities)
				.withTotal(costEntity)
				.build();
	}
}

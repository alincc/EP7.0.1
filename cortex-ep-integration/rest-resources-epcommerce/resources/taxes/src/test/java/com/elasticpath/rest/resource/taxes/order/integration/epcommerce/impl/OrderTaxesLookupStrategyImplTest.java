/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.taxes.order.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TaxesCalculator;
import com.elasticpath.rest.resource.taxes.integration.epcommerce.transform.TaxCalculationResultTransformer;
import com.elasticpath.service.tax.TaxCalculationResult;

/**
 * Tests for {@link OrderTaxesLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderTaxesLookupStrategyImplTest {

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String STORE_CODE = "STORE_CODE";
	private static final String USERID = "userid";
	private static final Locale LOCALE = Locale.ENGLISH;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock private ResourceOperationContext resourceOperationContext;
	@Mock private TaxesCalculator taxesCalculator;
	@Mock private TaxCalculationResultTransformer taxCalculationResultTransformer;

	@InjectMocks private OrderTaxesLookupStrategyImpl taxesLookupStrategy;

	@Test
	public void testTaxesLookup() {
		TaxCalculationResult taxCalculationResult = mock(TaxCalculationResult.class);
		shouldCalculateTax(ExecutionResultFactory.createReadOK(taxCalculationResult));
		setupSubject();
		TaxesEntity expectedTaxesEntity = ResourceTypeFactory.createResourceEntity(TaxesEntity.class);
		shouldTransformToEntity(taxCalculationResult, expectedTaxesEntity);

		ExecutionResult<TaxesEntity> result = taxesLookupStrategy.getTaxes(STORE_CODE, CART_ORDER_GUID);

		assertTrue("Tax Result should be successful", result.isSuccessful());
	}

	@Test
	public void testTaxesLookupWhenTaxCalculationFails() {
		shouldCalculateTax(ExecutionResultFactory.<TaxCalculationResult>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		taxesLookupStrategy.getTaxes(STORE_CODE, CART_ORDER_GUID);
	}

	private void setupSubject() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USERID, LOCALE);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	private void shouldCalculateTax(final ExecutionResult<TaxCalculationResult> result) {
		when(taxesCalculator.calculateTax(STORE_CODE, CART_ORDER_GUID)).thenReturn(result);
	}

	private void shouldTransformToEntity(final TaxCalculationResult taxCalculationResult, final TaxesEntity expectedTaxesEntity) {
		when(taxCalculationResultTransformer.transformToEntity(taxCalculationResult, LOCALE)).thenReturn(expectedTaxesEntity);
	}

}

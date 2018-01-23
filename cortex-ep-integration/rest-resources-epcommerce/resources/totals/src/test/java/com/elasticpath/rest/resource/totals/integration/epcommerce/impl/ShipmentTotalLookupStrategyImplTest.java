/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShipmentTotalsCalculator;
import com.elasticpath.rest.resource.totals.integration.epcommerce.transform.TotalMoneyTransformer;

/**
 * Tests {@link com.elasticpath.rest.resource.totals.integration.epcommerce.impl.ShipmentLineItemTotalLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTotalLookupStrategyImplTest {

	private static final String PURCHASE_GUID = "test-order";
	private static final String SHIPMENT_GUID = "test-shipment";

	private final Money money = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	@SuppressWarnings("PMD.UnusedPrivateField")
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private TotalMoneyTransformer totalMoneyTransformer;
	@Mock
	private ShipmentTotalsCalculator shipmentTotalsCalculator;
	@InjectMocks
	private ShipmentTotalLookupStrategyImpl shipmentTotalLookupStrategy;
	@Mock
	TotalEntity total;

	@Test
	public void testGetTotalWhenNotFound() {
		when(shipmentTotalsCalculator.calculateTotal(PURCHASE_GUID, SHIPMENT_GUID))
				.thenReturn(ExecutionResultFactory.<Money>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		shipmentTotalLookupStrategy.getTotal(PURCHASE_GUID, SHIPMENT_GUID);
	}


	@Test
	public void testGetTotalForSuccess() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale("scope", "user", Locale.ENGLISH);

		when(shipmentTotalsCalculator.calculateTotal(PURCHASE_GUID, SHIPMENT_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(money));
		when(totalMoneyTransformer.transformToEntity(eq(money), any(Locale.class)))
				.thenReturn(total);
		when(resourceOperationContext.getSubject())
				.thenReturn(subject);

		ExecutionResult<TotalEntity> executionResult = shipmentTotalLookupStrategy.getTotal(PURCHASE_GUID, SHIPMENT_GUID);

		assertExecutionResult(executionResult)
				.isSuccessful()
				.data(total);
	}
}

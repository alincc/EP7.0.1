/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.totals.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;
import com.elasticpath.rest.resource.totals.integration.epcommerce.transform.TotalMoneyTransformer;

/**
 * Tests {@link TotalLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TotalLookupStrategyImplTest {

	private static final String CART_GUID = "CART_GUID";
	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";
	private static final String LINE_ITEM_GUID = "LINE_ITEM_GUID";
	private static final String STORE_CODE = "STORE_CODE";
	private static final String USER_ID = "userId";
	private static final Locale LOCALE = Locale.ENGLISH;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private TotalMoneyTransformer totalMoneyTransformer;
	@Mock
	private TotalsCalculator totalsCalculator;

	@InjectMocks
	private TotalLookupStrategyImpl totalLookupStrategy;

	@Before
	public void setUp() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USER_ID, LOCALE);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
	}

	@Test
	public void testGetOrderTotal() {
		ExecutionResult<Money> calculatorResult = createCalculatorResultWithMoney();
		when(totalsCalculator.calculateTotalForCartOrder(STORE_CODE, CART_ORDER_GUID)).thenReturn(calculatorResult);
		TotalEntity totalDto = arrangeTransformerShouldReturnTotalEntity(calculatorResult.getData());

		ExecutionResult<TotalEntity> result = totalLookupStrategy.getOrderTotal(STORE_CODE, CART_ORDER_GUID);

		assertExecutionResult(result).isSuccessful().data(totalDto);
	}

	@Test
	public void testGetOrderTotalWhenCalculatorFails() {
		when(totalsCalculator.calculateTotalForCartOrder(STORE_CODE, CART_ORDER_GUID)).thenReturn(ExecutionResultFactory.<Money>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		totalLookupStrategy.getOrderTotal(STORE_CODE, CART_ORDER_GUID);
	}

	@Test
	public void testGettingTotalOnAValidCart() {
		ExecutionResult<Money> calculatorResult = createCalculatorResultWithMoney();
		when(totalsCalculator.calculateTotalForShoppingCart(STORE_CODE, CART_GUID)).thenReturn(calculatorResult);
		TotalEntity totalDto = arrangeTransformerShouldReturnTotalEntity(calculatorResult.getData());

		ExecutionResult<TotalEntity> result = totalLookupStrategy.getCartTotal(STORE_CODE, CART_GUID);

		assertExecutionResult(result).isSuccessful().data(totalDto);
	}

	@Test
	public void testGetCartTotalWhenCalculatorFails() {
		when(totalsCalculator.calculateTotalForShoppingCart(STORE_CODE, CART_GUID)).thenReturn(ExecutionResultFactory.<Money>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		totalLookupStrategy.getCartTotal(STORE_CODE, CART_GUID);
	}

	@Test
	public void testGetLineItemTotal() {
		ExecutionResult<Money> calculatorResult = createCalculatorResultWithMoney();
		when(totalsCalculator.calculateTotalForLineItem(STORE_CODE, CART_GUID, LINE_ITEM_GUID)).thenReturn(calculatorResult);
		TotalEntity totalDto = arrangeTransformerShouldReturnTotalEntity(calculatorResult.getData());

		ExecutionResult<TotalEntity> result = totalLookupStrategy.getLineItemTotal(STORE_CODE, CART_GUID, LINE_ITEM_GUID);

		assertExecutionResult(result).isSuccessful().data(totalDto);
	}

	@Test
	public void testLineItemTotalWhenCalculatorFails() {
		when(totalsCalculator.calculateTotalForLineItem(STORE_CODE, CART_GUID, LINE_ITEM_GUID))
				.thenReturn(ExecutionResultFactory.<Money>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		totalLookupStrategy.getLineItemTotal(STORE_CODE, CART_GUID, LINE_ITEM_GUID);
	}

	private ExecutionResult<Money> createCalculatorResultWithMoney() {
		Money total = Money.valueOf(BigDecimal.ONE, Currency.getInstance("CAD"));
		return ExecutionResultFactory.createReadOK(total);
	}

	private TotalEntity arrangeTransformerShouldReturnTotalEntity(final Money money) {
		TotalEntity totalDto = ResourceTypeFactory.createResourceEntity(TotalEntity.class);
		when(totalMoneyTransformer.transformToEntity(money, LOCALE)).thenReturn(totalDto);

		return totalDto;
	}

}

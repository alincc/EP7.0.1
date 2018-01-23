/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.integration.epcommerce.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Tests for {@link com.elasticpath.rest.resource.discounts.integration.epcommerce.impl.CartDiscountsLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseDiscountsLookupStrategyImplTest {

	private static final String TEST_ORDER_GUID = "test-cart-guid";
	private static final String SCOPE = "TESTSCOPE";
	private static final Money SOME_MONEY = Money.valueOf(BigDecimal.ONE, Currency.getInstance("EUR"));
	private static final Money NO_MONEY = Money.valueOf(BigDecimal.ZERO, Currency.getInstance("EUR"));

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private MoneyTransformer moneyTransformer;
	@InjectMocks
	private PurchaseDiscountsLookupStrategyImpl purchaseDiscountsLookupStrategy;
	@Mock
	private Order mockOrder;
	@Mock
	private CostEntity discountCost;

	@Before
	public void setUp() {
		when(mockOrder.getLocale()).thenReturn(Locale.ENGLISH);
		when(orderRepository.findByGuid(SCOPE, TEST_ORDER_GUID)).thenReturn(ExecutionResultFactory.createReadOK(mockOrder));

		discountCost = ResourceTypeFactory.createResourceEntity(CostEntity.class);
	}

	@Test
	public void testGetDiscountWhenSuccessful() {
		when(mockOrder.getSubtotalDiscountMoney()).thenReturn(SOME_MONEY);
		when(moneyTransformer.transformToEntity(SOME_MONEY, Locale.ENGLISH)).thenReturn(discountCost);

		ExecutionResult<DiscountEntity> result = purchaseDiscountsLookupStrategy.getPurchaseDiscounts(TEST_ORDER_GUID, SCOPE);

		assertThat("The discount costs should be as expected", result.getData().getDiscount(), hasItems(discountCost));
	}

	@Test
	public void testGetDiscountWhenNoDiscountPresent() {
		when(mockOrder.getSubtotalDiscountMoney()).thenReturn(NO_MONEY);
		when(moneyTransformer.transformToEntity(NO_MONEY, Locale.ENGLISH)).thenReturn(discountCost);

		ExecutionResult<DiscountEntity> result = purchaseDiscountsLookupStrategy.getPurchaseDiscounts(TEST_ORDER_GUID, SCOPE);

		assertThat("The discount costs should be as expected", result.getData().getDiscount(), hasItems(discountCost));
	}

	@Test
	public void testGetDiscountWhenPurchaseNotFound() {
		when(orderRepository.findByGuid(SCOPE, TEST_ORDER_GUID)).thenReturn(ExecutionResultFactory.<Order>createNotFound());

		ExecutionResult<DiscountEntity> result = purchaseDiscountsLookupStrategy.getPurchaseDiscounts(TEST_ORDER_GUID, SCOPE);

		assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

}

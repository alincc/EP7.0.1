/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.discounts.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
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

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerSessionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Tests for {@link CartDiscountsLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartsDiscountsLookupStrategyImplTest {

	private static final String TEST_CART_GUID = "test-cart-guid";
	private static final String SCOPE = "TESTSCOPE";
	private static final Money SOME_MONEY = Money.valueOf(BigDecimal.ONE, Currency.getInstance("EUR"));
	private static final Money NO_MONEY = Money.valueOf(BigDecimal.ZERO, Currency.getInstance("EUR"));

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Mock
	private CartOrderRepository cartOrderRepository;
	@Mock
	private CustomerSessionRepository customerSessionRepository;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;
	@Mock
	private MoneyTransformer moneyTransformer;
	@InjectMocks
	private CartDiscountsLookupStrategyImpl cartsDiscountsLookupStrategy;
	@Mock
	private CartOrder mockCartOrder;
	@Mock
	private ShoppingCart mockCart;
	@Mock
	private CustomerSession mockCustomerSession;
	@Mock
	private ShoppingCartPricingSnapshot mockPricingSnapshot;

	private CostEntity discountCost;

	@Before
	public void setUp() {
		when(mockCartOrder.getGuid()).thenReturn(TEST_CART_GUID);
		when(customerSessionRepository.findOrCreateCustomerSession()).thenReturn(ExecutionResultFactory.createReadOK(mockCustomerSession));

		when(mockCustomerSession.getLocale()).thenReturn(Locale.ENGLISH);
		when(cartOrderRepository.getEnrichedShoppingCart(SCOPE, TEST_CART_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCart));

		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(mockCart))
				.thenReturn(ExecutionResultFactory.createReadOK(mockPricingSnapshot));
		discountCost = ResourceTypeFactory.createResourceEntity(CostEntity.class);
	}

	@Test
	public void testGetDiscountWhenSuccessful() {
		when(mockPricingSnapshot.getSubtotalDiscountMoney()).thenReturn(SOME_MONEY);
		when(moneyTransformer.transformToEntity(SOME_MONEY, Locale.ENGLISH)).thenReturn(discountCost);

		ExecutionResult<DiscountEntity> result = cartsDiscountsLookupStrategy.getCartDiscounts(TEST_CART_GUID, SCOPE);

		assertThat("The discount costs should be as expected", result.getData().getDiscount(), hasItems(discountCost));
	}

	@Test
	public void testGetDiscountWhenNoDiscountPresent() {
		when(mockPricingSnapshot.getSubtotalDiscountMoney()).thenReturn(NO_MONEY);
		when(moneyTransformer.transformToEntity(NO_MONEY, Locale.ENGLISH)).thenReturn(discountCost);

		ExecutionResult<DiscountEntity> result = cartsDiscountsLookupStrategy.getCartDiscounts(TEST_CART_GUID, SCOPE);

		assertThat("The discount costs should be as expected", result.getData().getDiscount(), hasItems(discountCost));
	}

	@Test
	public void testGetDiscountWhenPurchaseNotFound() {
		when(cartOrderRepository.getEnrichedShoppingCart(SCOPE, TEST_CART_GUID, CartOrderRepository.FindCartOrder.BY_CART_GUID))
				.thenReturn(ExecutionResultFactory.<ShoppingCart>createNotFound());
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		cartsDiscountsLookupStrategy.getCartDiscounts(TEST_CART_GUID, SCOPE);
	}
}

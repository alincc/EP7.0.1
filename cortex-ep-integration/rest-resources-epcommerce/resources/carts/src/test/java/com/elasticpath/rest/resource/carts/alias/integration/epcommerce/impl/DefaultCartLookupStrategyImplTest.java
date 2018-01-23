/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias.integration.epcommerce.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.carts.alias.integration.DefaultCartLookupStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

/**
 * The test of {@link DefaultCartLookupStrategyImpl}.
 */
public class DefaultCartLookupStrategyImplTest {

	private static final String CART_GUID = "cartID";
	private static final String STORECODE = "some store";

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	private final ShoppingCartRepository mockShoppingCartRepository = context.mock(ShoppingCartRepository.class);
	private final DefaultCartLookupStrategy defaultCartLookupStrategy = new DefaultCartLookupStrategyImpl(mockShoppingCartRepository);

	/**
	 * Tests happy path for getting default cart, when the cart does not exist.
	 */
	@Test
	public void testGetDefaultCartGuidNewCart() {
		final ShoppingCart cart = createAndAddExpectationsForCart();
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCartRepository).getDefaultShoppingCartGuid(STORECODE);
				will(returnValue(ExecutionResultFactory.createNotFound("")));
				allowing(mockShoppingCartRepository).getDefaultShoppingCart();
				will(returnValue(ExecutionResultFactory.createReadOK(cart)));
			}
		});
		ExecutionResult<String> result = defaultCartLookupStrategy.getDefaultCartId(STORECODE);
		assertEquals(CART_GUID, result.getData());
		assertNull(result.getErrorMessage());
	}

	/**
	 * Tests failure for getting default cart, when the default shopping cart fails.
	 */
	@Test
	public void testGetDefaultCartWithMissingDefaultCart() {
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCartRepository).getDefaultShoppingCartGuid(STORECODE);
				will(returnValue(ExecutionResultFactory.createNotFound("")));
				allowing(mockShoppingCartRepository).getDefaultShoppingCart();
				will(returnValue(ExecutionResultFactory.createNotFound("Test induced failure for no cart")));
			}
		});
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		defaultCartLookupStrategy.getDefaultCartId(STORECODE);
	}

	/**
	 * Tests happy path for getting default cart, when the cart does exist.
	 */
	@Test
	public void testGetDefaultCartGuid() {
		context.checking(new Expectations() {
			{
				allowing(mockShoppingCartRepository).getDefaultShoppingCartGuid(STORECODE);
				will(returnValue(ExecutionResultFactory.createReadOK(CART_GUID)));
			}
		});
		ExecutionResult<String> result = defaultCartLookupStrategy.getDefaultCartId(STORECODE);
		assertEquals(CART_GUID, result.getData());
		assertNull(result.getErrorMessage());
	}

	private ShoppingCart createAndAddExpectationsForCart() {
		final ShoppingCart cart = context.mock(ShoppingCart.class);
		final Shopper shopper = context.mock(Shopper.class);
		context.checking(new Expectations() {
			{
				allowing(cart).getGuid();
				will(returnValue(CART_GUID));
				allowing(cart).getNumItems();
				will(returnValue(1));
				allowing(cart).getAllItems();
				will(returnValue(Collections.EMPTY_LIST));
				allowing(cart).getShopper();
				will(returnValue(shopper));
				allowing(shopper).getLocale();
				will(returnValue(Locale.US));
			}
		});

		return cart;
	}

}

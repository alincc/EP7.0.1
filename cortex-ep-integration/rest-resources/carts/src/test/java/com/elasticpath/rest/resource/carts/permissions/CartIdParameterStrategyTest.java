/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.permissions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.carts.CartLookup;

/**
 * Test class for {@link CartIdParameterStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CartIdParameterStrategyTest {
	private static final String PARAMETER_VALUE_SHOULD_BE_EMPTY_STRING = "The parameter value should be an empty string.";
	private static final String USER_ID = "user-id";
	private static final String SCOPE = "SCOPE";
	private static final String CART_ID = "CART_ID";
	private static final PrincipalCollection PRINCIPALS = TestSubjectFactory.createCollectionWithScopeAndUserId(SCOPE, USER_ID);

	@Mock
	private CartLookup cartLookup;

	@InjectMocks
	private CartIdParameterStrategy cartIdParameterStrategy;


	/**
	 * Test get cart ID parameter value on is cart owner by user failure.
	 */
	@Test
	public void testUserHasNoCart() {
		Collection<String> carts = Collections.emptyList();
		when(cartLookup.findCartIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(carts));
		String orderIdString = cartIdParameterStrategy.getParameterValue(PRINCIPALS);

		assertEquals(PARAMETER_VALUE_SHOULD_BE_EMPTY_STRING, StringUtils.EMPTY, orderIdString);
	}

	/**
	 * Test get cart ID parameter success.
	 */
	@Test
	public void testGetCartIdParameterValue() {
		Collection<String> carts = Arrays.asList(CART_ID);
		when(cartLookup.findCartIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(carts));

		String cartIdString = cartIdParameterStrategy.getParameterValue(PRINCIPALS);

		assertEquals(CART_ID, cartIdString);
	}
}

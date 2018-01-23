/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.permissions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.orders.OrderLookup;

/**
 * Test class for {@link OrderIdParameterStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class OrderIdParameterStrategyTest {
	private static final String PARAMETER_VALUE_SHOULD_BE_EMPTY_STRING = "The parameter value should be an empty string.";
	private static final String USER_ID = "USER_ID";
	private static final String SCOPE = "SCOPE";
	private static final PrincipalCollection PRINCIPALS = TestSubjectFactory.createCollectionWithScopeAndUserId(SCOPE, USER_ID);

	@Mock
	private OrderLookup orderLookup;

	@InjectMocks
	private OrderIdParameterStrategy orderIdParameterStrategy;


	/**
	 * Test return of an orders list.
	 */
	@Test
	public void testOrdersFound() {
		Collection<String> ordersList = CollectionUtils.asList("order1", "order2");
		when(orderLookup.findOrderIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(ordersList));

		String orderIdString = orderIdParameterStrategy.getParameterValue(PRINCIPALS);

		assertEquals(orderIdString, StringUtils.join(ordersList, ','));
	}

	/**
	 * Test return of an empty orders list.
	 */
	@Test
	public void testNoOrdersFound() {
		Collection<String> ordersList = Collections.emptyList();
		when(orderLookup.findOrderIds(SCOPE, USER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(ordersList));

		String orderIdString = orderIdParameterStrategy.getParameterValue(PRINCIPALS);

		assertThat(PARAMETER_VALUE_SHOULD_BE_EMPTY_STRING, orderIdString, Matchers.isEmptyString());
	}
}

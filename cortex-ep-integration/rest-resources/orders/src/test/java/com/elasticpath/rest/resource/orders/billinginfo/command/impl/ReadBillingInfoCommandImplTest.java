/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command.impl;

import static com.elasticpath.jmock.MockeryFactory.newRuleInstance;
import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_INFO_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingInfoCommand;
import com.elasticpath.rest.resource.orders.billinginfo.command.impl.ReadBillingInfoCommandImpl.BuilderImpl;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.OrdersUriBuilder;
import com.elasticpath.rest.schema.uri.OrdersUriBuilderFactory;

public final class ReadBillingInfoCommandImplTest {

	private static final String ORDER_ID = "ORDER_ID";
	private static final String ORDER_URI = "/orders/orderId";
	private static final String SCOPE = "SCOPE";

	@Rule
	public final JUnitRuleMockery context = newRuleInstance();

	private final OrdersUriBuilderFactory ordersUriBuilderFactory = context.mock(OrdersUriBuilderFactory.class);

	@Before
	public void setUp() {

		context.checking(new Expectations() {
			{
				OrdersUriBuilder ordersUriBuilder = context.mock(OrdersUriBuilder.class);
				allowing(ordersUriBuilderFactory).get();
				will(returnValue(ordersUriBuilder));

				allowing(ordersUriBuilder).setOrderId(ORDER_ID);
				will(returnValue(ordersUriBuilder));

				allowing(ordersUriBuilder).setScope(SCOPE);
				will(returnValue(ordersUriBuilder));

				allowing(ordersUriBuilder).build();
				will(returnValue(ORDER_URI));
			}
		});
	}

	@Test
	public void testGetBillingInfo() {

		ExecutionResult<ResourceState<InfoEntity>> result = createCommand().execute();

		assertTrue(result.isSuccessful());
		assertEquals(BILLING_ADDRESS_INFO_NAME, result.getData()
				.getEntity()
				.getName());
	}

	private ReadBillingInfoCommand createCommand() {

		ReadBillingInfoCommandImpl command = new ReadBillingInfoCommandImpl(ordersUriBuilderFactory);

		ReadBillingInfoCommandImpl.BuilderImpl builder = new BuilderImpl(command);
		return builder
				.setOrderId(ORDER_ID)
				.setScope(SCOPE)
				.build();
	}
}

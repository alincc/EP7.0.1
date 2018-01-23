/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.addresses.AddressesMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.orders.billinginfo.BillingAddressInfo;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoWriter;
import com.elasticpath.rest.resource.orders.billinginfo.command.SelectBillingInfoCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilder;
import com.elasticpath.rest.schema.uri.BillingAddressListUriBuilderFactory;
import com.elasticpath.rest.schema.uri.OrdersUriBuilder;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link SelectBillingInfoCommandImpl}.
 */
public class SelectBillingInfoCommandImplTest {

	private static final String RESOURCE_SERVER_NAME = "orders";

	private static final String SCOPE = "scope";

	private static final String ORDER_ID = "ORDER_ID";

	private static final String BILLING_ADDRESS_ID = "BILLING_ADDRESS_ID";

	private static final String BILLING_ADDRESS_URI = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, BILLING_ADDRESS_ID);

	private static final String ORDER_URI = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ORDER_ID);

	private static final String BILLING_ADDRESS_LIST_URI = URIUtil.format("addresses", SCOPE, "billing");

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private BillingInfoWriter billingInfoWriter;

	@Mock
	private OrdersUriBuilder ordersUriBuilder;

	@Mock
	private BillingAddressListUriBuilderFactory billingAddressUriBuilderFactory;


	/**
	 * Tests that {@link SelectBillingInfoCommand} works in the happy path case.
	 */
	@Test
	public void testSelectBillingInfo() {

		SelectBillingInfoCommand command = createSelectBillingInfoCommand();

		context.checking(new Expectations() {
			{
				allowing(billingInfoWriter).setAddressForOrder(SCOPE, ORDER_ID, BILLING_ADDRESS_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(Boolean.TRUE)));

				allowing(ordersUriBuilder).setScope(SCOPE);
				will(returnValue(ordersUriBuilder));

				allowing(ordersUriBuilder).setOrderId(ORDER_ID);
				will(returnValue(ordersUriBuilder));

				allowing(ordersUriBuilder).build();
				will(returnValue(ORDER_URI));
			}
		});

		ExecutionResult<ResourceState<ResourceEntity>> commandResult = command.execute();

		assertTrue(commandResult.isSuccessful());

		String billingAddressListUri = billingAddressUriBuilderFactory.get().setScope(SCOPE).build();
		String locationUri = URIUtil.format(ORDER_URI, BillingAddressInfo.URI_PART, Selector.URI_PART, billingAddressListUri);
		Self actualSelf = commandResult.getData().getSelf();
		assertEquals(locationUri, actualSelf.getUri());
	}

	/**
	 * Tests that {@link SelectBillingInfoCommand} fails when the {@link BillingInfoWriter} returns a server error.
	 */
	@Test
	public void testSelectBillingInfoWriterFails() {

		SelectBillingInfoCommand command = createSelectBillingInfoCommand();

		context.checking(new Expectations() {
			{
				allowing(billingInfoWriter).setAddressForOrder(SCOPE, ORDER_ID, BILLING_ADDRESS_ID);
				will(returnValue(ExecutionResultFactory.createNotFound()));
			}
		});

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		command.execute();
	}

	private SelectBillingInfoCommand createSelectBillingInfoCommand() {
		createUriBuilderFactoryExpectations(BILLING_ADDRESS_LIST_URI);
		SelectBillingInfoCommandImpl selectBillingInfoCommandImpl = new SelectBillingInfoCommandImpl(billingInfoWriter,
				ordersUriBuilder, billingAddressUriBuilderFactory);
		SelectBillingInfoCommandImpl.Builder builder = new SelectBillingInfoCommandImpl.BuilderImpl(selectBillingInfoCommandImpl);
		return builder.setBillingAddress(createAddressResourceState()).setScope(SCOPE).setOrderId(ORDER_ID).build();
	}

	private void createUriBuilderFactoryExpectations(final String billingAddressListUri) {
		final BillingAddressListUriBuilder pbauBuilder = context.mock(BillingAddressListUriBuilder.class);
		context.checking(new Expectations() {
			{
				allowing(billingAddressUriBuilderFactory).get();
				will(returnValue(pbauBuilder));
				allowing(pbauBuilder).setScope(with(any(String.class)));
				will(returnValue(pbauBuilder));
				allowing(pbauBuilder).build();
				will(returnValue(billingAddressListUri));
			}
		});
	}

	private ResourceState<AddressEntity> createAddressResourceState() {
		return ResourceState.Builder.create(AddressEntity.builder().withAddressId(BILLING_ADDRESS_ID).build())
				.withSelf(SelfFactory.createSelf(BILLING_ADDRESS_URI, AddressesMediaTypes.ADDRESS.id())).build();
	}
}
/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billinginfo.command.impl;

import static com.elasticpath.rest.resource.orders.billinginfo.BillingInfoConstants.BILLING_ADDRESS_LIST_NAME;
import static com.elasticpath.rest.schema.ResourceState.Builder.create;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.Operation;
import com.elasticpath.rest.TestResourceOperationContextFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.orders.billinginfo.BillingInfoLookup;
import com.elasticpath.rest.resource.orders.billinginfo.command.ReadBillingAddressChoiceCommand;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;

public final class ReadBillingAddressChoiceCommandImplTest {

	private static final String FAILURE_MESSAGE = "Intended mocked failure";
	private static final String RESOURCE_SERVER_NAME = "orders";
	private static final String SCOPE = "scope";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String BILLING_ADDRESS_ID = "BILLING_ADDRESS_ID";
	private static final String DIFFERENT_BILLING_ADDRESS_ID = "DIFFERENT_BILLING_ADDRESS_ID";
	private static final String BILLING_ADDRESS_URI = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, BILLING_ADDRESS_ID);
	private static final String ORDER_URI = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ORDER_ID);
	private static final String RESOURCE_OPERATION_URI = URIUtil.format(BILLING_ADDRESS_URI, ORDER_URI);
	private static final String DECODED_PROFILE_ID = "PROFILE_ID";

	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();
	@Mock
	private BillingInfoLookup billingInfoLookup;

	/**
	 * Tests that {@link ReadBillingAddressChoiceCommand} fails when the {@link BillingInfoLookup} returns a server error.
	 */
	@Test
	public void testBillingAddressWhenBillingInfoLookupFails() {

		ReadBillingAddressChoiceCommand command = createReadBillingAddressCommand();

		context.checking(new Expectations() {
			{
				allowing(billingInfoLookup).findAddressForOrder(SCOPE, ORDER_ID);
				will(returnValue(ExecutionResultFactory.<String>createServerError(FAILURE_MESSAGE)));
			}
		});

		ExecutionResult<ResourceState<LinksEntity>> commandResult = command.execute();

		assertTrue(commandResult.isSuccessful());
		ResourceState<LinksEntity> expectedRepresentation = createExpectedRepresentationBuilder();
		assertEquals(expectedRepresentation, commandResult.getData());
	}

	/**
	 * Tests that {@link ReadBillingAddressChoiceCommand} returns a representation with two extra links for the selector and select action
	 * when the {@link BillingInfoLookup} is successful but has a different selected address for the order.
	 */
	@Test
	public void testReadBillingAddressWithDifferentAddressSelected() {

		ReadBillingAddressChoiceCommand command = createReadBillingAddressCommand();

		context.checking(new Expectations() {
			{
				allowing(billingInfoLookup).findAddressForOrder(SCOPE, ORDER_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(DIFFERENT_BILLING_ADDRESS_ID)));
			}
		});

		ExecutionResult<ResourceState<LinksEntity>> commandResult = command.execute();
		assertTrue(commandResult.isSuccessful());

		ResourceState<LinksEntity> expectedRepresentation = createExpectedRepresentationBuilder();
		assertEquals(expectedRepresentation, commandResult.getData());
	}

	/**
	 * Tests that {@link ReadBillingAddressChoiceCommand} returns a representation with two extra links for the selector and select action
	 * when the {@link BillingInfoLookup} is successful with this address selected for the order.
	 */
	@Test
	public void testReadBillingAddressWithSameAddressSelected() {

		ReadBillingAddressChoiceCommand command = createReadBillingAddressCommand();

		context.checking(new Expectations() {
			{
				allowing(billingInfoLookup).findAddressForOrder(SCOPE, ORDER_ID);
				will(returnValue(ExecutionResultFactory.createReadOK(BILLING_ADDRESS_ID)));
			}
		});

		ExecutionResult<ResourceState<LinksEntity>> commandResult = command.execute();
		assertTrue(commandResult.isSuccessful());

		ResourceState<LinksEntity> expectedRepresentation = createExpectedRepresentationBuilder();
		assertEquals(expectedRepresentation, commandResult.getData());
	}

	private ResourceState<AddressEntity> createAddressResourceState() {

		return create(AddressEntity.builder()
				.withAddressId(BILLING_ADDRESS_ID)
				.build())
				.withSelf(createSelf(BILLING_ADDRESS_URI))
				.build();
	}

	private ResourceState<LinksEntity> createExpectedRepresentationBuilder() {

		LinksEntity linksEntity = LinksEntity.builder()
				.withName(BILLING_ADDRESS_LIST_NAME)
				.withElementListId(ORDER_ID)
				.build();
		return create(linksEntity)
				.withSelf(createSelf(RESOURCE_OPERATION_URI))
				.withScope(SCOPE)
				.build();
	}

	private ReadBillingAddressChoiceCommand createReadBillingAddressCommand() {

		Subject subject = TestSubjectFactory.createWithScopeAndUserId(SCOPE, DECODED_PROFILE_ID);
		ResourceOperationContext operationContext = TestResourceOperationContextFactory
				.create(Operation.READ, RESOURCE_OPERATION_URI, null, subject);

		ReadBillingAddressChoiceCommandImpl readBillingAddressChoiceCommand = new ReadBillingAddressChoiceCommandImpl(
				operationContext);

		ReadBillingAddressChoiceCommand.Builder builder = new ReadBillingAddressChoiceCommandImpl.BuilderImpl(readBillingAddressChoiceCommand);
		return builder.setBillingAddressResourceState(createAddressResourceState())
				.setScope(SCOPE)
				.setOrderId(ORDER_ID)
				.build();
	}
}

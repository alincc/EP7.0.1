/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias.command.impl;

import static org.junit.Assert.assertEquals;
import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elasticpath.jmock.MockeryFactory;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationContextFactory;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.paymentmethods.alias.DefaultPaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.alias.command.ReadDefaultPaymentMethodCommand;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilder;
import com.elasticpath.rest.schema.util.ResourceStateUtil;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link ReadDefaultPaymentMethodCommandImpl}.
 */
public final class ReadDefaultPaymentMethodCommandImplTest {

	private static final String DEFAULT_PAYMENT_ID = "DEFAULT_PAYMENT_ID";
	private static final String SCOPE = "SCOPE";
	private static final String USER_ID = "USER_ID";
	private static final String PAYMENT_METHOD_URI = URIUtil.format("paymethods", SCOPE, DEFAULT_PAYMENT_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	@Rule
	public final JUnitRuleMockery context = MockeryFactory.newRuleInstance();

	@Mock
	private DefaultPaymentMethodLookup defaultPaymentMethodLookup;
	@Mock
	private PaymentMethodUriBuilder paymentMethodUriBuilder;

	private final ResourceOperationContext resourceOperationContext = TestResourceOperationContextFactory.create(
			TestResourceOperationFactory.createRead(PAYMENT_METHOD_URI,
					TestSubjectFactory.createWithScopeAndUserId(SCOPE, USER_ID)));


	/**
	 * Test default payment method lookup.
	 */
	@Test
	public void testDefaultPaymentMethodLookup() {
		shouldGetDefaultPaymentMethodIdForProfileIdWithResult(ExecutionResultFactory.createReadOK(DEFAULT_PAYMENT_ID));
		shouldBuildPaymentMethodUri(PAYMENT_METHOD_URI);

		ReadDefaultPaymentMethodCommand command = createReadDefaultPaymentMethodCommand();
		ExecutionResult<ResourceState<ResourceEntity>> result = command.execute();

		assertEquals("This should return the expected resource status.", ResourceStatus.SEE_OTHER, result.getResourceStatus());
		assertEquals(PAYMENT_METHOD_URI, ResourceStateUtil.getSelfUri(result.getData()));
	}

	/**
	 * Test default payment method lookup when payment method id not found.
	 */
	@Test
	public void testDefaultPaymentMethodLookupWhenPaymentMethodIdNotFound() {
		shouldGetDefaultPaymentMethodIdForProfileIdWithResult(ExecutionResultFactory.<String>createNotFound("payment method id not found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		ReadDefaultPaymentMethodCommand command = createReadDefaultPaymentMethodCommand();
		command.execute();
	}

	private void shouldGetDefaultPaymentMethodIdForProfileIdWithResult(final ExecutionResult<String> result) {
		context.checking(new Expectations() {
			{
				oneOf(defaultPaymentMethodLookup).getDefaultPaymentMethodId(SCOPE, USER_ID);
				will(returnValue(result));
			}
		});
	}

	private void shouldBuildPaymentMethodUri(final String paymentMethodUri) {
		context.checking(new Expectations() {
			{
				oneOf(paymentMethodUriBuilder).setScope(SCOPE);
				will(returnValue(paymentMethodUriBuilder));

				oneOf(paymentMethodUriBuilder).setPaymentMethodId(Base32Util.encode(DEFAULT_PAYMENT_ID));
				will(returnValue(paymentMethodUriBuilder));

				oneOf(paymentMethodUriBuilder).build();
				will(returnValue(paymentMethodUri));
			}
		});
	}

	private ReadDefaultPaymentMethodCommand createReadDefaultPaymentMethodCommand() {
		ReadDefaultPaymentMethodCommandImpl readDefaultPaymentMethodCommand =
				new ReadDefaultPaymentMethodCommandImpl(defaultPaymentMethodLookup, paymentMethodUriBuilder,
						resourceOperationContext);

		ReadDefaultPaymentMethodCommandImpl.BuilderImpl builder =
				new ReadDefaultPaymentMethodCommandImpl.BuilderImpl(readDefaultPaymentMethodCommand);

		return builder
				.setScope(SCOPE)
				.build();
	}
}

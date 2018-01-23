/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.billing.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.addresses.billing.Billing;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Unit test for BillingAddress Resource Operators.
 */
@RunWith(MockitoJUnitRunner.class)
public final class BillingAddressesResourceOperatorsTest extends AbstractResourceOperatorUriTest {

	private static final String SCOPE = "mobee";

	@Spy
	private final BillingAddressListOperatorImpl billingAddressListOperator =
			new BillingAddressListOperatorImpl(null, null, null, null, null, null);
	@Spy
	private final DefaultBillingAddressOperatorImpl defaultBillingAddressOperator =
			new DefaultBillingAddressOperatorImpl(null, null);
	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Test for default billing address.
	 */
	@Test
	public void testDefaultBilling() {

		String uri = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE,
				Billing.URI_PART, Default.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		doReturn(mockOperationResult)
				.when(defaultBillingAddressOperator)
				.processRead(SCOPE, operation);

		dispatchMethod(operation, defaultBillingAddressOperator);

		verify(defaultBillingAddressOperator).processRead(SCOPE, operation);
	}

	/**
	 * Test for default billing address list.
	 */
	@Test
	public void testDefaultBillingAddressList() {

		String uri = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE, Billing.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		doReturn(mockOperationResult)
				.when(billingAddressListOperator)
				.processRead(SCOPE, operation);

		dispatchMethod(operation, billingAddressListOperator);

		verify(billingAddressListOperator).processRead(SCOPE, operation);
	}


	/**
	 * Test for default billing address.
	 */
	@Test
	public void testDefaultDefaultBillingAddress() {

		String uri = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE,
				Billing.URI_PART, Default.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		doReturn(mockOperationResult)
				.when(defaultBillingAddressOperator)
				.processRead(SCOPE, operation);

		dispatchMethod(operation, defaultBillingAddressOperator);

		verify(defaultBillingAddressOperator).processRead(SCOPE, operation);
	}
}

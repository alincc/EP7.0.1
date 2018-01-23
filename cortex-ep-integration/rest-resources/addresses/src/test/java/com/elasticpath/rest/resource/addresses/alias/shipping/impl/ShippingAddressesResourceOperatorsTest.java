/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.alias.shipping.impl;

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
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.addresses.shipping.Shipping;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Unit test for ShippingAddress Resource Operators.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ShippingAddressesResourceOperatorsTest extends AbstractResourceOperatorUriTest {

	private static final String SCOPE = "mobee";

	@Spy
	private final ShippingAddressListOperatorImpl shippingAddressListOperatorImpl =
			new ShippingAddressListOperatorImpl(null, null, null, null, null, null);
	@Spy
	private final DefaultShippingAddressOperatorImpl defaultShippingAddressOperatorImpl =
			new DefaultShippingAddressOperatorImpl(null, null);
	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Test for default shipping address list.
	 */
	@Test
	public void testDefaultShippingAddressList() {

		String uri = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE, Shipping.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(shippingAddressListOperatorImpl)
				.processRead(SCOPE, operation);

		dispatchMethod(operation, shippingAddressListOperatorImpl);

		verify(shippingAddressListOperatorImpl).processRead(SCOPE, operation);
	}


	/**
	 * Test for default shipping address.
	 */
	@Test
	public void testDefaultShippingAddress() {

		String uri = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE,
				Shipping.URI_PART, Default.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(defaultShippingAddressOperatorImpl)
				.processRead(SCOPE, operation);

		dispatchMethod(operation, defaultShippingAddressOperatorImpl);

		verify(defaultShippingAddressOperatorImpl).processRead(SCOPE, operation);
	}
}

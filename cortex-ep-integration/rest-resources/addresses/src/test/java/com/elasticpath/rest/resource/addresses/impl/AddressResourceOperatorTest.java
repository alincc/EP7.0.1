/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

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
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Unit test for AddressResourceOperator.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddressResourceOperatorTest extends AbstractResourceOperatorUriTest {

	private static final String SCOPE = "mobee";
	private static final String ADDRESS_ID = "3xz22pjosxx6x4ria6xfcmlb2c=";

	@Spy
	private final AddressResourceOperatorImpl addressResourceOperator =
			new AddressResourceOperatorImpl(null, null, null, null, null);

	@Mock
	private OperationResult mockOperationResult;

	@Test
	public void testValidAddressRead() {

		String uri = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE, ADDRESS_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		doReturn(mockOperationResult)
				.when(addressResourceOperator)
				.processReadAddress(SCOPE, ADDRESS_ID, operation);

		dispatchMethod(operation, addressResourceOperator);

		verify(addressResourceOperator).processReadAddress(SCOPE, ADDRESS_ID, operation);
	}
}

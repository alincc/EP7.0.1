/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.addresses.impl;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.addresses.rel.AddressResourceRels;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.uri.URIUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link com.elasticpath.rest.resource.addresses.impl.AddressListResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressListResourceOperatorImplTest extends AbstractResourceOperatorUriTest {

	private static final String SCOPE = "mobee";

	@Mock
	private OperationResult mockOperationResult;

	@Spy
	private final AddressListResourceOperatorImpl addressListOperator =
			new AddressListResourceOperatorImpl(null, null, null, null);

	@Test
	public void testProcessReadAddresses() {

		String uri = URIUtil.format(AddressResourceRels.ADDRESSES_REL, SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);

		doReturn(mockOperationResult).when(addressListOperator).processReadAddresses(SCOPE, operation);

		dispatchMethod(operation, addressListOperator);

		verify(addressListOperator).processReadAddresses(SCOPE, operation);
	}
}

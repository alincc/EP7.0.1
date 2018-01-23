/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.geographies.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GeographiesLinksResourceOperatorImpl.class)
public class GeographiesLinksResourceOperatorImplTest extends AbstractResourceOperatorUriTest {

	@Spy
	private final GeographiesLinksResourceOperatorImpl resourceOperator = new GeographiesLinksResourceOperatorImpl(null);
	@Mock
	private OperationResult mockOperationResult;

	@Test
	public void testProcessLink() throws Exception {
		ResourceOperation operation = TestResourceOperationFactory.createLink("/geographies/otheruri", null);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processLink(operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processLink(operation);

	}
}
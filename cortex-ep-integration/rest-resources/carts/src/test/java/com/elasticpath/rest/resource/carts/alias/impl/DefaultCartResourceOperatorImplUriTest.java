/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.carts.alias.impl;

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
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;


/**
 * Tests URI-related annotations on {@link DefaultCartResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DefaultCartResourceOperatorImpl.class })
public final class DefaultCartResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {

	@Spy
	private final DefaultCartResourceOperatorImpl defaultCartResourceOperator = new DefaultCartResourceOperatorImpl(null);
	@Mock
	private OperationResult operationResult;

	/**
	 * Test {@link DefaultCartResourceOperatorImpl#processCartRead(String, ResourceOperation)}.
	 */
	@Test
	public void testProcessDefaultCartRead() {
		ResourceOperation operation = TestResourceOperationFactory.createRead("/carts/siu/" + Default.URI_PART);

		doReturn(operationResult)
				.when(defaultCartResourceOperator)
				.processCartRead("siu", operation);

		dispatchMethod(operation, defaultCartResourceOperator);

		verify(defaultCartResourceOperator).processCartRead("siu", operation);
	}

}

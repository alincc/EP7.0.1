/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.navigations.impl;

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
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests URI-related annotations on {@link NavigationsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ NavigationsResourceOperatorImpl.class })
public final class NavigationsResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {

	private static final String TEST_NAVIGATION_ID = "testnavigationid=";
	private static final String NAVIGATIONS = "navigations";
	private static final String TEST_SCOPE = "testscope";

	@Spy
	private final NavigationsResourceOperatorImpl resourceOperator = new NavigationsResourceOperatorImpl(null);

	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Test path annotation for read root navigation nodes.
	 */
	@Test
	public void testPathAnnotationForReadRootNavigationNodes() {
		String uri = URIUtil.format(NAVIGATIONS, TEST_SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadRootNavigationNodes(TEST_SCOPE, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadRootNavigationNodes(TEST_SCOPE, operation);
	}

	/**
	 * Test path annotation for read navigation node.
	 */
	@Test
	public void testPathAnnotationForReadNavigationNode() {
		String uri = URIUtil.format(NAVIGATIONS, TEST_SCOPE, TEST_NAVIGATION_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadNavigationNode(TEST_SCOPE, TEST_NAVIGATION_ID, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadNavigationNode(TEST_SCOPE, TEST_NAVIGATION_ID, operation);
	}
}

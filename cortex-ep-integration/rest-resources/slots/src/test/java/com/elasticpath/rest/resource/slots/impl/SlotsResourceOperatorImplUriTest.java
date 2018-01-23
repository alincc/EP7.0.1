/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.slots.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import org.powermock.core.classloader.annotations.PrepareForTest;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests URI-related annotations on {@link SlotsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SlotsResourceOperatorImpl.class })
public final class SlotsResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {

	private static final String SLOT_ID = "testslotid=";
	private static final String RESOURCE_NAME = "slots";
	private static final String SCOPE = "mobee";

	@Spy
	private final SlotsResourceOperatorImpl resourceOperator = new SlotsResourceOperatorImpl(null, null, null);
	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Tests {@link SlotsResourceOperatorImpl#processReadList(String, ResourceOperation)}.
	 */
	@Test
	public void testPathAnnotationForProcessReadList() {
		String uri = URIUtil.format(RESOURCE_NAME, SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadList(SCOPE, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadList(SCOPE, operation);
	}

	/**
	 * Tests {@link SlotsResourceOperatorImpl#processReadSlot(String, String, ResourceOperation)}.
	 */
	@Test
	public void testPathAnnotationForProcessReadSlot() {
		String uri = URIUtil.format(RESOURCE_NAME, SCOPE, SLOT_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadSlot(SCOPE, SLOT_ID, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadSlot(SCOPE, SLOT_ID, operation);
	}
}

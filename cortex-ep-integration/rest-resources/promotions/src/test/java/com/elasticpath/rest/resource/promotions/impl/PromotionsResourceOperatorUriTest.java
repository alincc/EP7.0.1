/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

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
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;

/**
 * URI test class for {@link PromotionsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PromotionsResourceOperatorImpl.class })
public final class PromotionsResourceOperatorUriTest extends AbstractResourceOperatorUriTest {

	private static final String RESOURCE_SERVER_NAME = "promotions";
	private static final String ENCODED_PROMOTION_ID = Base32Util.encode("promotions");
	private static final String SCOPE = "scope";

	@Mock
	private OperationResult mockOperationResult;

	@Spy
	private final PromotionsResourceOperatorImpl resourceOperator = new PromotionsResourceOperatorImpl(null);

	@Test
	public void testReadPromotionDetails() {
		String promotionsUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setScope(SCOPE)
				.setPromotionId(ENCODED_PROMOTION_ID)
				.build();
		ResourceOperation operation = TestResourceOperationFactory.createRead(promotionsUri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadPromotionDetails(SCOPE, ENCODED_PROMOTION_ID, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadPromotionDetails(SCOPE, ENCODED_PROMOTION_ID, operation);
	}
}

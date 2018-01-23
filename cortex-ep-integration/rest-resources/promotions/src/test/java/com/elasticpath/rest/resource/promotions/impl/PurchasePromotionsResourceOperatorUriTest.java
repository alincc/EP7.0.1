/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractUriTest;
import com.elasticpath.rest.schema.ResourceState;

/**
 * URI test class for {@link com.elasticpath.rest.resource.promotions.impl.PromotionsResourceOperatorImpl}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PromotionsResourceOperatorImpl.class })
public final class PurchasePromotionsResourceOperatorUriTest extends AbstractUriTest {

	private static final String RESOURCE_SERVER_NAME = "promotions";
	private static final String ENCODED_PROMOTION_ID = Base32Util.encode("promotions");
	private static final String OTHER_URI = "items/other/uri=";

	@Spy
	private final PromotionsResourceOperatorImpl resourceOperator = new PromotionsResourceOperatorImpl(null);

	@Before
	public void setUp() {

		mediaType(PurchasesMediaTypes.PURCHASE);
	}


	@Test
	public void testReadPurchasePromotionDetails() {
		String promotionsUri = new PromotionsUriBuilderImpl(RESOURCE_SERVER_NAME)
				.setSourceUri(OTHER_URI)
				.setPromotionId(ENCODED_PROMOTION_ID)
				.build();

		ResourceOperation operation = TestResourceOperationFactory.createRead(promotionsUri);

		readOther(operation);
		doReturn(operationResult)
				.when(resourceOperator)
				.processReadPromotionDetailsForPurchase(anyPurchaseEntity(), anyString(), anyResourceOperation());
		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processReadPromotionDetailsForPurchase(anyPurchaseEntity(), anyString(), anyResourceOperation());
	}

	private static ResourceState<PurchaseEntity> anyPurchaseEntity() {
		return Mockito.any();
	}

	protected static ResourceOperation anyResourceOperation() {

		return any(ResourceOperation.class);
	}
}

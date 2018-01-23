/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.promotions.PromotionsLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.test.AssertOperationResult;

/**
 * Test class for {@link PromotionsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PromotionsResourceOperatorImplTest {

	private static final String SCOPE = "scope";
	private static final String TEST_URI = "/testUri";
	private static final String PROMOTION_ID = "decoded_id";
	private static final String RESOURCE_STATUS_SHOULD_BE_READ_OK = "Resource Status should be read OK.";
	private static final String STATE_RETURNED_DOES_NOT_MATCH_EXPECTED = "Resource State returned does not match expected.";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(TEST_URI);

	@Mock
	private PromotionsLookup promotionsLookup;
	@InjectMocks
	private PromotionsResourceOperatorImpl promotionsResourceOperator;
	@Mock
	ResourceState<PromotionEntity> promotionEntityResourceState;


	@Test
	public void testProcessReadPromotionDetails() {
		when(promotionsLookup.getPromotionDetails(SCOPE, PROMOTION_ID)).thenReturn(ExecutionResultFactory.createReadOK(promotionEntityResourceState));

		OperationResult result = promotionsResourceOperator.processReadPromotionDetails(SCOPE, PROMOTION_ID, READ);

		verify(promotionsLookup).getPromotionDetails(SCOPE, PROMOTION_ID);
		assertEquals(RESOURCE_STATUS_SHOULD_BE_READ_OK, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(STATE_RETURNED_DOES_NOT_MATCH_EXPECTED, promotionEntityResourceState, result.getResourceState());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testProcessReadPromotionDetailsForPurchaseIsSuccessful() {
		ResourceState<PurchaseEntity> purchaseRepresentation = mock(ResourceState.class);
		when(purchaseRepresentation.getScope()).thenReturn(SCOPE);
		PurchaseEntity mockPurchaseEntity = mock(PurchaseEntity.class);
		when(purchaseRepresentation.getEntity()).thenReturn(mockPurchaseEntity);
		when(mockPurchaseEntity.getPurchaseId()).thenReturn(Base32Util.encode("Purchase_Id"));

		when(promotionsLookup.getPurchasePromotionDetails(any(String.class), any(String.class), any(String.class),
				any(ResourceState.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(promotionEntityResourceState));

		OperationResult result = promotionsResourceOperator.processReadPromotionDetailsForPurchase(purchaseRepresentation, PROMOTION_ID,
				READ);

		AssertOperationResult.assertOperationResult(result)
				.resourceStatus(ResourceStatus.READ_OK)
				.resourceState(promotionEntityResourceState);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testProcessReadPromotionDetailsForCouponIsSuccessful() {
		ResourceState<CouponEntity> couponRepresentation = mock(ResourceState.class);
		when(couponRepresentation.getScope()).thenReturn(SCOPE);
		CouponEntity mockCouponEntity = mock(CouponEntity.class);
		when(couponRepresentation.getEntity()).thenReturn(mockCouponEntity);
		when(mockCouponEntity.getParentId()).thenReturn("Purchase_Id");

		when(promotionsLookup.getPurchasePromotionDetails(any(String.class), any(String.class), any(String.class),
				any(ResourceState.class)))
				.thenReturn(ExecutionResultFactory.createReadOK(promotionEntityResourceState));

		OperationResult result = promotionsResourceOperator.processReadPromotionDetailsForCoupon(couponRepresentation, PROMOTION_ID,
				READ);

		AssertOperationResult.assertOperationResult(result)
				.resourceStatus(ResourceStatus.READ_OK)
				.resourceState(promotionEntityResourceState);
	}
}

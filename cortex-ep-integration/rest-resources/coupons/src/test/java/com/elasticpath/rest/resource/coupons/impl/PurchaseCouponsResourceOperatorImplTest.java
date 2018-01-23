/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.resource.coupons.constant.CouponsConstants.PURCHASES_FOR_COUPONS_LIST;
import static com.elasticpath.rest.schema.SelfFactory.createSelf;
import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
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
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.coupons.PurchaseCouponsLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.uri.CouponsUriBuilderFactory;

/**
 * Testing for correct invocation of methods based on types.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class PurchaseCouponsResourceOperatorImplTest {

	private static final String COUPON_ID = "COUPON_ID";

	private static final String TEST_URI = "/testuri";

	@Mock
	private CouponsUriBuilderFactory couponsUriBuilderFactory;

	@Mock
	private PurchaseCouponsLookup mockPurchaseCouponsLookup;

	@Mock
	private ResourceState<CouponEntity> mockCoupon;

	@Mock
	private ResourceState<PurchaseEntity> purchaseResourceState;

	@InjectMocks
	private PurchaseCouponsResourceOperatorImpl resourceOperator;

	@Before
	public void setUp() {

		when(couponsUriBuilderFactory.get()).thenAnswer(invocation -> new CouponsUriBuilderImpl("coupons"));
	}

	@Test
	public void testProcessReadCouponDetailsReturnsReadOkay() {
		arrangeASuccessfulPurchaseResult(PurchasesMediaTypes.PURCHASE.id());

		OperationResult result = resourceOperator.processReadCouponDetailsForPurchase(purchaseResourceState, COUPON_ID, createReadOperation());

		assertOperationResult(result)
				.resourceState(mockCoupon)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void testProcessReadCouponDetailsReturnsNotFound() {
		arrangeANotFoundPurchaseResult(PurchasesMediaTypes.PURCHASE.id());

		OperationResult result = resourceOperator.processReadCouponDetailsForPurchase(purchaseResourceState, COUPON_ID, createReadOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testProcessReadCouponDetailsReturnsServerError() {
		arrangeAServerErrorPurchaseResult(PurchasesMediaTypes.PURCHASE.id());

		OperationResult result = resourceOperator.processReadCouponDetailsForPurchase(purchaseResourceState, COUPON_ID, createReadOperation());

		assertOperationResult(result).resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessReadPurchaseCouponsWhenReadOkay() {
		PurchaseEntity purchaseEntity = PurchaseEntity.builder()
				.build();
		ResourceState<PurchaseEntity> purchaseResourceState = ResourceState.Builder
				.create(purchaseEntity)
				.withSelf(createSelf("selfUri"))
				.build();

		OperationResult result = resourceOperator.processReadCouponsForPurchase(purchaseResourceState, createReadOperation());

		ResourceState<LinksEntity> state = (ResourceState<LinksEntity>) result.getResourceState();
		assertEquals(PURCHASES_FOR_COUPONS_LIST, state.getEntity().getName());
	}

	private ResourceOperation createReadOperation() {
		return TestResourceOperationFactory.createRead(TEST_URI);
	}

	private void arrangeASuccessfulPurchaseResult(final String type) {
		arrangeLookupsAndWritersToReturnReadOkays();

		mockReadFromPurchase(type);
	}

	private void arrangeAServerErrorPurchaseResult(final String type) {
		arrangeLookupsAndWritersToReturnServerError();

		mockReadFromPurchase(type);
	}

	private void arrangeANotFoundPurchaseResult(final String type) {
		arrangeLookupsAndWritersToReturnNotFound();

		mockReadFromPurchase(type);
	}

	private void arrangeLookupsAndWritersToReturnReadOkays() {

		when(mockPurchaseCouponsLookup.getCouponDetailsForPurchase(anyPurchase(), anyString()))
				.thenReturn(ExecutionResultFactory.createReadOK(mockCoupon));
	}

	private void arrangeLookupsAndWritersToReturnNotFound() {
		when(mockPurchaseCouponsLookup.getCouponDetailsForPurchase(anyPurchase(), anyString()))
				.thenReturn(ExecutionResultFactory.<ResourceState<CouponEntity>>createNotFound());
	}

	private ResourceState<PurchaseEntity> anyPurchase() {
		return any();
	}

	private void arrangeLookupsAndWritersToReturnServerError() {
		String testServerMsg = "Test server error";
		when(mockPurchaseCouponsLookup.getCouponDetailsForPurchase(anyPurchase(), anyString()))
				.thenReturn(ExecutionResultFactory.<ResourceState<CouponEntity>>createServerError(testServerMsg));
	}

	private void mockReadFromPurchase(final String type) {
		Self self = mock(Self.class);
		when(self.getType()).thenReturn(type);

		when(purchaseResourceState.getSelf()).thenReturn(self);
	}
}

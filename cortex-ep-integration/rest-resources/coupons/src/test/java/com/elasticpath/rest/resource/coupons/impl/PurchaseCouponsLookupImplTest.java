/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.coupons.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.purchases.PurchaseEntity;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.coupons.integration.PurchaseCouponsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;

/**
 * Tests for {@link PurchaseCouponsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseCouponsLookupImplTest {

	private static final String DECODED_COUPON_ID = "test_id_2";
	private static final String COUPON_ID = Base32Util.encode(DECODED_COUPON_ID);
	private static final String DECODED_PURCHASE_ID = "PURCHASE_ID";
	private static final String PURCHASE_ID = Base32Util.encode("PURCHASE_ID");

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private PurchaseCouponsLookupStrategy mockCouponsLookupStrategy;

	@Mock
	private TransformRfoToResourceState<LinksEntity, Iterable<String>, PurchaseEntity> mockCouponLinksTransformer;

	@Mock
	private TransformRfoToResourceState<CouponEntity, CouponEntity, PurchaseEntity> mockCouponDetailsTransformer;

	private PurchaseCouponsLookupImpl lookup;

	@Mock
	private Iterable<String> mockCollectionOfCouponIds;

	@Mock
	private ResourceState<LinksEntity> linksResourceState;

	@Mock
	private LinksEntity mockLinksEntity;

	@Mock
	private ResourceState<PurchaseEntity> purchaseResourceState;

	@Mock
	private PurchaseEntity mockPurchaseEntity;

	@Mock
	private ResourceState<CouponEntity> couponResourceState;

	private final CouponEntity expectedCouponEntity = CouponEntity.builder()
			.build();

	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
		// cannot use inject mocks with two of the same class in the constructor.
		lookup = new PurchaseCouponsLookupImpl(mockCouponsLookupStrategy, mockCouponDetailsTransformer);
		when(linksResourceState.getEntity()).thenReturn(mockLinksEntity);
		when(purchaseResourceState.getEntity()).thenReturn(mockPurchaseEntity);
		when(couponResourceState.getEntity()).thenReturn(expectedCouponEntity);
		when(mockPurchaseEntity.getPurchaseId()).thenReturn(PURCHASE_ID);
		Self mockPurchaseSelf = mock(Self.class);
		when(purchaseResourceState.getSelf()).thenReturn(mockPurchaseSelf);
		when(mockPurchaseSelf.getType()).thenReturn(PurchasesMediaTypes.PURCHASE.id());
	}

	@Test
	public void testDetailsLookupReturnsCouponRepresentationOnSuccess() {
		ExecutionResult<CouponEntity> createReadOK = ExecutionResultFactory.createReadOK(expectedCouponEntity);
		when(mockCouponsLookupStrategy.getCouponDetailsForPurchase(anyString(), anyString(), eq(DECODED_COUPON_ID)))
				.thenReturn(createReadOK);
		ArgumentCaptor<CouponEntity> actualEnhancedEntity = ArgumentCaptor.forClass(CouponEntity.class);
		when(mockCouponDetailsTransformer.transform(actualEnhancedEntity.capture(), eq(purchaseResourceState)))
				.thenReturn(couponResourceState);

		ExecutionResult<ResourceState<CouponEntity>> result = lookup.getCouponDetailsForPurchase(purchaseResourceState, COUPON_ID);

		assertExecutionResult(result)
				.isSuccessful()
				.data(couponResourceState);

		assertEquals(DECODED_PURCHASE_ID, actualEnhancedEntity.getValue().getParentId());
		assertEquals(PurchasesMediaTypes.PURCHASE.id(), actualEnhancedEntity.getValue().getParentType());
	}

	@Test
	public void testDetailsLookupReturnsFailureResultOnServerFailure() {
		when(mockCouponsLookupStrategy.getCouponDetailsForPurchase(anyString(), anyString(), eq(DECODED_COUPON_ID)))
				.thenReturn(ExecutionResultFactory.<CouponEntity>createServerError("Test error"));

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		lookup.getCouponDetailsForPurchase(purchaseResourceState, COUPON_ID);
	}

	@Test
	public void testDetailsLookupReturnsFailureResultOnLookupFailure() {
		when(mockCouponsLookupStrategy.getCouponDetailsForPurchase(anyString(), anyString(), eq(DECODED_COUPON_ID)))
				.thenReturn(ExecutionResultFactory.<CouponEntity>createNotFound("Test error"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		lookup.getCouponDetailsForPurchase(purchaseResourceState, COUPON_ID);
	}

	@Test
	public void testPurchaseCouponsLookupReturnsLinksRepresentationOnSuccess() {
		when(mockCouponsLookupStrategy.getCouponsForPurchase(anyString(), anyString()))
				.thenReturn(mockCollectionOfCouponIds);
		when(mockCouponLinksTransformer.transform(mockCollectionOfCouponIds, purchaseResourceState))
				.thenReturn(linksResourceState);

		Iterable<String> result = lookup.getCouponLinksForPurchase(purchaseResourceState);

		assertThat(result, equalTo(mockCollectionOfCouponIds));
	}
}

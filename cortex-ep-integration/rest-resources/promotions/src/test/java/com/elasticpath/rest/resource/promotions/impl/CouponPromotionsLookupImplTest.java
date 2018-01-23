/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.promotions.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.definition.coupons.CouponsMediaTypes.COUPON;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.coupons.CouponEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.purchases.PurchasesMediaTypes;
import com.elasticpath.rest.resource.promotions.integration.AppliedOrderCouponPromotionsLookupStrategy;
import com.elasticpath.rest.resource.promotions.integration.AppliedPurchaseCouponPromotionsLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.transform.TransformRfoToResourceState;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Test class for {@link CouponPromotionsLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class CouponPromotionsLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String PROMOTION_ID = "12345";
	private static final String COUPON_ID = "6789";
	private static final String CART_ORDER_ID = "cartOrderId";
	private static final String COUPON_SOURCE_URI = "coupons/other/stuff";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String PURCHASE_TYPE = PurchasesMediaTypes.PURCHASE.id();
	private static final String CART_ORDER_TYPE = OrdersMediaTypes.ORDER.id();
	private static final String TEST_ERROR = "test error";

	private final Collection<String> mockPromos = Arrays.asList(PROMOTION_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private AppliedPurchaseCouponPromotionsLookupStrategy mockPurchaseCouponPromotionsLookupStrategy;

	@Mock
	private AppliedOrderCouponPromotionsLookupStrategy mockAppliedOrderCouponPromotionsLookupStrategy;

	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, CouponEntity> mockAppliedPromotionsTransformer;

	@Mock
	private TransformRfoToResourceState<LinksEntity, Collection<String>, CouponEntity> mockPurchaseAppliedPromotionsTransformer;

	private CouponPromotionsLookupImpl couponPromotionsLookup;

	@Mock
	private ResourceState<LinksEntity> mockLinksRepresentation;

	/**
	 * Setup Mocks.
	 * Not using @InjectMocks, as Mockito has a Java 7 bug that causes confusion with parameters of same type.
	 */
	@Before
	public void setUp() {
		couponPromotionsLookup = new CouponPromotionsLookupImpl(
				mockPurchaseAppliedPromotionsTransformer,
				mockPurchaseCouponPromotionsLookupStrategy,
				mockAppliedOrderCouponPromotionsLookupStrategy,
				mockAppliedPromotionsTransformer);
	}

	@Test
	public void testServerErrorForGettingCouponsWithNoValidInnerType() {
		ResourceState<CouponEntity> coupon = createCouponRepresentation(COUPON_SOURCE_URI, StringUtils.EMPTY, StringUtils.EMPTY);

		ExecutionResult<ResourceState<LinksEntity>> executionResult = couponPromotionsLookup.getAppliedPromotionsForCoupon(coupon);

		AssertExecutionResult.assertExecutionResult(executionResult)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@Test
	public void testServerErrorForGettingPurchaseCoupons() {
		ResourceState<CouponEntity> coupon = createCouponRepresentation(COUPON_SOURCE_URI, PURCHASE_TYPE, PURCHASE_ID);
		when(mockPurchaseCouponPromotionsLookupStrategy.getAppliedPromotionsForCoupon(SCOPE, COUPON_ID, PURCHASE_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createServerError(TEST_ERROR));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		couponPromotionsLookup.getAppliedPromotionsForCoupon(coupon);
	}

	@Test
	public void testSuccessfulPurchaseCouponsRetrieval() {
		ResourceState<CouponEntity> coupon = createCouponRepresentation(COUPON_SOURCE_URI, PURCHASE_TYPE, PURCHASE_ID);
		when(mockPurchaseCouponPromotionsLookupStrategy.getAppliedPromotionsForCoupon(SCOPE, COUPON_ID, PURCHASE_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockPromos));
		when(mockPurchaseAppliedPromotionsTransformer.transform(mockPromos, coupon))
				.thenReturn(mockLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> executionResult = couponPromotionsLookup.getAppliedPromotionsForCoupon(coupon);

		AssertExecutionResult.assertExecutionResult(executionResult)
				.isSuccessful()
				.data(mockLinksRepresentation);
	}

	@Test
	public void testServerErrorForGettingCartOrderCoupons() {
		ResourceState<CouponEntity> coupon = createCouponRepresentation(COUPON_SOURCE_URI, CART_ORDER_TYPE, CART_ORDER_ID);
		when(mockAppliedOrderCouponPromotionsLookupStrategy.getAppliedPromotionsForCoupon(SCOPE, COUPON_ID, CART_ORDER_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createServerError(TEST_ERROR));
		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		couponPromotionsLookup.getAppliedPromotionsForCoupon(coupon);
	}

	@Test
	public void testSuccessfulCartOrderCouponsRetrieval() {
		ResourceState<CouponEntity> coupon = createCouponRepresentation(COUPON_SOURCE_URI, CART_ORDER_TYPE, CART_ORDER_ID);
		when(mockAppliedOrderCouponPromotionsLookupStrategy.getAppliedPromotionsForCoupon(SCOPE, COUPON_ID, CART_ORDER_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockPromos));
		when(mockAppliedPromotionsTransformer.transform(mockPromos, coupon))
				.thenReturn(mockLinksRepresentation);

		ExecutionResult<ResourceState<LinksEntity>> executionResult = couponPromotionsLookup.getAppliedPromotionsForCoupon(coupon);

		AssertExecutionResult.assertExecutionResult(executionResult)
				.isSuccessful()
				.data(mockLinksRepresentation);
	}

	private ResourceState<CouponEntity> createCouponRepresentation(final String sourceUri, final String parentType, final String parentId) {
		CouponEntity couponEntity = CouponEntity.builder()
				.withCouponId(COUPON_ID)
				.withParentId(parentId)
				.withParentType(parentType)
				.build();
		Self self = SelfFactory.createSelf(sourceUri, COUPON.id());

		return ResourceState.<CouponEntity>builder()
				.withEntity(couponEntity)
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}
}

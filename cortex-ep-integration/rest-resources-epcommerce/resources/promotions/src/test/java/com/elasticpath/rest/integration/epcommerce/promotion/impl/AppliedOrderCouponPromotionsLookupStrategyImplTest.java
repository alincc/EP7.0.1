/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;


import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.coupon.CouponRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Test for getting applied promotions for order coupons.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases") // tests are defined in the superclass
public class AppliedOrderCouponPromotionsLookupStrategyImplTest extends AbstractPromotionsLookupStrategyImplContractTest {

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private PromotionRepository promotionRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;

	@InjectMocks
	private AppliedOrderCouponPromotionsLookupStrategyImpl lookup;

	@Mock
	private CartOrder mockCartOrder;

	@Mock
	private ShoppingCart mockShoppingCart;

	@Mock
	private ShoppingCartPricingSnapshot mockPricingSnapshot;

	@Mock
	private Coupon mockCoupon;

	private final Collection<String> appliedPromotions = new ArrayList<>();

	@Override
	ExecutionResult<Collection<String>> getPromotions() {
		return lookup.getAppliedPromotionsForCoupon(SCOPE, DECODED_ID, DECODED_ID);
	}

	@Override
	Collection<String> setUpToReturnNoPromotions() {
		return setupSuccesfulPromotionsLookup();
	}

	@Override
	Collection<String> setUpToReturnOnePromotion() {
		appliedPromotions.add(PROMO_1);
		return setupSuccesfulPromotionsLookup();
	}

	@Override
	Collection<String> setUpToReturnMultiplePromotions() {
		appliedPromotions.add(PROMO_1);
		appliedPromotions.add(PROMO_2);
		return setupSuccesfulPromotionsLookup();
	}

	@Override
	void setUpToReturnNotFoundWhenGettingPromotions() {
		when(cartOrderRepository.getEnrichedShoppingCart(SCOPE, DECODED_ID, CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(ExecutionResultFactory.createNotFound("not found"));
	}

	private Collection<String> setupSuccesfulPromotionsLookup() {
		when(mockCartOrder.getGuid()).thenReturn(CART_ORDER_GUID);
		when(cartOrderRepository.getEnrichedShoppingCart(SCOPE, DECODED_ID, CartOrderRepository.FindCartOrder.BY_ORDER_GUID))
				.thenReturn(ExecutionResultFactory.createReadOK(mockShoppingCart));
		when(pricingSnapshotRepository.getShoppingCartPricingSnapshot(mockShoppingCart))
			.thenReturn(ExecutionResultFactory.createReadOK(mockPricingSnapshot));
		when(couponRepository.findByCouponCode(DECODED_ID)).thenReturn(ExecutionResultFactory.createReadOK(mockCoupon));

		when(promotionRepository.getAppliedPromotionsForCoupon(mockPricingSnapshot, mockCoupon)).thenReturn(appliedPromotions);

		return appliedPromotions;
	}

}

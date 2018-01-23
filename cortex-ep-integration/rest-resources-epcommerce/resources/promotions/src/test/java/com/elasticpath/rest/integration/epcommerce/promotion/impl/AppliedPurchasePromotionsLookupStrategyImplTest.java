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

import com.elasticpath.domain.order.Order;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;

/**
 * Looks up promotions associated with a coupon.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases") // tests are defined in the superclass
public class AppliedPurchasePromotionsLookupStrategyImplTest extends AbstractPromotionsLookupStrategyImplContractTest {

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private PromotionRepository promotionRepository;

	@InjectMocks
	public AppliedPurchasePromotionsLookupStrategyImpl lookup;

	@Mock
	private Order mockOrder;

	private final Collection<String> appliedPromotions = new ArrayList<>();

	@Override
	ExecutionResult<Collection<String>> getPromotions() {
		return lookup.getAppliedPromotionsForPurchase(SCOPE, DECODED_ID);
	}

	@Override
	Collection<String> setUpToReturnNoPromotions() {
		return setUpSuccessfulPromotionLookup();
	}

	@Override
	Collection<String> setUpToReturnOnePromotion() {
		appliedPromotions.add(PROMO_1);
		return setUpSuccessfulPromotionLookup();
	}

	@Override
	Collection<String> setUpToReturnMultiplePromotions() {
		appliedPromotions.add(PROMO_1);
		appliedPromotions.add(PROMO_2);
		return setUpSuccessfulPromotionLookup();
	}

	@Override
	void setUpToReturnNotFoundWhenGettingPromotions() {
		when(orderRepository.findByGuid(SCOPE, DECODED_ID)).thenReturn(ExecutionResultFactory.<Order>createNotFound("not found"));
	}

	private Collection<String> setUpSuccessfulPromotionLookup() {
		when(orderRepository.findByGuid(SCOPE, DECODED_ID)).thenReturn(ExecutionResultFactory.createReadOK(mockOrder));

		when(promotionRepository.getAppliedPromotionsForPurchase(mockOrder)).thenReturn(appliedPromotions);
		return appliedPromotions;
	}
}

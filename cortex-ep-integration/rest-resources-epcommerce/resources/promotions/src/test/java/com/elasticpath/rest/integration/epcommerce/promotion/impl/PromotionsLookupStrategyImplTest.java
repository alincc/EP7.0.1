/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integration.epcommerce.promotion.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.promotions.PromotionEntity;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;
import com.elasticpath.rest.test.AssertExecutionResult;

/**
 * Test class for {@link PromotionsLookupStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PromotionsLookupStrategyImplTest {

	private static final String USER_ID = "userid";
	private static final Locale TEST_LOCALE = Locale.ENGLISH;
	private static final String PROMOTION_ID = "promoId";
	private static final String SCOPE = "TEST_SCOPE";
	private static final String PURCHASE_ID = "PURCHASE_ID";

	@Mock
	private PromotionRepository promotionRepository;
	@Mock
	private AbstractDomainTransformer<Rule, PromotionEntity> promotionTransformer;
	@Mock
	private ResourceOperationContext resourceOperationContext;
	@Mock
	private AbstractDomainTransformer<AppliedRule, PromotionEntity> appliedPromotionTransformer;
	@Mock
	private OrderRepository orderRepository;
	private PromotionsLookupStrategyImpl lookupStrategy;

	@Before
	public void setUp() {
		Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(SCOPE, USER_ID, TEST_LOCALE);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
		lookupStrategy = new PromotionsLookupStrategyImpl(resourceOperationContext, promotionRepository, promotionTransformer,
				appliedPromotionTransformer, orderRepository);
	}


	@Test(expected = BrokenChainException.class)
	public void testGetPromotionForCouponFailureWhenNoneReturned() {
		when(promotionRepository.findByPromotionId(PROMOTION_ID)).thenReturn(ExecutionResultFactory.<Rule>createNotFound());

		lookupStrategy.getPromotionById(SCOPE, PROMOTION_ID);
	}

	@Test
	public void testGetPromotionForCouponSuccessWhenOneReturned() {
		Rule rule = mock(Rule.class);
		when(promotionRepository.findByPromotionId(PROMOTION_ID)).thenReturn(ExecutionResultFactory.createReadOK(rule));
		PromotionEntity expectedPromotionEntity = mock(PromotionEntity.class);
		when(promotionTransformer.transformToEntity(rule, TEST_LOCALE)).thenReturn(expectedPromotionEntity);

		ExecutionResult<PromotionEntity> result = lookupStrategy.getPromotionById(SCOPE, PROMOTION_ID);

		assertTrue(result.isSuccessful());
		assertEquals("The expected promotionEntity should have been returned", expectedPromotionEntity, result.getData());
	}

	@Test(expected = BrokenChainException.class)
	public void testGetPromotionForPurchaseFailureWhenNotFoundReturned() {
		when(orderRepository.findByGuid(SCOPE, PURCHASE_ID)).thenReturn(ExecutionResultFactory.<Order>createNotFound());

		lookupStrategy.getPromotionForPurchase(SCOPE, PROMOTION_ID, PURCHASE_ID);
	}

	@Test
	public void testGetPromotionForPurchaseSuccessWhenNoneMatching() {
		AppliedRule mockAppliedRule = mock(AppliedRule.class);
		when(mockAppliedRule.getGuid()).thenReturn("NOT MATCHING");
		Set<AppliedRule> appliedRules = new HashSet<>();
		appliedRules.add(mockAppliedRule);
		Order order = mock(Order.class);
		when(orderRepository.findByGuid(SCOPE, PURCHASE_ID)).thenReturn(ExecutionResultFactory.createReadOK(order));
		when(order.getAppliedRules()).thenReturn(appliedRules);

		ExecutionResult<PromotionEntity> result = lookupStrategy.getPromotionForPurchase(SCOPE, PROMOTION_ID, PURCHASE_ID);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testGetPromotionForPurchaseSuccessWhenNoneOnOrder() {
		Set<AppliedRule> appliedRules = new HashSet<>();
		Order order = mock(Order.class);
		when(orderRepository.findByGuid(SCOPE, PURCHASE_ID)).thenReturn(ExecutionResultFactory.createReadOK(order));
		when(order.getAppliedRules()).thenReturn(appliedRules);

		ExecutionResult<PromotionEntity> result = lookupStrategy.getPromotionForPurchase(SCOPE, PROMOTION_ID, PURCHASE_ID);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	@Test
	public void testGetPromotionForPurchaseSuccessWhenOneReturned() {
		AppliedRule mockAppliedRule = mock(AppliedRule.class);
		when(mockAppliedRule.getGuid()).thenReturn(PROMOTION_ID);
		Set<AppliedRule> appliedRules = new HashSet<>();
		appliedRules.add(mockAppliedRule);
		Order order = mock(Order.class);
		when(orderRepository.findByGuid(SCOPE, PURCHASE_ID)).thenReturn(ExecutionResultFactory.createReadOK(order));
		when(order.getAppliedRules()).thenReturn(appliedRules);
		PromotionEntity expectedPromotionEntity = mock(PromotionEntity.class);
		when(appliedPromotionTransformer.transformToEntity(mockAppliedRule)).thenReturn(expectedPromotionEntity);

		ExecutionResult<PromotionEntity> result = lookupStrategy.getPromotionForPurchase(SCOPE, PROMOTION_ID, PURCHASE_ID);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(expectedPromotionEntity);
	}
}

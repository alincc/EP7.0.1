/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.ExecutionResultChain;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.PromotionRuleMatcher;
import com.elasticpath.service.rules.RuleService;

/**
 * The facade for Coupon related operations.
 */
@Singleton
@Named("promotionRepository")
public class PromotionRepositoryImpl implements PromotionRepository {

	private static final Logger LOG = LoggerFactory.getLogger(PromotionRepositoryImpl.class);

	private final RuleService ruleService;
	private final PromotionRuleMatcher<Long, Rule> cartPromotionRuleMatcher;
	private final PromotionRuleMatcher<AppliedRule, AppliedRule> orderPromotionRuleMatcher;
	private final PriceRepository priceRepository;

	/**
	 * Look ma', a Constructor!
	 *
	 * @param ruleService               rule service
	 * @param cartPromotionRuleMatcher  the cart promotion rule matcher
	 * @param orderPromotionRuleMatcher the order promotion rule matcher
	 * @param priceRepository           the price repository
	 */
	@Inject
	public PromotionRepositoryImpl(
			@Named("ruleService")
			final RuleService ruleService,
			@Named("cartPromotionRuleMatcher")
			final PromotionRuleMatcher<Long, Rule> cartPromotionRuleMatcher,
			@Named("orderPromotionRuleMatcher")
			final PromotionRuleMatcher<AppliedRule, AppliedRule> orderPromotionRuleMatcher,
			@Named("priceRepository")
			final PriceRepository priceRepository) {

		this.ruleService = ruleService;
		this.cartPromotionRuleMatcher = cartPromotionRuleMatcher;
		this.orderPromotionRuleMatcher = orderPromotionRuleMatcher;
		this.priceRepository = priceRepository;
	}

	@Override
	@CacheResult
	public ExecutionResult<Rule> findByRuleId(final Long ruleId) {
		return new ExecutionResultChain() {
			protected ExecutionResult<?> build() {
				String ruleCode = Assign.ifNotNull(ruleService.findRuleCodeById(ruleId), OnFailure.returnNotFound());
				return findByPromotionId(ruleCode);
			}
		}.execute();
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedShippingPromotions(
			final ShoppingCartPricingSnapshot pricingSnapshot, final ShippingServiceLevel shippingServiceLevel) {
		AppliedShippingRulePredicate rulePredicate = new AppliedShippingRulePredicate(pricingSnapshot, shippingServiceLevel);
		AppliedPromotionRuleAwareShippingOptionAdapter shippingServiceLevelAdapter
				= new AppliedPromotionRuleAwareShippingOptionAdapter(pricingSnapshot, shippingServiceLevel);
		return cartPromotionRuleMatcher.findMatchingAppliedRules(shippingServiceLevelAdapter, rulePredicate);
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedPromotionsForCoupon(final ShoppingCartPricingSnapshot pricingSnapshot, final Coupon coupon) {
		CouponRulePredicate rulePredicate = new CouponRulePredicate(coupon);
		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter ruleAwareShoppingCart =
				new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		return cartPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareShoppingCart, rulePredicate);
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedPromotionsForCoupon(final Order order, final Coupon coupon) {
		CouponAppliedRulePredicate rulePredicate = new CouponAppliedRulePredicate(coupon);
		AppliedPromotionRuleAwareOrderAdapter ruleAwareOrder = new AppliedPromotionRuleAwareOrderAdapter(order);
		return orderPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareOrder, rulePredicate);
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedPromotionsForPurchase(final Order order) {
		PurchaseAppliedRulePredicate rulePredicate = new PurchaseAppliedRulePredicate();
		AppliedPromotionRuleAwareOrderAdapter ruleAwareOrder = new AppliedPromotionRuleAwareOrderAdapter(order);
		return orderPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareOrder, rulePredicate);
	}

	@Override
	@CacheResult
	public ExecutionResult<Rule> findByPromotionId(final String promotionId) {
		ExecutionResult<Rule> result;
		try {
			Rule rule = ruleService.findByRuleCode(promotionId);
			if (rule == null) {
				result = ExecutionResultFactory.createNotFound();
			} else {
				result = ExecutionResultFactory.createReadOK(rule);
			}
		} catch (EpServiceException exception) {
			LOG.warn("An exception occurred when searching for a Rule by promotion id: " + promotionId, exception);
			result = ExecutionResultFactory.createServerError("Server error occurred when searching for promotion rule");
		}
		return result;
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedPromotionsForItem(final String storeCode, final String itemId) {
		Collection<String> appliedPromotions = new ArrayList<>();
		Set<Long> ruleTracker = Assign.ifSuccessful(priceRepository.getLowestPriceRules(storeCode, itemId));
		for (Long ruleId : ruleTracker) {
			Rule rule = Assign.ifSuccessful(findByRuleId(ruleId));
			appliedPromotions.add(rule.getCode());
		}
		return appliedPromotions;
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedCartLineitemPromotions(final ShoppingCart shoppingCart,
																final ShoppingCartPricingSnapshot pricingSnapshot,
																final String lineItemId) {
		PromotionRecordContainer promotionRecordContainer = pricingSnapshot.getPromotionRecordContainer();
		Collection<Long> appliedRuleIds = promotionRecordContainer.getAppliedRulesByLineItem(lineItemId);
		CartLineItemRulePredicate rulePredicate = new CartLineItemRulePredicate(appliedRuleIds);
		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter ruleAwareShoppingCart =
				new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		return cartPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareShoppingCart, rulePredicate);
	}

	@Override
	@CacheResult
	public Collection<String> getAppliedCartPromotions(final ShoppingCartPricingSnapshot pricingSnapshot) {
		CartRulePredicate rulePredicate = new CartRulePredicate();
		AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter ruleAwareShoppingCart =
				new AppliedPromotionRuleAwareShoppingCartPricingSnapshotAdapter(pricingSnapshot);
		return cartPromotionRuleMatcher.findMatchingAppliedRules(ruleAwareShoppingCart, rulePredicate);
	}

}

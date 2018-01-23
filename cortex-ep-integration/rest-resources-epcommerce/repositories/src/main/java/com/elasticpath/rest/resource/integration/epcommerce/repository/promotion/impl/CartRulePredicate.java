/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl;

import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;

/**
 * This predicate matches a Rule as an applied promotion for a cart.
 */
public class CartRulePredicate implements RulePredicate<Rule> {

	@Override
	public boolean isSatisfied(final Rule rule) {
		for (RuleAction ruleAction : rule.getActions()) {
			if (DiscountType.CART_SUBTOTAL_DISCOUNT.equals(ruleAction.getDiscountType())) {
				return true;
			}
		}
		return false;
	}
}

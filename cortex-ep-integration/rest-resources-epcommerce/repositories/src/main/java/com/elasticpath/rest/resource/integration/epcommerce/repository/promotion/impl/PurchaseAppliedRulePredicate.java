/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl;

import com.elasticpath.domain.rules.AppliedRule;

/**
 * This predicates matches all Applied Rules.
 */
public class PurchaseAppliedRulePredicate implements RulePredicate<AppliedRule> {
	/**
	 * Every rule is satisfied for a purchase if it is applied.
	 * @param appliedRule applied rule.
	 * @return true since Applied Rule is a synonym for Purchase Applied Rule.
	 */
	@Override
	public boolean isSatisfied(final AppliedRule appliedRule) {
		return true;
	}
}

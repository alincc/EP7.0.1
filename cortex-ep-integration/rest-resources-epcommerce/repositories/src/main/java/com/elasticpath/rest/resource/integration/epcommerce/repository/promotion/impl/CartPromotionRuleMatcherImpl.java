/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.promotion.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.RuleService;

/**
 * Filters a collection of applied promotions to carts.
 */
@Singleton
@Named("cartPromotionRuleMatcher")
public class CartPromotionRuleMatcherImpl extends AbstractPromotionRuleMatcher<Long, Rule> {

	/**
	 * Constructor.
	 * @param ruleService The promotion rule service.
	 */
	@Inject
	public CartPromotionRuleMatcherImpl(
			@Named("ruleService")
			final RuleService ruleService) {
		super(ruleService);
	}

	@Override
	protected Rule getRule(final Long ruleId) {
		return super.getRuleById(ruleId);
	}

	@Override
	protected String getGuid(final Rule rule) {
		return rule.getCode();
	}
}